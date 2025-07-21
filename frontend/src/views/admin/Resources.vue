<template>
  <div class="admin-resources">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>资源管理</h2>
      <el-button class="add-btn" @click="showAddModal = true" type="primary">
        <i class="fas fa-plus"></i>
        添加资源
      </el-button>
    </div>

    <!-- 搜索和筛选 -->
    <div class="search-bar">
      <div class="search-input">
        <el-input v-model="searchKeyword" placeholder="搜索资源名称或描述..." @input="handleSearch" clearable>
          <template #prefix>
            <i class="fas fa-search"></i>
          </template>
        </el-input>
      </div>

      <el-select v-model="selectedCategory" @change="handleSearch" placeholder="全部分类"
        style="width: 120px; margin-right: 10px;">
        <el-option label="全部分类" value="" />
        <el-option v-for="category in categories" :key="category.id" :label="category.name" :value="category.id" />
      </el-select>

      <el-button class="refresh-btn" @click="loadResources" type="info">
        <i class="fas fa-refresh"></i>
        刷新
      </el-button>
    </div>

    <!-- 资源列表 -->
    <el-table :data="resources" border stripe style="width: 100%">
      <el-table-column prop="id" sortable label="ID" width="70" />
      <el-table-column prop="name" label="名称" />
      <el-table-column label="分类">
        <template #default="scope">
          {{ getCategoryName(scope.row.categoryId) }}
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="viewCount" label="浏览量" width="80" sortable />
      <el-table-column prop="sortOrder" label="排序/权重" width="90" sortable />
      <el-table-column label="状态" width="80">
        <template #default="scope">
          <span :class="['status', scope.row.status === 1 ? 'active' : 'inactive']">
            {{ scope.row.status === 1 ? '正常' : '禁用' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="createdTime" label="创建时间" width="120" sortable>
        <template #default="scope">
          {{ formatDate(scope.row.createdTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140">
        <template #default="scope">
          <div class="action-buttons">
            <el-button class="edit-btn" @click="editResource(scope.row)" type="primary" size="small">
              <i class="fas fa-edit"></i>
            </el-button>
            <el-button class="delete-btn" @click="deleteResource(scope.row.id)" type="danger" size="small">
              <i class="fas fa-trash"></i>
            </el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination" style="justify-content: flex-start; gap: 20px;">
      <div class="page-size-select">
        <label style="margin-right: 6px; color: #555;">每页显示</label>
        <el-select v-model="pageSize" @change="handlePageSizeChange" style="width: 80px;">
          <el-option label="5" :value="5" />
          <el-option label="10" :value="10" />
          <el-option label="15" :value="15" />
          <el-option label="20" :value="20" />
        </el-select>
        <span style="margin-left: 6px; color: #555;">条</span>
      </div>
      <el-button :disabled="currentPage === 1" @click="changePage(currentPage - 1)" size="small">
        上一页
      </el-button>
      <span>{{ currentPage }} / {{ totalPages }}</span>
      <el-button :disabled="currentPage === totalPages" @click="changePage(currentPage + 1)" size="small">
        下一页
      </el-button>
    </div>

    <!-- 添加/编辑资源弹窗 -->
    <el-dialog :title="showEditModal ? '编辑资源' : '添加资源'" v-model="showAddEditDialog" width="500px" @close="closeModal">
      <el-form @submit.prevent="submitResource" class="modal-form">
        <el-form-item label="资源名称">
          <el-input v-model="resourceForm.name" type="text" required />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="resourceForm.categoryId" placeholder="请选择分类" required>
            <el-option label="请选择分类" :value="null" />
            <el-option v-for="category in categories" :key="category.id" :label="category.name" :value="category.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="resourceForm.description" type="textarea" rows="3" />
        </el-form-item>
        <el-form-item label="资源链接">
          <el-input v-model="resourceForm.url" type="url" required />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="resourceForm.icon" type="text" placeholder="图标类名" />
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="resourceForm.tags" type="text" placeholder="用逗号分隔多个标签" />
        </el-form-item>
        <el-form-item label="排序/权重">
          <el-input v-model.number="resourceForm.sortOrder" type="number" min="0" placeholder="数值越大越靠前" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="resourceForm.status" placeholder="正常">
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="default" class="cancel-btn" @click="closeModal">取消</el-button>
          <el-button type="primary" class="submit-btn" native-type="submit">{{ showEditModal ? '更新' : '创建'
            }}</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { getResourceList, createResource, updateResource, deleteResource as deleteResourceApi } from '@/api/resource'
import { getCategories } from '@/api/category'
import { ElMessage } from 'element-plus'

interface Resource {
  id: number
  name: string
  description: string
  categoryId: number | null
  url: string
  icon: string
  tags: string
  viewCount: number
  status: number
  createdTime: string
  sortOrder: number
}

interface Category {
  id: number
  name: string
}

const resources = ref<Resource[]>([])
const categories = ref<Category[]>([])
const searchKeyword = ref('')
const selectedCategory = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const totalPages = ref(1)
const showAddModal = ref(false)
const showEditModal = ref(false)
const editingResource = ref<Resource | null>(null)

const resourceForm = ref({
  name: '',
  categoryId: null as number | null,
  description: '',
  url: '',
  icon: '',
  tags: '',
  status: 1,
  sortOrder: 0
})

const showAddEditDialog = computed({
  get: () => showAddModal.value || showEditModal.value,
  set: (val: boolean) => {
    if (!val) closeModal();
  }
});

const loadResources = async () => {
  try {
    const response = await getResourceList({
      current: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value,
      categoryId: selectedCategory.value ? Number(selectedCategory.value) : undefined
    })
    const res = response.data;
    if (res.code === 200) {
      resources.value = res.data.records;
      // 修复分页异常：totalPages最小为1
      let total = res.data.total;
      total = Number(total) || 0;
      totalPages.value = Math.max(1, Math.ceil(total / Number(pageSize.value)));
    }
  } catch (error) {
    console.error('加载资源列表失败:', error)
  }
}

const loadCategories = async () => {
  try {
    const response = await getCategories()
    if (response.data.code === 200) {
      categories.value = response.data.data
    }
  } catch (error) {
    console.error('加载分类列表失败:', error)
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadResources()
}

const changePage = (page: number) => {
  currentPage.value = page
  loadResources()
}

const handlePageSizeChange = () => {
  pageSize.value = Number(pageSize.value);
  currentPage.value = 1;
  loadResources();
}

const getCategoryName = (categoryId: number) => {
  const category = categories.value.find(c => c.id === categoryId)
  return category?.name || '未知分类'
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN')
}

const editResource = (resource: Resource) => {
  editingResource.value = resource
  resourceForm.value = {
    name: resource.name,
    categoryId: resource.categoryId ?? null,
    description: resource.description,
    url: resource.url,
    icon: resource.icon,
    tags: resource.tags,
    status: resource.status,
    sortOrder: resource.sortOrder ?? 0
  }
  showEditModal.value = true
}

const deleteResource = async (id: number) => {
  if (!confirm('确定要删除这个资源吗？')) return

  try {
    await deleteResourceApi(id)
    await loadResources()
  } catch (error) {
    console.error('删除资源失败:', error)
  }
}

const submitResource = async () => {
  try {
    // categoryId 必须为 number，未选时阻止提交
    if (resourceForm.value.categoryId === null) {
      ElMessage.error('请选择分类');
      return;
    }
    const submitData = { ...resourceForm.value, categoryId: resourceForm.value.categoryId };
    if (showEditModal.value && editingResource.value) {
      await updateResource(editingResource.value.id, submitData)
    } else {
      await createResource(submitData)
    }

    closeModal()
    await loadResources()
  } catch (error) {
    console.error('保存资源失败:', error)
  }
}

const closeModal = () => {
  showAddModal.value = false
  showEditModal.value = false
  editingResource.value = null
  resourceForm.value = {
    name: '',
    categoryId: null,
    description: '',
    url: '',
    icon: '',
    tags: '',
    status: 1,
    sortOrder: 0
  }
}

onMounted(() => {
  loadResources()
  loadCategories()
})
</script>

<style scoped>
.admin-resources {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #2c3e50;
}

.add-btn {
  background: #3498db;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
}

.search-bar {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  align-items: center;
}

.search-input {
  position: relative;
  flex: 1;
}

.search-input input {
  width: 100%;
  padding: 10px 40px 10px 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.search-input i {
  position: absolute;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
}

.search-bar select {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  min-width: 150px;
}

.refresh-btn {
  background: #6c757d;
  color: white;
  border: none;
  padding: 10px 15px;
  border-radius: 4px;
  cursor: pointer;
}

.resources-table {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

table {
  width: 100%;
  border-collapse: collapse;
}

th,
td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

th {
  background: #f8f9fa;
  font-weight: 600;
  color: #2c3e50;
}

.status {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status.active {
  background: #d4edda;
  color: #155724;
}

.status.inactive {
  background: #f8d7da;
  color: #721c24;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.edit-btn,
.delete-btn {
  background: none;
  border: none;
  padding: 6px;
  border-radius: 4px;
  cursor: pointer;
}

.edit-btn {
  color: #3498db;
}

.delete-btn {
  color: #e74c3c;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  margin-top: 20px;
}

.pagination button {
  background: #3498db;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
}

.pagination button:disabled {
  background: #bdc3c7;
  cursor: not-allowed;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: white;
  border-radius: 8px;
  width: 500px;
  max-width: 90vw;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  color: #999;
}

.modal-form {
  padding: 20px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
  color: #2c3e50;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.form-group textarea {
  resize: vertical;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.cancel-btn {
  background: #6c757d;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
}

.submit-btn {
  background: #3498db;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
}

.page-size-select {
  display: flex;
  align-items: center;
  gap: 2px;
}

.page-size-select select {
  padding: 6px 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}
</style>
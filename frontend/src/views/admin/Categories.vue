<template>
  <div class="admin-categories">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>分类管理</h2>
      <el-button class="add-btn" @click="openAddModal()" type="primary">
        <i class="fas fa-plus"></i>
        添加分类
      </el-button>
    </div>

    <!-- 分类树形表格 -->
    <el-table
      :data="categoryTree"
      row-key="id"
      border
      default-expand-all
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      style="width: 100%"
    >
      <el-table-column prop="name" label="分类名称" min-width="180">
        <template #default="scope">
          <i :class="scope.row.icon || 'fas fa-folder'" style="margin-right:8px;"></i>
          {{ scope.row.name }}
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" min-width="200" />
      <el-table-column prop="resourceCount" label="资源数" width="80" />
      <el-table-column prop="sortOrder" label="排序" width="80" />
      <el-table-column label="操作" width="140">
        <template #default="scope">
          <el-button size="small" type="primary" @click="editCategory(scope.row)"><i class="fas fa-edit"></i></el-button>
          <el-button size="small" type="danger" @click="deleteCategory(scope.row.id)"><i class="fas fa-trash"></i></el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加/编辑分类弹窗 -->
    <el-dialog :title="showEditModal ? '编辑分类' : '添加分类'" v-model="showAddEditDialog" width="500px" @close="closeModal">
      <el-form @submit.prevent="submitCategory" class="modal-form">
        <el-form-item label="分类名称">
          <el-input v-model="categoryForm.name" type="text" required />
        </el-form-item>
        <el-form-item label="分类编码">
          <el-input v-model="categoryForm.code" type="text" required placeholder="唯一英文编码，如 anime-online" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="categoryForm.description" type="textarea" rows="3" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="categoryForm.icon" type="text" placeholder="图标类名，如: fas fa-folder" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input v-model="categoryForm.sortOrder" type="number" min="0" />
        </el-form-item>
        <el-form-item label="父级分区">
          <el-select v-model="categoryForm.parentId" placeholder="请选择父级分区">
            <el-option :value="0" label="无（主分区）" />
            <el-option v-for="cat in mainCategories" :key="cat.id" :value="cat.id" :label="cat.name" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="default" class="cancel-btn" @click="closeModal">取消</el-button>
          <el-button type="primary" class="submit-btn" native-type="submit">{{ showEditModal ? '更新' : '创建' }}</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { getCategories, createCategory, updateCategory, deleteCategory as deleteCategoryApi } from '@/api/category'

interface Category {
  id: number
  name: string
  code: string
  description: string
  icon: string
  sortOrder: number
  parentId: number
  resourceCount?: number
  children?: Category[]
}

const categories = ref<Category[]>([])
const categoryTree = computed(() => buildCategoryTree(categories.value))
const mainCategories = computed(() => categories.value.filter(c => c.parentId === 0))

const showAddModal = ref(false)
const showEditModal = ref(false)
const editingCategory = ref<Category | null>(null)

const categoryForm = ref({
  name: '',
  code: '',
  description: '',
  icon: '',
  sortOrder: 0,
  parentId: 0
})

const showAddEditDialog = computed({
  get: () => showAddModal.value || showEditModal.value,
  set: (val: boolean) => {
    if (!val) closeModal();
  }
});

function buildCategoryTree(list: Category[]): Category[] {
  const map = new Map<number, Category & { children: Category[] }>()
  const roots: Category[] = []
  list.forEach(cat => map.set(cat.id, { ...cat, children: [] }))
  map.forEach(cat => {
    if (cat.parentId && cat.parentId !== 0 && map.has(cat.parentId)) {
      map.get(cat.parentId)!.children.push(cat)
    } else {
      roots.push(cat)
    }
  })
  return roots
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

function openAddModal(parentId = 0) {
  editingCategory.value = null
  categoryForm.value = {
    name: '',
    code: '',
    description: '',
    icon: '',
    sortOrder: 0,
    parentId
  }
  showAddModal.value = true
}

const editCategory = (category: Category) => {
  editingCategory.value = category
  categoryForm.value = {
    name: category.name,
    code: category.code,
    description: category.description,
    icon: category.icon,
    sortOrder: category.sortOrder,
    parentId: category.parentId || 0
  }
  showEditModal.value = true
}

const deleteCategory = async (id: number) => {
  if (!confirm('确定要删除这个分类吗？删除后该分类下的子分区和资源将无法显示。')) return
  try {
    await deleteCategoryApi(id)
    await loadCategories()
  } catch (error) {
    console.error('删除分类失败:', error)
  }
}

const submitCategory = async () => {
  try {
    if (showEditModal.value && editingCategory.value) {
      await updateCategory(editingCategory.value.id, categoryForm.value)
    } else {
      await createCategory(categoryForm.value)
    }
    closeModal()
    await loadCategories()
  } catch (error) {
    console.error('保存分类失败:', error)
  }
}

const closeModal = () => {
  showAddModal.value = false
  showEditModal.value = false
  editingCategory.value = null
  categoryForm.value = {
    name: '',
    code: '',
    description: '',
    icon: '',
    sortOrder: 0,
    parentId: 0
  }
}

onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.admin-categories {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
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

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.category-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.category-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.15);
}

.category-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.category-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: #3498db;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
}

.category-actions {
  display: flex;
  gap: 8px;
}

.edit-btn, .delete-btn {
  background: none;
  border: none;
  padding: 6px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.edit-btn {
  color: #3498db;
}

.delete-btn {
  color: #e74c3c;
}

.category-content h3 {
  margin: 0 0 10px 0;
  color: #2c3e50;
  font-size: 18px;
}

.category-content p {
  margin: 0 0 15px 0;
  color: #7f8c8d;
  line-height: 1.5;
}

.category-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #95a5a6;
}

.resource-count {
  background: #e8f4fd;
  color: #3498db;
  padding: 2px 8px;
  border-radius: 12px;
}

.sort-order {
  background: #f8f9fa;
  padding: 2px 8px;
  border-radius: 12px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
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

@media (max-width: 768px) {
  .categories-grid {
    grid-template-columns: 1fr;
  }
}
</style> 
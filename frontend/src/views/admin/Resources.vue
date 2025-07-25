<template>
  <div class="admin-resources">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>资源管理</h2>
      <el-button class="add-btn" @click="openAddModal" type="primary">
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
      <el-button class="refresh-btn" @click="handleRefreshAndCheck" type="info">
        <i class="fas fa-refresh"></i>
        刷新并检测状态
      </el-button>
    </div>

    <!-- 树形资源表格，懒加载模式 -->
    <el-table :data="tableData" style="width: 100%" :row-key="getRowKey" border lazy :load="loadNode"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }" ref="resourceTable">
      <el-table-column prop="name" label="名称" min-width="180">
        <template #default="scope">
          <template v-if="scope.row.type === 'category'">
            <i :class="scope.row.icon || 'fas fa-folder'" style="margin-right:8px;"></i>
            <b>{{ scope.row.name }}</b>
          </template>
          <template v-else>
            {{ scope.row.name }}
          </template>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" min-width="200" />
      <el-table-column label="浏览量" width="80">
        <template #default="scope">
          <template v-if="scope.row.type === 'resource'">
            {{ scope.row.viewCount }}
          </template>
        </template>
      </el-table-column>
      <el-table-column label="排序/权重" width="90">
        <template #default="scope">
          <template v-if="scope.row.type === 'resource'">
            {{ scope.row.sortOrder }}
          </template>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <template v-if="scope.row.type === 'resource'">
          <span :class="['status', scope.row.status === 1 ? 'active' : 'inactive']">
              {{ scope.row.status === 1 ? '正常' : '无法访问' }}
          </span>
          </template>
        </template>
      </el-table-column>
      <el-table-column prop="createdTime" label="创建时间" width="120" sortable>
        <template #default="scope">
          <span v-if="scope.row.createdTime">{{ formatDate(scope.row.createdTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140">
        <template #default="scope">
          <template v-if="scope.row.type === 'resource'">
          <div class="action-buttons">
            <el-button class="edit-btn" @click="editResource(scope.row)" type="primary" size="small">
              <i class="fas fa-edit"></i>
            </el-button>
            <el-button class="delete-btn" @click="deleteResource(scope.row.id)" type="danger" size="small">
              <i class="fas fa-trash"></i>
            </el-button>
          </div>
          </template>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件已移除 -->

    <!-- 添加/编辑资源弹窗 -->
    <el-dialog :title="showEditModal ? '编辑资源' : '添加资源'" v-model="showAddEditDialog" width="500px" @close="closeModal">
      <el-form @submit.prevent="submitResource" class="modal-form">
        <el-form-item label="资源名称">
          <el-input v-model="resourceForm.name" type="text" required />
        </el-form-item>
        <el-form-item label="分类">
          <el-tree-select v-model="resourceForm.categoryId" :data="categoryTreeSelectData" :render-after-expand="false"
            :props="{ label: 'label', children: 'children', value: 'value' }" placeholder="请选择分类" style="width: 100%"
            check-strictly clearable />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="resourceForm.description" type="textarea" :rows="3" />
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
            <el-option label="无法访问" :value="0" />
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
import { ref, onMounted, computed, nextTick } from 'vue'
import { getResourceList, createResource, updateResource, deleteResource as deleteResourceApi, checkAllResourcesStatus } from '@/api/resource'
import { getCategories } from '@/api/category'
import { ElMessage, ElNotification } from 'element-plus'

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
  type?: 'resource'
}

interface Category {
  id: number
  name: string
  description: string
  icon: string
  sortOrder: number
  parentId: number
  children?: (Category | Resource)[]
  type?: 'category'
}

const resources = ref<Resource[]>([])
const categories = ref<Category[]>([])
const tableData = ref<(Category | Resource)[]>([])
const searchKeyword = ref('')
const selectedCategory = ref('')
// 移除分页相关的 ref
// const currentPage = ref(1)
// const pageSize = ref(10)
// const totalPages = ref(1)
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

const resourceTable = ref();
const nodeResolveMap = new Map<number, { row: Category, treeNode: any, resolve: (data: (Category | Resource)[]) => void }>();

// 恢复 buildCategoryResourceTree，并增强循环检测
function buildCategoryResourceTree(categories: Category[], resources: Resource[]): (Category | Resource)[] {
  const nodes = new Map<number, any>();
  const roots: any[] = [];

  // 1. Initialize nodes for all categories
  categories.forEach(cat => {
    nodes.set(cat.id, { ...cat, children: [], type: 'category' });
  });

  // 2. Build the category hierarchy
  categories.forEach(cat => {
    if (cat.parentId && cat.parentId !== 0 && nodes.has(cat.parentId)) {
      const parentNode = nodes.get(cat.parentId);
      const childNode = nodes.get(cat.id);
      if (parentNode && childNode) {
        parentNode.children.push(childNode);
      }
    }
  });

  // 3. Attach resources to their categories
  resources.forEach(res => {
    if (res.categoryId && nodes.has(res.categoryId)) {
      const parentCategory = nodes.get(res.categoryId);
      if (parentCategory) {
        // Resources are always leaf nodes
        parentCategory.children.push({ ...res, type: 'resource', hasChildren: false });
      }
    }
  });

  // 4. Final pass to set hasChildren for categories and collect roots
  nodes.forEach(node => {
    if (node.type === 'category') {
      node.hasChildren = node.children.length > 0;
    }
    if (!node.parentId || node.parentId === 0) {
      roots.push(node);
    }
  });

  return roots;
}

const getRowKey = (row: Category | Resource) => {
  // Combine type and id to ensure a unique key for both categories and resources
  return `${row.type}-${row.id}`;
};

// 懒加载树表格：只加载顶级分类，点击展开时再加载子分类和资源
const loadTopLevelData = async () => {
  try {
    // 只查顶级分类，保证hasChildren字段正确
    const response = await getCategories({ parentId: 0 });
    tableData.value = (response.data.data || []).map((cat: Category) => ({
      ...cat,
      type: 'category'
    }));
    // 弹窗下拉树依然需要全量分类
    const allCatsRes = await getCategories();
    categories.value = allCatsRes.data.data || [];
  } catch (error) {
    console.error('Failed to load top level data:', error);
  }
};

const loadNode = async (row: Category, treeNode: unknown, resolve: (data: (Category | Resource)[]) => void) => {
  // 缓存 resolve 参数，便于后续刷新
  nodeResolveMap.set(row.id, { row, treeNode, resolve });
  if (row.type === 'category') {
    try {
      const [subCategoriesRes, resourcesRes] = await Promise.all([
        getCategories({ parentId: row.id }),
        getResourceList({ categoryId: row.id })
      ]);

      const subCategories = (subCategoriesRes.data.data || []).map((cat: Category) => ({
        ...cat,
        type: 'category',
        // hasChildren 字段由后端返回
      }));

      const resources = (resourcesRes.data.data || []).map((res: Resource) => ({
        ...res,
        type: 'resource',
        hasChildren: false // 资源永远是叶子节点
      }));

      const data = [...subCategories, ...resources];
      if (data.length > 0) {
        resolve(data);
      } else {
        // 若无数据，手动置空子节点，防止脏数据
        if (resourceTable.value) {
          resourceTable.value.store.states.lazyTreeNodeMap.value[row.id] = [];
        }
        resolve([]);
      }
    } catch (error) {
      console.error('Failed to load node:', error);
      resolve([]);
    }
  }
};

// 刷新指定父节点的子节点数据
const refreshNode = (parentId: number) => {
  const map = nodeResolveMap.get(parentId);
  if (map) {
    loadNode(map.row, map.treeNode, map.resolve);
  }
};

const loadAllData = async () => {
  try {
    const [categoriesRes, resourcesRes] = await Promise.all([
      getCategories(),
      // admin/resources页面我们获取全量数据，不分页
      getResourceList({ keyword: searchKeyword.value, categoryId: selectedCategory.value ? Number(selectedCategory.value) : undefined })
    ]);

    if (categoriesRes.data.code === 200 && resourcesRes.data.code === 200) {
      categories.value = categoriesRes.data.data;
      // 智能判断返回的是否是分页数据
      const resourceData = resourcesRes.data.data;
      resources.value = Array.isArray(resourceData) ? resourceData : resourceData.records || [];
      // treeData.value = buildCategoryResourceTree(categories.value, resources.value); // This line is no longer needed
    }
  } catch (error) {
    console.error('Failed to load data:', error);
  }
};


const handleRefreshAndCheck = async () => {
  try {
    const res = await checkAllResourcesStatus()
    ElNotification({
      title: '成功',
      message: res.data.message || '已发送检测指令，任务正在后台执行，请稍后查看结果。',
      type: 'success',
    });
    loadAllData(); // Refresh data after check
  } catch (error) {
    ElNotification({
      title: '失败',
      message: '触发检测失败！',
      type: 'error',
    });
  }
}

const handleSearch = () => {
  loadAllData();
}

// 移除 changePage 和 handlePageSizeChange 方法

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
    await loadAllData()
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

    // 先保存 parentId，再重置表单
    const parentId = resourceForm.value.categoryId;
    closeModal();
    refreshNode(parentId);
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

const openAddModal = async () => {
  showAddModal.value = true;
  // 获取所有分类（不带 parentId，拿到完整树）
  const response = await getCategories();
  if (response.data.code === 200) {
    categories.value = response.data.data;
  }
}

// 构建 el-tree-select 需要的数据结构
const categoryTreeSelectData = computed(() => {
  const map = new Map<number, any>()
  const roots: any[] = []
  categories.value.forEach(cat => {
    map.set(cat.id, {
      value: cat.id,
      label: cat.name,
      children: []
    })
  })
  categories.value.forEach(cat => {
    if (cat.parentId && map.has(cat.parentId)) {
      map.get(cat.parentId).children.push(map.get(cat.id))
    } else {
      roots.push(map.get(cat.id))
    }
  })
  return roots
})

onMounted(() => {
  loadTopLevelData();
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
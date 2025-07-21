<template>
  <div class="admin-users">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>用户管理</h2>
      <div class="header-actions">
        <el-button class="refresh-btn" @click="loadUsers">
          <i class="fas fa-refresh"></i>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <div class="search-input">
        <el-input 
          v-model="searchKeyword" 
          placeholder="搜索用户名或邮箱..."
          @input="handleSearch"
          clearable
        >
          <template #prefix>
            <i class="fas fa-search"></i>
          </template>
        </el-input>
      </div>
      
      <el-select v-model="selectedRole" @change="handleSearch" placeholder="全部角色" style="width: 120px; margin-right: 10px;">
        <el-option label="全部角色" value="" />
        <el-option label="管理员" value="admin" />
        <el-option label="普通用户" value="user" />
      </el-select>
      
      <el-select v-model="selectedStatus" @change="handleSearch" placeholder="全部状态" style="width: 120px;">
        <el-option label="全部状态" value="" />
        <el-option label="正常" value="1" />
        <el-option label="禁用" value="0" />
      </el-select>
    </div>

    <!-- 用户列表 -->
    <el-table :data="users" border stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column label="角色">
        <template #default="scope">
          <span :class="['role-badge', (scope.row.role === 'admin' || scope.row.role === 'ADMIN') ? 'admin' : 'user']">
            {{ (scope.row.role === 'admin' || scope.row.role === 'ADMIN') ? '管理员' : '普通用户' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="状态">
        <template #default="scope">
          <span :class="['status', scope.row.status === 1 ? 'active' : 'inactive']">
            {{ scope.row.status === 1 ? '正常' : '禁用' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="createdTime" label="注册时间">
        <template #default="scope">
          {{ formatDate(scope.row.createdTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="lastLoginTime" label="最后登录">
        <template #default="scope">
          {{ formatDate(scope.row.lastLoginTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="260">
        <template #default="scope">
          <div class="action-buttons">
            <el-button v-if="scope.row.status === 1" class="disable-btn" @click="toggleUserStatus(scope.row.id, 0)" type="warning" size="small">禁用</el-button>
            <el-button v-else class="enable-btn" @click="toggleUserStatus(scope.row.id, 1)" type="success" size="small">启用</el-button>
            <el-button v-if="scope.row.role === 'user'" class="promote-btn" @click="promoteUser(scope.row.id)" type="primary" size="small">设为管理员</el-button>
            <el-button v-else class="demote-btn" @click="demoteUser(scope.row.id)" type="info" size="small">取消管理员</el-button>
            <el-button class="delete-btn" @click="deleteUserHandler(scope.row.id)" type="danger" size="small">删除</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-button 
        :disabled="currentPage === 1" 
        @click="changePage(currentPage - 1)"
        size="small"
      >
        上一页
      </el-button>
      <span>{{ currentPage }} / {{ totalPages }}</span>
      <el-button 
        :disabled="currentPage === totalPages" 
        @click="changePage(currentPage + 1)"
        size="small"
      >
        下一页
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getUserList, updateUserStatus, updateUserRole, deleteUser, type User } from '@/api/admin'

const users = ref<User[]>([])
const searchKeyword = ref('')
const selectedRole = ref('')
const selectedStatus = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const totalPages = ref(1)

const loadUsers = async () => {
  try {
    const params = { current: currentPage.value, size: pageSize.value, keyword: searchKeyword.value, role: selectedRole.value, status: selectedStatus.value ? Number(selectedStatus.value) : undefined };
    const response = await getUserList(params)
    if (response.data.code === 200 && response.data.data) {
      users.value = response.data.data.records || []
      totalPages.value = response.data.data.pages || 1
    }
  } catch (error) {
    console.error('加载用户列表失败:', error)
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadUsers()
}

const changePage = (page: number) => {
  currentPage.value = page
  loadUsers()
}

const formatDate = (dateString: string) => {
  if (!dateString) return '从未登录'
  return new Date(dateString).toLocaleDateString('zh-CN')
}

const toggleUserStatus = async (userId: number, status: number) => {
  const action = status === 1 ? '启用' : '禁用'
  if (!confirm(`确定要${action}这个用户吗？`)) return
  
  try {
    await updateUserStatus(userId, status)
    await loadUsers()
  } catch (error) {
    console.error('更新用户状态失败:', error)
  }
}

const promoteUser = async (userId: number) => {
  if (!confirm('确定要将此用户设为管理员吗？')) return
  
  try {
    await updateUserRole(userId, 'ADMIN')
    await loadUsers()
  } catch (error) {
    console.error('提升用户权限失败:', error)
  }
}

const demoteUser = async (userId: number) => {
  if (!confirm('确定要取消此用户的管理员权限吗？')) return
  
  try {
    await updateUserRole(userId, 'USER')
    await loadUsers()
  } catch (error) {
    console.error('降低用户权限失败:', error)
  }
}

const deleteUserHandler = async (userId: number) => {
  if (!confirm('确定要删除这个用户吗？此操作不可恢复。')) return
  
  try {
    await deleteUser(userId)
    await loadUsers()
  } catch (error) {
    console.error('删除用户失败:', error)
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.admin-users {
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

.header-actions {
  display: flex;
  gap: 10px;
}

.refresh-btn {
  background: #6c757d;
  color: white;
  border: none;
  padding: 10px 15px;
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
  min-width: 120px;
}

.page-content, .main-content {
  overflow-x: auto;
  width: 100%;
  box-sizing: border-box;
}

.el-table {
  max-width: 100%;
}

.users-table {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

th {
  background: #f8f9fa;
  font-weight: 600;
  color: #2c3e50;
}

.role-badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.role-badge.admin {
  background: #e8f4fd;
  color: #3498db;
}

.role-badge.user {
  background: #f8f9fa;
  color: #6c757d;
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
  flex-wrap: wrap;
}

.disable-btn, .enable-btn, .promote-btn, .demote-btn, .delete-btn {
  background: none;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  border: 1px solid;
}

.disable-btn {
  color: #e74c3c;
  border-color: #e74c3c;
}

.enable-btn {
  color: #27ae60;
  border-color: #27ae60;
}

.promote-btn {
  color: #f39c12;
  border-color: #f39c12;
}

.demote-btn {
  color: #6c757d;
  border-color: #6c757d;
}

.delete-btn {
  color: #dc3545;
  border-color: #dc3545;
}

.pagination {
  display: flex;
  justify-content: flex-start;
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

@media (max-width: 768px) {
  .search-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .action-buttons {
    flex-direction: column;
  }
}
</style> 
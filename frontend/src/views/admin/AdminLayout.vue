<template>
  <div class="admin-layout">
    <!-- 侧边栏（自定义实现，保留原有风格） -->
    <div class="sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="sidebar-header">
        <h2 v-if="!sidebarCollapsed">管理后台</h2>
        <h2 v-else>后台</h2>
        <el-button class="collapse-btn" @click="toggleSidebar" type="info" size="small">
          <i class="fas fa-bars"></i>
        </el-button>
      </div>
      <nav class="sidebar-nav">
        <router-link 
          to="/admin/dashboard" 
          class="nav-item"
          :class="{ active: $route.path === '/admin/dashboard' }"
        >
          <i class="fas fa-tachometer-alt"></i>
          <span v-if="!sidebarCollapsed">仪表盘</span>
        </router-link>
        <router-link 
          to="/admin/resources" 
          class="nav-item"
          :class="{ active: $route.path === '/admin/resources' }"
        >
          <i class="fas fa-file-alt"></i>
          <span v-if="!sidebarCollapsed">资源管理</span>
        </router-link>
        <router-link 
          to="/admin/categories" 
          class="nav-item"
          :class="{ active: $route.path === '/admin/categories' }"
        >
          <i class="fas fa-folder"></i>
          <span v-if="!sidebarCollapsed">分类管理</span>
        </router-link>
        <router-link 
          to="/admin/users" 
          class="nav-item"
          :class="{ active: $route.path === '/admin/users' }"
        >
          <i class="fas fa-users"></i>
          <span v-if="!sidebarCollapsed">用户管理</span>
        </router-link>
      </nav>
      <div class="sidebar-logout">
        <el-button class="logout-btn" @click="logout" type="danger" size="small">
          <i class="fas fa-sign-out-alt"></i>
          <span v-if="!sidebarCollapsed">退出</span>
        </el-button>
      </div>
    </div>
    <!-- 主内容区域 -->
    <div class="main-content">
      <main class="page-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const sidebarCollapsed = ref(false)

const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value
}

const pageTitle = computed(() => {
  const routeMap: Record<string, string> = {
    '/admin/dashboard': '仪表盘',
    '/admin/resources': '资源管理',
    '/admin/categories': '分类管理',
    '/admin/users': '用户管理'
  }
  return routeMap[route.path] || '管理后台'
})

const logout = async () => {
  await userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  height: 100vh;
}

.sidebar {
  width: 250px;
  background: #2c3e50;
  color: white;
  transition: width 0.3s ease;
  display: flex;
  flex-direction: column;
}

.sidebar.collapsed {
  width: 60px;
  transition: width 0.3s ease;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid #34495e;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sidebar-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.collapse-btn {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  font-size: 16px;
}

.sidebar-nav {
  flex: 1;
  padding: 20px 0;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  color: #bdc3c7;
  text-decoration: none;
  transition: all 0.3s ease;
}

.nav-item:hover {
  background: #34495e;
  color: white;
}

.nav-item.active {
  background: #3498db;
  color: white;
}

.nav-item i {
  margin-right: 10px;
  width: 20px;
  text-align: center;
}

.sidebar-logout {
  padding: 20px;
  border-top: 1px solid #34495e;
}

.logout-btn {
  width: 100%;
  background: #dc3545;
  color: white;
  border: none;
  padding: 10px 0;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 16px;
  transition: background-color 0.3s ease;
}

.logout-btn:hover {
  background: #c82333;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #f8f9fa;
}

.top-header {
  background: white;
  padding: 15px 30px;
  border-bottom: 1px solid #e9ecef;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.menu-btn {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  color: #6c757d;
}

.header-left h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-info span {
  color: #6c757d;
  font-weight: 500;
}

.logout-btn {
  background: #dc3545;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: background-color 0.3s ease;
}

.logout-btn:hover {
  background: #c82333;
}

.page-content {
  flex: 1;
  padding: 30px;
  overflow-y: auto;
}

@media (max-width: 768px) {
  .sidebar,
  .sidebar.collapsed {
    width: 200px;
    min-width: 60px;
    max-width: 100vw;
    transition: width 0.3s ease, transform 0.3s;
  }
  .sidebar {
    transform: translateX(-100%);
  }
  .sidebar.collapsed {
    transform: translateX(0);
    width: 60px;
  }
  .main-content {
    margin-left: 0;
  }
}
</style> 
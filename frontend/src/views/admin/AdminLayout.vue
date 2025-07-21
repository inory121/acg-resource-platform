<template>
  <div class="admin-layout">

    <!-- 侧边栏 -->
    <div class="admin-sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="sidebar-header">
        <h2 v-if="!sidebarCollapsed">管理后台</h2>
        <h2 v-else>后台</h2>
        <el-button class="collapse-btn" @click="toggleSidebar" type="info" size="small">
          <i class="fas fa-bars"></i>
        </el-button>
      </div>
      <nav class="sidebar-nav">
        <router-link to="/admin/dashboard" class="nav-item" :class="{ active: $route.path === '/admin/dashboard' }">
          <i class="fas fa-tachometer-alt"></i>
          <span v-if="!sidebarCollapsed">仪表盘</span>
        </router-link>
        <router-link to="/admin/resources" class="nav-item" :class="{ active: $route.path === '/admin/resources' }">
          <i class="fas fa-file-alt"></i>
          <span v-if="!sidebarCollapsed">资源管理</span>
        </router-link>
        <router-link to="/admin/categories" class="nav-item" :class="{ active: $route.path === '/admin/categories' }">
          <i class="fas fa-folder"></i>
          <span v-if="!sidebarCollapsed">分类管理</span>
        </router-link>
        <router-link to="/admin/users" class="nav-item" :class="{ active: $route.path === '/admin/users' }">
          <i class="fas fa-users"></i>
          <span v-if="!sidebarCollapsed">用户管理</span>
        </router-link>
      </nav>
      <div class="sidebar-footer">
        <el-button class="footer-btn" @click="goHome" type="primary" size="large">
          <i class="fas fa-home"></i>
          <span v-if="!sidebarCollapsed">首页</span>
        </el-button>
        <el-button class="footer-btn" @click="logout" type="danger" size="large">
          <i class="fas fa-sign-out-alt"></i>
          <span v-if="!sidebarCollapsed">退出</span>
        </el-button>
      </div>
    </div>
    <!-- 主内容区域 -->
    <div class="admin-content-wrapper">
      <!-- 主题切换按钮 -->
      <div class="theme-toggle-btn">
        <el-button @click="toggleTheme" circle >
          <i :class="isDark ? 'fas fa-sun' : 'fas fa-moon'"></i>
        </el-button>
      </div>
      <main class="page-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
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

const goHome = () => {
  router.push('/')
}

// 官方推荐的暗黑模式切换逻辑
const isDark = ref(localStorage.getItem('theme') === 'dark')
function toggleTheme() {
  isDark.value = !isDark.value
  document.documentElement.classList.toggle('dark', isDark.value)
  localStorage.setItem('theme', isDark.value ? 'dark' : 'light')
}
onMounted(() => {
  if (isDark.value) document.documentElement.classList.add('dark')
})
</script>

<style>
.admin-layout {
  display: flex;
  height: 100vh;
}

.theme-toggle-btn {
  display: flex;
  align-self: flex-end;
  padding: 20px 20px 0 0;
}

.admin-sidebar {
  width: 250px;
  background: var(--admin-sidebar-bg, #2c3e50);
  color: var(--admin-sidebar-color, #fff);
  transition: width 0.3s;
  display: flex;
  flex-direction: column;
}

.admin-sidebar.collapsed {
  width: 60px;
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
  color: inherit;
  cursor: pointer;
  font-size: 16px;
}

.sidebar-nav {
  flex: 1;
  padding: 20px 0;
}

.sidebar-footer {
  padding: 10px;
  border-top: 1px solid #34495e;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
}

.footer-btn {
  flex: 1;
  margin-left: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  color: inherit;
  text-decoration: none;
  transition: all 0.3s;
}

.nav-item:hover {
  background: #34495e;
  color: #fff;
}

.nav-item.active {
  background: #3498db;
  color: #fff;
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
  transition: background-color 0.3s;
}

.logout-btn:hover {
  background: #c82333;
}

.admin-content-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #f8f9fa;
}

@media (max-width: 768px) {

  .admin-sidebar,
  .admin-sidebar.collapsed {
    width: 200px;
    min-width: 60px;
    max-width: 100vw;
    transition: width 0.3s, transform 0.3s;
  }

  .admin-sidebar {
    transform: translateX(-100%);
  }

  .admin-sidebar.collapsed {
    transform: translateX(0);
    width: 60px;
  }

  .admin-content-wrapper {
    margin-left: 0;
  }
}

:root .admin-sidebar {
  --admin-sidebar-bg: #2c3e50;
  --admin-sidebar-color: #fff;
}

html.dark .admin-sidebar {
  --admin-sidebar-bg: #232b36;
  --admin-sidebar-color: #bbb;
}

html.dark .admin-content-wrapper {
  background: #181a20;
  color: #d1d5db;
}

html.dark .page-header h2 {
  color: #f3f4f6;
}

html.dark .search-bar {
  background-color: transparent;
}

/* Dashboard.vue specific dark styles */
html.dark .stat-card,
html.dark .chart-card,
html.dark .recent-activities {
  background: #232b36;
  color: #d1d5db;
  box-shadow: none;
  border: 1px solid #374151;
}

html.dark .stat-content h3,
html.dark .chart-card h3,
html.dark .recent-activities h3,
html.dark .activity-text {
  color: #f3f4f6;
}

html.dark .stat-content p,
html.dark .activity-time {
  color: #9ca3af;
}

html.dark .chart-placeholder {
  background: #1f2937;
  border-color: #374151;
  color: #9ca3af;
}

html.dark .activity-item {
  background: #1f2937;
}
</style>
<template>
  <header class="header">
    <div class="header-content">
      <div class="logo">
        <h1 @click="$router.push('/')">ACG导航站</h1>
      </div>
      <div class="search-box">
        <input
          v-model="searchKeyword"
          class="search-input"
          placeholder="搜索资源..."
          @keyup.enter="handleSearch"
          @focus="onSearchFocus"
          @blur="onSearchBlur"
          ref="searchInputRef"
        />
        <button v-if="searchKeyword" class="clear-input-btn" @mousedown.prevent="clearInput">
          ×
        </button>
        <button class="search-btn" @click="handleSearch">
          <Search class="search-icon" />
        </button>
        <div v-if="showDropdown && searchHistory.length > 0" class="search-dropdown">
          <div class="search-section">
            <div class="section-title">搜索历史</div>
            <div class="search-items">
              <div
                v-for="item in searchHistory"
                :key="item"
                class="search-item"
                @click="handleHistorySelect(item)"
              >
                {{ item }}
              </div>
            </div>
            <div class="section-actions">
              <button @click="clearHistory">清空</button>
            </div>
          </div>
        </div>
      </div>
      <div class="header-actions">
        <el-button @click="toggleTheme" :icon="isDark ? Sunny : Moon" circle style="margin-right: 15px;"/>
        <template v-if="!isLoggedIn">
          <el-button type="primary" @click="$router.push('/login')">登录</el-button>
          <el-button @click="$router.push('/register')">注册</el-button>
        </template>
        <template v-else>
          <el-dropdown @command="handleUserCommand">
            <el-avatar :src="userInfo?.avatar" :size="32">
              {{ userInfo?.nickname?.charAt(0) || userInfo?.username?.charAt(0) }}
            </el-avatar>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="favorites">我的收藏</el-dropdown-item>
                <el-dropdown-item command="history">浏览历史</el-dropdown-item>
                <el-dropdown-item v-if="userInfo?.role === 'ADMIN'" divided command="admin">管理后台</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { Search, Moon, Sunny } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const searchKeyword = ref('')
const isDark = ref(false)

const isLoggedIn = computed(() => !!userStore.token)
const userInfo = computed(() => userStore.userInfo)

const showDropdown = ref(false)
const searchHistory = ref<string[]>([])
const searchInputRef = ref<HTMLInputElement | null>(null)

function loadSearchHistory() {
  const history = localStorage.getItem('searchHistory')
  searchHistory.value = history ? JSON.parse(history) : []
}
function saveSearchHistory(keyword: string) {
  let history = searchHistory.value.filter(item => item !== keyword)
  history.unshift(keyword)
  if (history.length > 20) history = history.slice(0, 20)
  searchHistory.value = history
  localStorage.setItem('searchHistory', JSON.stringify(history))
}
function clearHistory() {
  searchHistory.value = []
  localStorage.removeItem('searchHistory')
}
function handleSearch() {
  const kw = searchKeyword.value.trim()
  if (kw) {
    saveSearchHistory(kw)
    router.push({ path: '/search', query: { q: kw } })
    showDropdown.value = false
  }
}
function handleHistorySelect(item: string) {
  searchKeyword.value = item
  handleSearch()
}
function onSearchFocus() {
  loadSearchHistory()
  showDropdown.value = true
}
function onSearchBlur() {
  setTimeout(() => { showDropdown.value = false }, 200)
}

function clearInput() {
  searchKeyword.value = ''
  searchInputRef.value?.focus()
}

const toggleTheme = () => {
  isDark.value = !isDark.value
  document.documentElement.classList.toggle('dark', isDark.value)
  localStorage.setItem('theme', isDark.value ? 'dark' : 'light')
}

const handleUserCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/user')
      break
    case 'favorites':
      router.push('/user?tab=favorites')
      break
    case 'history':
      router.push('/user?tab=history')
      break
    case 'admin':
      router.push('/admin')
      break
    case 'logout':
      userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/')
      break
  }
}

onMounted(async () => {
  // 检查主题设置
  const theme = localStorage.getItem('theme')
  isDark.value = theme === 'dark'
  document.documentElement.classList.toggle('dark', isDark.value)
  
  // 如果有token但没有用户信息，尝试获取用户信息
  if (userStore.token && !userStore.userInfo) {
    try {
      await userStore.fetchUserProfile()
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }
})
</script>

<style scoped>
.header {
  background-color: var(--bg-color);
  border-bottom: 1px solid var(--border-color-light);
  position: sticky;
  top: 0;
  z-index: 100;
}
.header-content {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.logo h1 {
  font-size: 20px;
  color: var(--primary-color);
  margin: 0;
  cursor: pointer;
}
.search-box {
  position: relative;
  flex: 1 1 480px;
  max-width: 480px;
  margin: 0 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255,255,255,0.85);
  border-radius: 24px;
  box-shadow: none;
  backdrop-filter: blur(8px);
  border: 1px solid #eee;
  height: 40px;
  overflow: visible;
}
.search-input {
  width: 100%;
  min-width: 0;
  height: 40px;
  padding: 0 14px;
  border: none;
  border-radius: 24px 0 0 24px;
  font-size: 15px;
  outline: none;
  background: transparent;
  box-shadow: none;
  transition: background 0.2s;
}

.search-btn {
  height: 40px;
  width: 40px;
  background: transparent;
  color: #666;
  border: none;
  border-radius: 0 24px 24px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  cursor: pointer;
  transition: background 0.18s, color 0.18s;
  box-shadow: none;
  padding: 0;
}
.search-btn:hover {
  background: rgba(0,0,0,0.06);
  color: #1976d2;
}
.search-btn .search-icon {
  font-size: 18px;
  width: 18px;
  height: 18px;
  vertical-align: middle;
}

/* 卡片风格弹窗 */
.search-dropdown {
  position: absolute;
  top: 48px;
  left: 0;
  width: 100%;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.12);
  z-index: 99;
  padding: 18px 18px 12px 18px;
  min-width: 0;
  min-height: 0;
  overflow: visible;
  border: none;
  transition: box-shadow 0.2s;
}
.search-section {
  margin-bottom: 0;
}
.section-title {
  font-size: 15px;
  color: #333;
  font-weight: 600;
  margin-bottom: 10px;
}
.search-items {
  display: flex;
  flex-wrap: wrap;
  gap: 8px 10px;
  margin-bottom: 8px;
}
.search-item {
  background: #f5f6fa;
  color: #333;
  border-radius: 8px;
  padding: 6px 16px;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.18s, color 0.18s;
  white-space: nowrap;
  border: none;
}
.search-item:hover {
  background: #eaf3fb;
  color: #1976d2;
}
.section-actions {
  position: absolute;
  top: 18px;
  right: 18px;
}
.section-actions button {
  background: none;
  border: none;
  color: #bbb;
  font-size: 13px;
  cursor: pointer;
  padding: 0;
}
.section-actions button:hover {
  color: #1976d2;
}
.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}
.clear-input-btn {
  position: absolute;
  right: 48px;
  top: 50%;
  transform: translateY(-50%);
  background: transparent;
  border: none;
  color: #bbb;
  font-size: 18px;
  cursor: pointer;
  z-index: 2;
  padding: 0 6px;
  line-height: 1;
  border-radius: 50%;
  transition: background 0.18s, color 0.18s;
}
.clear-input-btn:hover {
  background: #f0f0f0;
  color: #1976d2;
}
@media (max-width: 768px) {
  .header-content {
    padding: 0 12px;
  }
  .logo h1 {
    font-size: 16px;
  }
  .search-box {
    margin: 0 12px;
    flex: 1;
  }
  .header-actions {
    gap: 8px;
  }
}
</style> 
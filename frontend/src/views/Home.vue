<template>
  <div class="home">
    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 顶部搜索框 -->
      <!-- <div class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索资源..."
          @keyup.enter="handleSearch"
          @focus="onSearchFocus"
          @blur="onSearchBlur"
          class="search-input"
          clearable
        >
          <template #append>
            <el-button icon="el-icon-search" @click="handleSearch"></el-button>
          </template>
        </el-input>
        <ul v-if="showHistory && searchHistory.length" class="search-history-list">
          <li v-for="item in searchHistory" :key="item" @mousedown.prevent="handleHistorySelect(item)">{{ item }}</li>
        </ul>
      </div> -->
      <div class="container">
        <!-- 左侧导航 -->
        <CategorySidebar :active-category="activeCategory" :categories="subCategories" :collapsed="sidebarCollapsed" @toggle="handleSidebarToggle" @select="handleCategorySelect" />
        <!-- 右侧内容 -->
        <main class="content" :class="{ 'content-collapsed': sidebarCollapsed }">
          <!-- 热门资源 -->
          <section class="section">
            <div class="section-header">
              <h2>热门资源</h2>
              <el-button text @click="loadMoreHot">查看更多</el-button>
            </div>
            <div class="resource-grid">
              <div
                v-for="resource in hotResources"
                :key="resource.id"
                class="resource-card"
                @click="viewResource(resource)"
              >
                <div class="resource-icon">
                  <img :src="getFaviconUrl(resource.url)" :alt="resource.name" />
                </div>
                <div class="resource-info">
                  <h3>{{ resource.name }}</h3>
                  <p>{{ resource.description }}</p>
                  <div class="resource-stats">
                    <span>👁️ {{ resource.viewCount }}</span>
                    <span>👍 {{ resource.likeCount }}</span>
                  </div>
                </div>
              </div>
            </div>
          </section>
          <!-- 最新资源 -->
          <section class="section">
            <div class="section-header">
              <h2>最新资源</h2>
              <el-button text @click="loadMoreNew">查看更多</el-button>
            </div>
            <div class="resource-grid">
              <div
                v-for="resource in newResources"
                :key="resource.id"
                class="resource-card"
                @click="viewResource(resource)"
              >
                <div class="resource-icon">
                  <img :src="getFaviconUrl(resource.url)" :alt="resource.name" />
                </div>
                <div class="resource-info">
                  <h3>{{ resource.name }}</h3>
                  <p>{{ resource.description }}</p>
                  <div class="resource-stats">
                    <span>👁️ {{ resource.viewCount }}</span>
                    <span>👍 {{ resource.likeCount }}</span>
                  </div>
                </div>
              </div>
            </div>
          </section>
        </main>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch, onActivated, onBeforeUnmount } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Moon, Sunny, Folder } from '@element-plus/icons-vue'
import { getResourceList, getHotResources, getLatestResources } from '@/api/resource'
import { getCategories } from '@/api/category'
import type { Ref } from 'vue'
import CategorySidebar from '@/components/CategorySidebar.vue'
import { useCategoryStore } from '@/store/category'
import { storeToRefs } from 'pinia'

interface ResourceItem {
  id: number
  name: string
  description?: string
  icon?: string
  url?: string
  viewCount?: number
  likeCount?: number
}

interface CategoryItem {
  id: number
  name: string
  icon?: string
}

// 响应式数据
const router = useRouter()
const route = useRoute()
const activeCategory = ref('')
const isDark = ref(false)
const isLoggedIn = ref(false)
const userInfo = ref({
  username: '',
  nickname: '',
  avatar: ''
})

// 模拟数据
const hotResources: Ref<ResourceItem[]> = ref([])
const newResources: Ref<ResourceItem[]> = ref([])
const sidebarCollapsed = ref(false)

// 搜索相关
const searchKeyword = ref('')
const showHistory = ref(false)
const searchHistory = ref<string[]>([])

function handleSearch() {
  if (!searchKeyword.value) return
  // 这里可以添加实际的搜索跳转逻辑
  router.push({ path: '/search', query: { q: searchKeyword.value } })
  // 记录历史
  if (!searchHistory.value.includes(searchKeyword.value)) {
    searchHistory.value.unshift(searchKeyword.value)
    if (searchHistory.value.length > 10) searchHistory.value.length = 10
  }
  showHistory.value = false
}
function onSearchFocus() {
  if (searchHistory.value.length > 0) showHistory.value = true
}
function onSearchBlur() {
  setTimeout(() => { showHistory.value = false }, 200)
}
function handleHistorySelect(item: string) {
  searchKeyword.value = item
  handleSearch()
}

function handleSidebarToggle() {
  sidebarCollapsed.value = !sidebarCollapsed.value
}

function handleResize() {
  if (window.innerWidth < 1000) {
    sidebarCollapsed.value = true
  } else {
    sidebarCollapsed.value = false
  }
}

function getFaviconUrl(url?: string) {
  if (!url) return '/default-icon.png';
  try {
    const domain = new URL(url).hostname.replace(/^www\./, '');
    return `https://favicon.im/${domain}?larger=true`;
  } catch {
    return '/default-icon.png';
  }
}

async function loadHomeData() {
  // 获取热门资源
  try {
    const hotRes = await getHotResources(6)
    if (hotRes.data && hotRes.data.data) {
      hotResources.value = hotRes.data.data
    }
  } catch (e) {
    hotResources.value = []
  }
  // 获取最新资源
  try {
    const newRes = await getLatestResources(6)
    if (newRes.data && newRes.data.data) {
      newResources.value = newRes.data.data
    }
  } catch (e) {
    newResources.value = []
  }
}

const handleCategorySelect = (index: string) => {
  router.push(`/category/${index}`)
}

const handleUserCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/api/user')
      break
    case 'favorites':
      router.push('/api/user/favorites')
      break
    case 'history':
      router.push('/api/user/history')
      break
    case 'logout':
      // 退出登录逻辑
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      isLoggedIn.value = false
      userInfo.value = { username: '', nickname: '', avatar: '' }
      ElMessage.success('已退出登录')
      break
  }
}

const viewResource = (resource: any) => {
  router.push(`/resource/${resource.id}`)
}

const loadMoreHot = () => {
  // 加载更多热门资源
  ElMessage.info('加载更多热门资源')
}

const loadMoreNew = () => {
  // 加载更多最新资源
  ElMessage.info('加载更多最新资源')
}

// 生命周期
const categoryStore = useCategoryStore()
const { categories } = storeToRefs(categoryStore)

// 只显示所有子分区（parentId!=0）
const subCategories = computed(() => categories.value.filter((c: any) => c.parentId !== 0))

onMounted(() => {
  // categoryStore.fetchCategories()
  // 检查登录状态
  const token = localStorage.getItem('token')
  if (token) {
    isLoggedIn.value = true
    const userInfoStr = localStorage.getItem('userInfo')
    if (userInfoStr) {
      userInfo.value = JSON.parse(userInfoStr)
    }
  }
  // 检查主题设置
  const theme = localStorage.getItem('theme')
  isDark.value = theme === 'dark'
  document.documentElement.classList.toggle('dark', isDark.value)
  // 获取分类数据
  // 首页数据加载
  loadHomeData()
  window.addEventListener('resize', handleResize)
  handleResize()
})

onActivated(() => {
  loadHomeData()
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
})

// 监听路由变化，回到首页时刷新内容
watch(
  () => route.fullPath,
  (newPath) => {
    if (newPath === '/') {
      loadHomeData()
    }
  }
)
</script>

<style scoped>
.home {
  min-height: 100vh;
  background-color: var(--bg-color-page);
  min-width: 900px;
}

.main-content {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.container {
  display: flex;
  gap: 20px;
  min-width: 0;
}

.sidebar {
  min-width: 0;
}

.category-nav {
  background-color: var(--bg-color);
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.category-nav h3 {
  margin-bottom: 15px;

  color: var(--text-color-primary);
}

.category-menu {
  border: none;
}

.category-menu i {
  font-size: 16px;
  vertical-align: middle;
  margin-right: 8px;
  color: #666;
  width: 24px;
  text-align: center;
  display: inline-block;
}

.content {
  flex: 1;
  min-width: 0;
  overflow: auto;
}

.content-collapsed {
  margin-left: 0;
}

.section {
  background-color: var(--bg-color);
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  margin: 0;
  color: var(--text-color-primary);
}

.resource-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.resource-card {
  background-color: var(--bg-color-page);
  border-radius: 8px;
  padding: 15px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid var(--border-color-lighter);
}

.resource-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.resource-card {
  display: flex;
  align-items: center;
  gap: 15px;
}

.resource-icon {
  width: 50px;
  height: 50px;
  flex-shrink: 0;
}

.resource-icon img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}

.resource-info {
  flex: 1;
}

.resource-info h3 {
  margin: 0 0 5px 0;
  color: var(--text-color-primary);
  font-size: 16px;
}

.resource-info p {
  margin: 0 0 10px 0;
  color: var(--text-color-secondary);
  font-size: 14px;
  line-height: 1.4;
}

.resource-stats {
  display: flex;
  gap: 15px;
  font-size: 12px;
  color: var(--text-color-secondary);
}

/* 响应式设计 */
@media (max-width: 768px) {
  /* .container {
    flex-direction: column;
  } */
  
  /* .sidebar {
    width: 100%;
  } */
  
  .resource-grid {
    grid-template-columns: 1fr;
  }
  
  .header-content {
    padding: 0 15px;
  }
  
  .search-box {
    margin: 0 10px;
  }
}

/* 移除与本地搜索框相关的样式 */
/* .search-box { ... } */
/* .search-input { ... } */
/* .search-btn { ... } */
/* .search-history-list { ... } */
</style> 
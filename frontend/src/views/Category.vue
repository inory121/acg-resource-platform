<template>
  <div class="main-content">
    <MainCategoryTabs :main-categories="mainCategories" :active-main-category="activeMainCategory" @select="onMainCategorySelect" />
    <div class="container">
      <!-- 左侧导航：只显示当前主分区下的子分区 -->
      <CategorySidebar :active-category="activeCategory" :categories="subCategories" :collapsed="sidebarCollapsed" @toggle="handleSidebarToggle" @select="onCategorySelect" />
      <!-- 右侧内容 -->
      <main class="content" :class="{ 'content-collapsed': sidebarCollapsed }">
        <el-card class="resource-list-card">
          <h3>分类资源</h3>
          <el-empty v-if="resources.length === 0" description="暂无资源" />
          <div v-else class="resource-list">
            <div v-for="res in resources" :key="res.id" class="resource-item">
              <el-button text @click="goDetail(res.id)">
                <img :src="getFaviconUrl(res.url)" class="resource-icon" />
                <div class="resource-info">
                  <h4>{{ res.name }}</h4>
                  <p>{{ res.description }}</p>
                </div>
              </el-button>
            </div>
          </div>
        </el-card>
      </main>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted, watch, computed, onBeforeUnmount } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { getResourceList } from '@/api/resource';
import CategorySidebar from '@/components/CategorySidebar.vue'
import MainCategoryTabs from '@/components/MainCategoryTabs.vue'
import type { CategoryItem } from '@/store/category'
import { storeToRefs } from 'pinia'
import { useCategoryStore } from '@/store/category'

const router = useRouter();
const route = useRoute();
const resources = ref<any[]>([]);

const categoryStore = useCategoryStore()
const { categories } = storeToRefs(categoryStore)

// 主分区（parentId === 0）
const mainCategories = computed(() => categories.value.filter((c: CategoryItem) => c.parentId === 0))
const activeMainCategory = ref(mainCategories.value[0]?.id || 0)
// 子分区（parentId === 当前主分区id）
const subCategories = computed(() => categories.value.filter((c: CategoryItem) => c.parentId === activeMainCategory.value))
// 当前选中子分区
const activeCategory = ref(subCategories.value[0]?.id || 0)

const sidebarCollapsed = ref(false)
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

// 切换主分区
function onMainCategorySelect(id: number) {
  activeMainCategory.value = id
  // 自动选中第一个子分区
  if (subCategories.value.length) {
    onCategorySelect(String(subCategories.value[0].id))
  }
}
// 切换子分区
function onCategorySelect(id: string) {
  activeCategory.value = Number(id)
  router.push(`/category/${id}`) // 添加这行代码以更新 URL
  fetchResources()
}
function goDetail(id: number) {
  router.push(`/resource/${id}`);
}
// 获取资源列表，categoryId为当前选中子分区
async function fetchResources() {
  const res = await getResourceList({ categoryId: activeCategory.value });
  resources.value = res.data.data.records || [];
}
// 监听分类数据和路由参数变化，自动同步分区高亮和资源
watch(
  [categories, () => route.params.id],
  ([newCategories, newId]) => {
    if (newCategories.length && newId) {
      const currentCategory = newCategories.find(c => c.id === Number(newId));
      if (currentCategory) {
        activeCategory.value = currentCategory.id;
        activeMainCategory.value = currentCategory.parentId;
        fetchResources();
      }
    }
  },
  { immediate: true }
);

onMounted(() => {
  categoryStore.fetchCategories();
  window.addEventListener('resize', handleResize)
  handleResize()
});
onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
})

function getFaviconUrl(url?: string) {
  if (!url) return '/default-icon.png';
  try {
    const domain = new URL(url).hostname.replace(/^www\./, '');
    return `https://favicon.im/${domain}?larger=true`;
  } catch {
    return '/default-icon.png';
  }
}
</script>
<style scoped>
.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.main-category-tabs {
  display: flex;
  gap: 20px;
  padding: 16px 0 ;
  background: transparent;
  margin-bottom: 16px;
}
.main-tab {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 28px;
  border-radius: 24px;
  cursor: pointer;
  font-weight: 600;
  font-size: 17px;
  color: #555;
  background: transparent;
  transition: background 0.18s, color 0.18s, box-shadow 0.18s;
  position: relative;
  min-height: 40px;
  line-height: 1;
}
.main-tab.active, .main-tab:hover {
  background: #eaf3fb;
  color: #1976d2;
  box-shadow: 0 2px 8px #e3e8f7;
}
.main-tab i {
  font-size: 20px;
}

.container {
  display: flex;
  gap: 20px;
}
.sidebar {
  width: 250px;
  flex-shrink: 0;
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
.category-menu .el-menu-item {
  border-radius: 8px;
  margin-bottom: 8px;
  font-size: 16px;
  padding: 12px 20px;
  transition: background 0.2s, color 0.2s;
}
.category-menu .el-menu-item.is-active, .category-menu .el-menu-item:hover {
  background: #e6f0fa;
  color: #409eff;
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
}
.resource-list-card {
  background-color: var(--bg-color);
  border-radius: 8px;
  padding: 20px;
  padding-top: 0;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
.resource-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 20px;
}
.resource-item {
  background-color: var(--bg-color-page);
  border-radius: 8px;
  padding: 15px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid var(--border-color-lighter);
  display: flex;
  align-items: center;
  gap: 15px;
}
.resource-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
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
.resource-info h4 {
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
.content-collapsed {
  margin-left: 0;
}
@media (max-width: 768px) {
  /* .container {
    flex-direction: column;
  } */
  /* .sidebar {
    width: 100%;
  } */
  .resource-list {
    grid-template-columns: 1fr;
  }
}
</style>
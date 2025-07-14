<template>
  <div class="main-content" v-if="resource">
    <MainCategoryTabs :main-categories="mainCategories" :active-main-category="activeMainCategory" @select="onMainCategorySelect" />
    <div class="container">
      <CategorySidebar :active-category="activeCategory" :categories="subCategories" @select="onCategorySelect" />
      <main class="content">
        <el-card>
          <div class="resource-header">
            <img :src="getFaviconUrl(resource.url)" class="resource-icon" />
            <div class="resource-info">
              <h2>{{ resource.name }}</h2>
              <p>{{ resource.description }}</p>
              <div class="resource-meta">
                <span>分类：{{ resource.categoryName }}</span>
                <span>浏览：{{ resource.viewCount }}</span>
                <span>点赞：{{ resource.likeCount }}</span>
              </div>
            </div>
          </div>
          <el-divider />
          <div class="resource-url">
            <el-button type="primary" @click="openResourceUrl">访问资源</el-button>
          </div>
        </el-card>
      </main>
    </div>
  </div>
  <el-empty v-else description="资源不存在" />
</template>
<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getResourceById } from '@/api/resource';
import CategorySidebar from '@/components/CategorySidebar.vue';
import MainCategoryTabs from '@/components/MainCategoryTabs.vue';
import { useCategoryStore } from '@/store/category';
import { storeToRefs } from 'pinia';
import type { CategoryItem } from '@/store/category';

const route = useRoute();
const router = useRouter();
const resource = ref<any>(null);

const categoryStore = useCategoryStore();
const { categories } = storeToRefs(categoryStore);

const mainCategories = computed(() => categories.value.filter((c: CategoryItem) => c.parentId === 0));
const activeMainCategory = ref(0);
const subCategories = computed(() => categories.value.filter((c: CategoryItem) => c.parentId === activeMainCategory.value));
const activeCategory = ref(0);

async function fetchResource() {
  try {
    const res = await getResourceById(Number(route.params.id));
    resource.value = res.data.data;
    // 资源加载后，自动高亮主分区和子分区
    if (resource.value && categories.value.length) {
      const cat = categories.value.find((c: CategoryItem) => c.id === resource.value.categoryId);
      if (cat) {
        activeCategory.value = cat.id;
        activeMainCategory.value = cat.parentId;
      }
    }
  } catch {
    resource.value = null;
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

function onMainCategorySelect(id: number) {
  activeMainCategory.value = id;
  // 跳转到该主分区下第一个子分区的分类页
  const firstSub = categories.value.find((c: CategoryItem) => c.parentId === id);
  if (firstSub) {
    router.push(`/category/${firstSub.id}`);
  }
}
function onCategorySelect(id: string) {
  // 跳转到该子分区的分类页
  router.push(`/category/${id}`);
}

function openResourceUrl() {
  if (resource.value && resource.value.url) {
    window.open(resource.value.url, '_blank');
  }
}

onMounted(async () => {
  await categoryStore.fetchCategories();
  await fetchResource();
});
// 监听路由变化，切换资源时自动刷新
watch(() => route.params.id, async (newId) => {
  if (newId) {
    await fetchResource();
  }
});
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
  padding: 16px 0 20px 0;
  background: transparent;
  margin-bottom: 12px;
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
.resource-header {
  display: flex;
  align-items: center;
}
.resource-icon {
  width: 80px;
  height: 80px;
  margin-right: 24px;
}
.resource-info h2 {
  margin: 0 0 8px 0;
}
.resource-meta {
  color: #888;
  font-size: 13px;
  margin-top: 8px;
}
.resource-url {
  margin-top: 24px;
}
@media (max-width: 768px) {
  .container {
    flex-direction: column;
  }
  .sidebar {
    width: 100%;
  }
}
</style>
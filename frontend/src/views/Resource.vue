<template>
  <div class="main-content" v-if="resource">
    <el-tabs :model-value="activeMainCategory" type="card" class="main-category-tabs" @tab-click="onMainCategorySelect">
      <el-tab-pane v-for="cat in mainCategories" :key="cat.id" :label="cat.name" :name="cat.id" />
    </el-tabs>
    <div class="container">
      <CategorySidebar
        :mainCategoryId="activeMainCategory"
        :active-category="activeCategory"
        :categories="subCategories"
        :hideUserInfo="true"
        @select="onCategorySelect"
      />
      <main class="content">
        <el-card>
          <div class="resource-header">
            <img :src="getFaviconUrl(resource.url)" class="resource-icon" />
            <div class="resource-info">
              <h2>{{ resource.name }}</h2>
              <p>{{ resource.description }}</p>
              <div class="resource-meta">
                <span>浏览：{{ resource.viewCount }}</span>
                <span style="margin-left: 10px;">点赞：{{ resource.likeCount }}</span>
                <span v-if="resource.tags && resource.tags.length > 0" class="resource-tags">
                  <el-tag
                    v-for="tag in (resource.tags.split(',').filter((t: string) => t.trim()))"
                    :key="tag"
                    type="info"
                    class="tag-item"
                  >
                    {{ tag.trim() }}
                  </el-tag>
                </span>
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
import { ref, computed, watch, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getResourceById } from '@/api/resource';
import CategorySidebar from '@/components/CategorySidebar.vue';
import MainCategoryTabs from '@/components/MainCategoryTabs.vue';
import { useCategoryStore } from '@/store/category';
import { storeToRefs } from 'pinia';
import type { CategoryItem } from '@/store/category';

export interface Resource {
  id: number;
  name: string;
  description: string;
  url: string;
  categoryId: number;
  viewCount: number;
  likeCount: number;
  tags: string;
  createdTime: string; // Changed from createdAt
  updatedTime: string; // Changed from updatedAt
}

const route = useRoute();
const router = useRouter();
const resource = ref<Resource | null>(null);

const categoryStore = useCategoryStore();
const { categories } = storeToRefs(categoryStore);
categoryStore.fetchCategories(); // Ensure categories are being fetched

const mainCategories = computed(() => categories.value.filter((c: CategoryItem) => c.parentId === 0));

// Convert ref to computed to solve race condition
const currentCategory = computed(() => {
    if (!resource.value || !categories.value.length) return null;
    return categories.value.find((c: CategoryItem) => c.id === resource.value?.categoryId) || null;
});

const activeCategory = computed(() => currentCategory.value?.id || 0);
const activeMainCategory = computed(() => currentCategory.value?.parentId || 0);

const subCategories = computed(() => {
    if (!activeMainCategory.value) return [];
    return categories.value.filter((c: CategoryItem) => c.parentId === activeMainCategory.value);
});

async function fetchResource(id: number) {
  try {
    const res = await getResourceById(id);
    resource.value = res.data.data;
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

function onMainCategorySelect(tab: { paneName: string | number }) {
  const id = Number(tab.paneName);
  // activeMainCategory.value = id; // This is now a computed property and cannot be set directly.
  
  // Instead of setting, we navigate to the first sub-category of the selected main category.
  const firstSubCategory = categories.value.find((c: CategoryItem) => c.parentId === id);
  if (firstSubCategory) {
    router.push(`/category/${firstSubCategory.id}`);
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

watch(
  () => route.params.id,
  (newId) => {
    if (newId) {
      fetchResource(Number(newId));
    }
  },
  { immediate: true }
);

onMounted(() => {
  // 移除这里的调用，因为 watch immediate: true 会在初始化时执行
  // if (route.params.id) {
  //   fetchResource(Number(route.params.id));
  // }
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
  gap: 24px;
  padding-top: 15px;
  background: transparent;
}
:deep(.el-tabs__item) {
  font-size: 18px !important;
  font-weight: bold;
  letter-spacing: 1px;
  height: 60px;
  line-height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
}
:deep(.el-tabs) {
    --el-tabs-header-height: 60px;
    display: flex;
}
:deep(.dark .el-tabs--card .el-tabs__item) {
  background: #181a20 !important;
  color: #bbb !important;
  border: 1.5px solid #333 !important;
  border-bottom: none !important;
  transition: background 0.18s, color 0.18s, border-color 0.18s;
}
:deep(.dark .el-tabs--card .el-tabs__item.is-active) {
  background: #232b36 !important;
  color: #409EFF !important;
  border-bottom: 2.5px solid #232b36 !important;
  font-weight: bold;
}
:deep(.dark .el-tabs--card .el-tabs__item:hover) {
  background: #23272e !important;
  color: #fff !important;
}
:deep(.dark .el-tabs--card > .el-tabs__header) {
  border-bottom: 2px solid #333 !important;
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
.resource-tags {
  margin-left: 16px;
  display: inline-flex;
  gap: 8px;
}
.tag-item {
  font-size: 12px;
  padding: 0 8px;
  border-radius: 6px;
  background: #f4f8fb;
  color: #409EFF;
  border: none;
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
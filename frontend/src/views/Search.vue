<template>
  <div class="main-content">
    <div class="search-header">
      <h2>搜索结果</h2>
      <div class="search-keyword">关键词：<span>{{ keyword }}</span></div>
    </div>
    <el-empty v-if="loading && resources.length === 0" description="加载中..." />
    <el-empty v-else-if="!loading && resources.length === 0" description="暂无相关资源" />
    <div v-else class="resource-list">
      <div v-for="res in resources" :key="res.id" class="resource-item" @click="goDetail(res.id)">
        <img :src="getFaviconUrl(res.url)" class="resource-icon" />
        <div class="resource-info">
          <h4>{{ res.name }}</h4>
          <p>{{ res.description }}</p>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, watch, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getResourceList } from '@/api/resource';

const route = useRoute();
const router = useRouter();
const resources = ref<any[]>([]);
const loading = ref(false);
const keyword = ref('');

function goDetail(id: number) {
  router.push(`/resource/${id}`);
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
async function fetchResources() {
  loading.value = true;
  const q = route.query.q as string;
  keyword.value = q || '';
  if (!q) {
    resources.value = [];
    loading.value = false;
    return;
  }
  try {
    const res = await getResourceList({ keyword: q });
    resources.value = res.data.data.records || [];
  } catch {
    resources.value = [];
  }
  loading.value = false;
}
watch(() => route.query.q, fetchResources, { immediate: true });
</script>
<style scoped>
.main-content {
  max-width: 900px;
  margin: 32px auto;
  padding: 20px;
}
.search-header {
  display: flex;
  align-items: center;
  gap: 30px;
  margin-bottom: 24px;
}
.search-keyword span {
  color: #1976d2;
  font-weight: bold;
}
.resource-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
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
</style> 
<template>
  <aside class="sidebar" :class="{ collapsed: collapsed }">
    <div v-if="!props.hideUserInfo" class="sidebar-user-info">
      <el-avatar v-if="isLoggedIn" :src="userInfo?.avatar" size="large" class="sidebar-avatar" @click="goUserCenter">
        {{ userInfo?.nickname?.charAt(0) || userInfo?.username?.charAt(0) }}
      </el-avatar>
      <div v-if="isLoggedIn" class="sidebar-nickname" @click="goUserCenter">{{ userInfo?.nickname || userInfo?.username }}</div>
    </div>
    <div v-if="!collapsed" class="category-nav">
      <div class="category-toggle-btn-wrap">
        <el-button class="sidebar-toggle-btn" @click="$emit('toggle')" circle size="small">
          <i class="fas fa-bars"></i>
        </el-button>
      </div>
      <h3 class="category-title">热门资源分类</h3>
      <el-menu
        :default-active="String(activeCategory)"
        class="category-menu"
        @select="onSelect"
      >
        <el-menu-item
          v-for="cat in hotCategories"
          :key="cat.id"
          :index="cat.id.toString()"
        >
          <i :class="cat.icon || 'fas fa-folder'"></i>
          <span>{{ cat.name }}</span>
        </el-menu-item>
      </el-menu>
    </div>
    <div v-else class="category-nav-collapsed">
      <div class="category-toggle-btn-wrap">
        <el-button class="sidebar-toggle-btn" @click="$emit('toggle')" circle size="small">
          <i class="fas fa-bars"></i>
        </el-button>
      </div>
      <el-menu
        :default-active="String(activeCategory)"
        class="category-menu category-menu-collapsed"
        @select="onSelect"
      >
        <el-menu-item
          v-for="cat in hotCategories"
          :key="cat.id"
          :index="cat.id.toString()"
        >
          <div class="collapsed-icon-wrap">
            <i :class="cat.icon || 'fas fa-folder'"></i>
          </div>
        </el-menu-item>
      </el-menu>
    </div>
  </aside>
</template>
<script setup lang="ts">
import { storeToRefs } from 'pinia'
import { useCategoryStore } from '@/store/category'
import type { CategoryItem } from '@/store/category'
import { computed, watch } from 'vue'
import { useUserStore } from '@/store/user'
import { useRouter } from 'vue-router'

const props = defineProps<{
  activeCategory: string | number,
  categories?: CategoryItem[],
  collapsed?: boolean,
  mainCategoryId?: number,
  hideUserInfo?: boolean
}>()
const emit = defineEmits(['select', 'toggle'])

const categoryStore = useCategoryStore()
// const { categories: globalCategories } = storeToRefs(categoryStore)

const hotCategories = computed(() => {
  // 优先使用 props 传入的 categories，如果未传入，则使用 store 中的全局 categories
  const sourceCategories = props.categories || categoryStore.categories;

  if (props.mainCategoryId) {
    // 只显示当前主分类下的子分类
    return sourceCategories
      .filter(cat => cat.parentId === props.mainCategoryId)
      .sort((a, b) => Number(a.sortOrder || 0) - Number(b.sortOrder || 0));
  } else {
    // 首页：全站热门子分类（前10，按 sort_order 排序）
    return sourceCategories
      .filter(cat => cat.parentId !== 0)
      .sort((a, b) => Number(a.sortOrder || 0) - Number(b.sortOrder || 0))
      .slice(0, 10);
  }
});

categoryStore.fetchCategories()

function onSelect(id: string) {
  emit('select', id)
}

// 用户信息相关
const userStore = useUserStore()
const router = useRouter()
const userInfo = computed(() => userStore.userInfo)
const isLoggedIn = computed(() => !!userStore.token)
function goUserCenter() {
  router.push('/user')
}
</script>
<style scoped>
.sidebar {
  width: 250px;
  flex-shrink: 0;
  transition: width 0.2s;
  min-width: 0;
  position: relative;
}
.sidebar.collapsed {
  width: 60px;
  min-width: 0;
}
.sidebar-user-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 18px 0 10px 0;
}
.sidebar-avatar {
  cursor: pointer;
  margin-bottom: 6px;
}
.sidebar-nickname {
  font-size: 15px;
  color: #333;
  font-weight: 500;
  margin-bottom: 4px;
  cursor: pointer;
  transition: color 0.18s;
}
.sidebar-nickname:hover {
  color: #409eff;
}
.sidebar-auth-btns {
  display: flex;
  gap: 8px;
  margin-bottom: 4px;
}
.sidebar-toggle-btn-wrap {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 10px;
}
.sidebar-toggle-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--bg-color, #fff);
  border: none;
  box-shadow: 0 2px 8px rgba(0,0,0,0.10);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #888;
  cursor: pointer;
  transition: background 0.18s, color 0.18s, box-shadow 0.18s;
  z-index: 10;
}
.sidebar-toggle-btn:hover {
  background: var(--el-color-primary-light-9, #f0f6ff);
  color: var(--el-color-primary, #409eff);
  box-shadow: 0 4px 16px rgba(64,158,255,0.12);
}
.category-nav {
  background-color: var(--bg-color);
  border-radius: 8px;
  padding: 20px 0;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  /* min-height: 100%; */
}
.category-nav-collapsed {
  background-color: var(--bg-color);
  border-radius: 8px;
  padding: 20px 0;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  /* min-height: 100%; */
  position: relative;
}
.category-toggle-btn-wrap {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 10px;
}
.category-title {
  text-align: center;
  margin-bottom: 15px;
  margin-top: 5px;
  color: var(--text-color-primary);
  font-size: 18px;
  font-weight: 600;
}
.category-menu {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  border: none;
  background: transparent;
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
.category-menu-collapsed {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  border: none;
  background: transparent;
}
.collapsed-icon-wrap {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 40px;
  margin: 0 auto;
}
.category-menu-collapsed .el-menu-item {
  justify-content: center;
  align-items: center;
  display: flex;
  flex-direction: column;
  padding: 0 !important;
  background: transparent;
  width: 100%;
  text-align: center;
}
.category-menu-collapsed .el-menu-item i {
  font-size: 20px;
  margin: 0 auto !important;
  color: #666;
  width: 24px;
  text-align: center;
  display: block;
}
@media (max-width: 768px) {
  /* .sidebar {
    width: 100%;
  } */
}
</style> 
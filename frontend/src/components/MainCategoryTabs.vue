<template>
  <div class="main-category-tabs">
    <el-button
      v-for="cat in mainCategories"
      :key="cat.id"
      :type="cat.id === activeMainCategory ? 'primary' : 'default'"
      class="main-tab"
      @click="$emit('select', cat.id)"
      size="small"
      round
    >
      <i :class="cat.icon || 'fas fa-folder'"></i>
      <span>{{ cat.name }}</span>
    </el-button>
  </div>
</template>
<script setup lang="ts">
import { defineProps, defineEmits } from 'vue'
interface CategoryItem {
  id: number
  name: string
  icon?: string
}
const props = defineProps<{
  mainCategories: CategoryItem[],
  activeMainCategory: number | string
}>()
defineEmits(['select'])
</script>
<style scoped>
.main-category-tabs {
  display: flex;
  gap: 16px;
  padding: 16px 0 ;
  border-bottom: 1px solid #eee;
  overflow-x: auto;
  background: var(--bg-color);
}
.main-tab {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 18px;
  border-radius: 20px 20px 0 0;
  cursor: pointer;
  font-weight: 500;
  color: #888;
  transition: background 0.2s, color 0.2s;
}
.main-tab.active {
  background: #409eff22;
  color: #409eff;
}
.main-tab i {
  font-size: 18px;
}
</style> 
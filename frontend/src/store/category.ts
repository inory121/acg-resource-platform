import { defineStore } from 'pinia'
import { getCategories } from '@/api/category'

export interface CategoryItem {
  id: number;
  name: string;
  icon?: string;
  parentId: number;
  sortOrder: number;
}

export const useCategoryStore = defineStore('category', {
  state: () => ({
    categories: [] as CategoryItem[],
    loaded: false
  }),
  actions: {
    async fetchCategories() {
      if (this.loaded) return
      const res = await getCategories()
      if (res.data && res.data.data) {
        this.categories = res.data.data
        this.loaded = true
      }
    }
  }
}) 
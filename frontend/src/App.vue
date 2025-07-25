<template>
  <div id="app">
    <template v-if="!isAdminRoute">
      <HeaderBar />
    </template>
    <router-view :key="$route.fullPath" />
  </div>
</template>

<script setup lang="ts">
import HeaderBar from './components/HeaderBar.vue'
import {useRoute} from 'vue-router'
import {computed} from 'vue'
// 组件逻辑
const route = useRoute()
const isAdminRoute = computed(() => route.path.startsWith('/admin'))

defineExpose({ isAdminRoute })
</script>

<style>
#app {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  height: 100vh;
  margin: 0;
  padding: 0;
  min-width: 900px;
  width: 100vw;
}

* {
  box-sizing: border-box;
}

body {
  margin: 0;
  padding: 0;
  background-color: var(--el-bg-color);
  color: var(--el-text-color-primary);
}
</style> 
<template>
  <div class="not-found-container">
    <h1>404</h1>
    <p>页面不存在</p>
    <el-button type="primary" @click="goHome">返回首页</el-button>
  </div>
</template>
<script setup lang="ts">
import {useRouter} from 'vue-router';

const router = useRouter();
function goHome() {
  router.push('/');
}
</script>
<style scoped>
.not-found-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 80vh;
}
h1 {
  font-size: 5rem;
  margin-bottom: 16px;
}
p {
  margin-bottom: 24px;
}
</style>
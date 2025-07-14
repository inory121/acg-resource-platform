<template>
  <div class="user-center-container">
    <el-card class="user-info-card">
      <div class="user-header">
        <el-avatar :src="userInfo?.avatar" size="large">
          {{ userInfo?.nickname?.charAt(0) || userInfo?.username?.charAt(0) }}
        </el-avatar>
        <div class="user-basic">
          <h3>{{ userInfo?.nickname || userInfo?.username }}</h3>
          <p>{{ userInfo?.email }}</p>
        </div>
      </div>
      <el-divider />
      <el-row :gutter="16">
        <el-col :span="8">
          <el-button type="primary" @click="activeTab = 'profile'" :plain="activeTab !== 'profile'">资料</el-button>
        </el-col>
        <el-col :span="8">
          <el-button type="primary" @click="activeTab = 'favorites'" :plain="activeTab !== 'favorites'">收藏</el-button>
        </el-col>
        <el-col :span="8">
          <el-button type="primary" @click="activeTab = 'history'" :plain="activeTab !== 'history'">历史</el-button>
        </el-col>
      </el-row>
    </el-card>
    <el-card class="user-content-card">
      <component :is="currentTabComponent" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useUserStore } from '@/store/user';
import { ElMessage } from 'element-plus';
import ProfileTab from '@/components/user/ProfileTab.vue';
import FavoritesTab from '@/components/user/FavoritesTab.vue';
import HistoryTab from '@/components/user/HistoryTab.vue';

const userStore = useUserStore();
const activeTab = ref('profile');
const userInfo = computed(() => userStore.userInfo);

const currentTabComponent = computed(() => {
  if (activeTab.value === 'profile') return ProfileTab;
  if (activeTab.value === 'favorites') return FavoritesTab;
  if (activeTab.value === 'history') return HistoryTab;
  return ProfileTab;
});

onMounted(() => {
  if (!userInfo.value) {
    userStore.fetchUserProfile().catch(() => {
      ElMessage.error('获取用户信息失败');
    });
  }
});
</script>

<style scoped>
.user-center-container {
  max-width: 800px;
  margin: 32px auto;
  padding: 0 16px;
}

.user-info-card {
  margin-bottom: 24px;
}

.user-header {
  display: flex;
  align-items: center;
  padding: 20px 0;
}

.user-basic {
  margin-left: 16px;
}

.user-basic h3 {
  margin: 0 0 8px 0;
  font-size: 20px;
  color: #333;
}

.user-basic p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.user-content-card {
  min-height: 400px;
}

@media (max-width: 768px) {
  .user-center-container {
    margin: 16px auto;
    padding: 0 8px;
  }
  
  .user-header {
    flex-direction: column;
    text-align: center;
  }
  
  .user-basic {
    margin-left: 0;
    margin-top: 12px;
  }
}
</style>
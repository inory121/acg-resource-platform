<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <div class="stats-grid">
      <el-card class="stat-card" v-for="(stat, idx) in statList" :key="idx">
        <div class="stat-icon">
          <i :class="stat.icon"></i>
        </div>
        <div class="stat-content">
          <h3>{{ stat.value }}</h3>
          <p>{{ stat.label }}</p>
        </div>
      </el-card>
    </div>

    <!-- 图表区域 -->
    <div class="charts-grid">
      <div class="chart-card">
        <h3>最近7天资源上传趋势</h3>
        <div class="chart-placeholder">
          <p>图表区域 - 需要集成图表库</p>
        </div>
      </div>

      <div class="chart-card">
        <h3>分类资源分布</h3>
        <div class="chart-placeholder">
          <p>图表区域 - 需要集成图表库</p>
        </div>
      </div>
    </div>

    <!-- 最新活动 -->
    <div class="recent-activities">
      <h3>最新活动</h3>
      <div class="activity-list">
        <div v-for="activity in recentActivities" :key="activity.id" class="activity-item">
          <div class="activity-icon">
            <i :class="activity.icon"></i>
          </div>
          <div class="activity-content">
            <p class="activity-text">{{ activity.text }}</p>
            <span class="activity-time">{{ activity.time }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { getDashboardStats } from '@/api/admin'
import type { DashboardStats } from '@/api/admin'

const stats = ref<DashboardStats>({
  totalResources: 0,
  totalUsers: 0,
  totalCategories: 0,
  totalViews: 0,
  recentUsers: [],
  hotResources: []
})

const loadStats = async () => {
  try {
    const response = await getDashboardStats()
    if (response.data.code === 200) {
      stats.value = response.data.data
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

onMounted(() => {
  loadStats()
})

// 确保 statList 正确引用 stats.value 的值
const statList = computed(() => [
  { icon: 'fas fa-file-alt', value: stats.value.totalResources, label: '总资源数' },
  { icon: 'fas fa-users', value: stats.value.totalUsers, label: '总用户数' },
  { icon: 'fas fa-folder', value: stats.value.totalCategories, label: '总分类数' },
  { icon: 'fas fa-eye', value: stats.value.totalViews, label: '总浏览量' }
])

const recentActivities = ref([
  { id: 1, icon: 'fas fa-upload', text: '用户A 上传了新资源', time: '1小时前' },
  { id: 2, icon: 'fas fa-user-plus', text: '新用户B 注册', time: '2小时前' },
  { id: 3, icon: 'fas fa-edit', text: '用户C 修改了资源信息', time: '3小时前' }
])
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 20px;
  height: 200px;
}

:deep(.el-card__body) {
  display: flex;
  align-items: center;
  flex-direction: column;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
  margin-bottom: 8px;
}

.stat-card:nth-child(1) .stat-icon {
  background: #3498db;
}

.stat-card:nth-child(2) .stat-icon {
  background: #e74c3c;
}

.stat-card:nth-child(3) .stat-icon {
  background: #f39c12;
}

.stat-card:nth-child(4) .stat-icon {
  background: #27ae60;
}

.stat-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1;
  width: 100%;
}

.stat-content h3 {
  margin: 0;
  font-size: 32px;
  font-weight: 700;
  color: #2c3e50;
  text-align: center;
}

.stat-content p {
  margin: 8px 0 0 0;
  color: #7f8c8d;
  font-size: 15px;
  text-align: center;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.chart-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.chart-card h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 18px;
  font-weight: 600;
}

.chart-placeholder {
  height: 200px;
  background: #f8f9fa;
  border: 2px dashed #dee2e6;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6c757d;
}

.recent-activities {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.recent-activities h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 18px;
  font-weight: 600;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 6px;
}

.activity-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #3498db;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 14px;
}

.activity-content {
  flex: 1;
}

.activity-text {
  margin: 0;
  color: #2c3e50;
  font-weight: 500;
}

.activity-time {
  color: #7f8c8d;
  font-size: 12px;
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }

  .charts-grid {
    grid-template-columns: 1fr;
  }
}
</style>
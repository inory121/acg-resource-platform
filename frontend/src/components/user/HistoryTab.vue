<template>
  <div class="history-tab">
    <div class="history-header">
      <h3>浏览历史</h3>
      <div class="history-actions">
        <el-button @click="clearHistory" :loading="clearing">
          清空历史
        </el-button>
        <el-button type="primary" @click="refreshHistory" :loading="loading">
          刷新
        </el-button>
      </div>
    </div>
    
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="3" animated />
    </div>
    
    <div v-else-if="history.length === 0" class="empty-container">
      <el-empty description="暂无浏览历史" />
    </div>
    
    <div v-else class="history-list">
      <el-card
        v-for="item in history"
        :key="item.id"
        class="history-item"
        @click="viewResource(item.resource)"
      >
        <div class="history-content">
          <div class="resource-info">
            <h4>{{ item.resource.title }}</h4>
            <p class="resource-desc">{{ item.resource.description }}</p>
            <div class="resource-meta">
              <span class="category">{{ item.resource.categoryName }}</span>
              <span class="date">{{ formatDate(item.viewTime) }}</span>
            </div>
          </div>
          <div class="history-actions">
            <el-button
              type="danger"
              size="small"
              @click.stop="removeHistory(item.id)"
              :loading="removingId === item.id"
            >
              删除
            </el-button>
          </div>
        </div>
      </el-card>
    </div>
    
    <div v-if="history.length > 0" class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {useRouter} from 'vue-router';
import {ElMessage, ElMessageBox} from 'element-plus';

const router = useRouter();
const loading = ref(false);
const clearing = ref(false);
const removingId = ref<number | null>(null);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

interface Resource {
  id: number;
  title: string;
  description: string;
  categoryName: string;
}

interface History {
  id: number;
  resource: Resource;
  viewTime: string;
}

const history = ref<History[]>([]);

onMounted(() => {
  loadHistory();
});

const loadHistory = async () => {
  loading.value = true;
  try {
    // 这里应该调用实际的API
    // 暂时使用模拟数据
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    history.value = [
      {
        id: 1,
        resource: {
          id: 1,
          title: '示例资源1',
          description: '这是一个示例资源的描述信息',
          categoryName: '动漫专区'
        },
        viewTime: '2024-01-15T10:30:00'
      },
      {
        id: 2,
        resource: {
          id: 2,
          title: '示例资源2',
          description: '这是另一个示例资源的描述信息',
          categoryName: '游戏专区'
        },
        viewTime: '2024-01-14T15:20:00'
      },
      {
        id: 3,
        resource: {
          id: 3,
          title: '示例资源3',
          description: '这是第三个示例资源的描述信息',
          categoryName: '音乐专区'
        },
        viewTime: '2024-01-13T09:15:00'
      }
    ];
    total.value = history.value.length;
  } catch (error) {
    ElMessage.error('加载历史记录失败');
  } finally {
    loading.value = false;
  }
};

const refreshHistory = () => {
  loadHistory();
};

const clearHistory = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有浏览历史吗？此操作不可恢复。', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    clearing.value = true;
    
    // 这里应该调用实际的API
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    history.value = [];
    total.value = 0;
    
    ElMessage.success('已清空浏览历史');
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败');
    }
  } finally {
    clearing.value = false;
  }
};

const removeHistory = async (historyId: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这条历史记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    removingId.value = historyId;
    
    // 这里应该调用实际的API
    await new Promise(resolve => setTimeout(resolve, 500));
    
    history.value = history.value.filter(h => h.id !== historyId);
    total.value = history.value.length;
    
    ElMessage.success('已删除历史记录');
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败');
    }
  } finally {
    removingId.value = null;
  }
};

const viewResource = (resource: Resource) => {
  router.push(`/resource/${resource.id}`);
};

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN');
};

const handleSizeChange = (size: number) => {
  pageSize.value = size;
  loadHistory();
};

const handleCurrentChange = (page: number) => {
  currentPage.value = page;
  loadHistory();
};
</script>

<style scoped>
.history-tab {
  padding: 20px 0;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.history-header h3 {
  margin: 0;
  color: #333;
}

.history-actions {
  display: flex;
  gap: 12px;
}

.loading-container {
  padding: 40px 0;
}

.empty-container {
  padding: 60px 0;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.history-item {
  cursor: pointer;
  transition: all 0.3s;
}

.history-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.history-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.resource-info {
  flex: 1;
}

.resource-info h4 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 16px;
}

.resource-desc {
  margin: 0 0 12px 0;
  color: #666;
  font-size: 14px;
  line-height: 1.5;
}

.resource-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #999;
}

.category {
  background: #f0f0f0;
  padding: 2px 8px;
  border-radius: 4px;
}

.history-actions {
  margin-left: 16px;
}

.pagination-container {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .history-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .history-actions {
    width: 100%;
    justify-content: space-between;
  }
  
  .history-content {
    flex-direction: column;
    gap: 12px;
  }
  
  .history-actions {
    margin-left: 0;
    align-self: flex-end;
  }
}
</style> 
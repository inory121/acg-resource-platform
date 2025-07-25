<template>
  <div class="favorites-tab">
    <div class="favorites-header">
      <h3>我的收藏</h3>
      <el-button type="primary" @click="refreshFavorites" :loading="loading">
        刷新
      </el-button>
    </div>
    
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="3" animated />
    </div>
    
    <div v-else-if="favorites.length === 0" class="empty-container">
      <el-empty description="暂无收藏内容" />
    </div>
    
    <div v-else class="favorites-list">
      <el-card
        v-for="favorite in favorites"
        :key="favorite.id"
        class="favorite-item"
        @click="viewResource(favorite.resource)"
      >
        <div class="favorite-content">
          <div class="resource-info">
            <h4>{{ favorite.resource.title }}</h4>
            <p class="resource-desc">{{ favorite.resource.description }}</p>
            <div class="resource-meta">
              <span class="category">{{ favorite.resource.categoryName }}</span>
              <span class="date">{{ formatDate(favorite.createdTime) }}</span>
            </div>
          </div>
          <div class="favorite-actions">
            <el-button
              type="danger"
              size="small"
              @click.stop="removeFavorite(favorite.id)"
              :loading="removingId === favorite.id"
            >
              取消收藏
            </el-button>
          </div>
        </div>
      </el-card>
    </div>
    
    <div v-if="favorites.length > 0" class="pagination-container">
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

interface Favorite {
  id: number;
  resource: Resource;
  createdTime: string;
}

const favorites = ref<Favorite[]>([]);

onMounted(() => {
  loadFavorites();
});

const loadFavorites = async () => {
  loading.value = true;
  try {
    // 这里应该调用实际的API
    // 暂时使用模拟数据
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    favorites.value = [
      {
        id: 1,
        resource: {
          id: 1,
          title: '示例资源1',
          description: '这是一个示例资源的描述信息',
          categoryName: '动漫专区'
        },
        createdTime: '2024-01-15T10:30:00'
      },
      {
        id: 2,
        resource: {
          id: 2,
          title: '示例资源2',
          description: '这是另一个示例资源的描述信息',
          categoryName: '游戏专区'
        },
        createdTime: '2024-01-14T15:20:00'
      }
    ];
    total.value = favorites.value.length;
  } catch (error) {
    ElMessage.error('加载收藏失败');
  } finally {
    loading.value = false;
  }
};

const refreshFavorites = () => {
  loadFavorites();
};

const removeFavorite = async (favoriteId: number) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    removingId.value = favoriteId;
    
    // 这里应该调用实际的API
    await new Promise(resolve => setTimeout(resolve, 500));
    
    favorites.value = favorites.value.filter(f => f.id !== favoriteId);
    total.value = favorites.value.length;
    
    ElMessage.success('已取消收藏');
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
  loadFavorites();
};

const handleCurrentChange = (page: number) => {
  currentPage.value = page;
  loadFavorites();
};
</script>

<style scoped>
.favorites-tab {
  padding: 20px 0;
}

.favorites-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.favorites-header h3 {
  margin: 0;
  color: #333;
}

.loading-container {
  padding: 40px 0;
}

.empty-container {
  padding: 60px 0;
}

.favorites-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.favorite-item {
  cursor: pointer;
  transition: all 0.3s;
}

.favorite-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.favorite-content {
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

.favorite-actions {
  margin-left: 16px;
}

.pagination-container {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .favorites-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .favorite-content {
    flex-direction: column;
    gap: 12px;
  }
  
  .favorite-actions {
    margin-left: 0;
    align-self: flex-end;
  }
}
</style> 
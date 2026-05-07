<template>
  <view class="cache-manager-page">
    <!-- 缓存列表 -->
    <view class="cache-list">
      <view
          v-for="item in cacheList"
          :key="item.dictValue"
          class="cache-item"
      >
        <view class="cache-info">
          <text class="cache-name">{{ item.dictLabel }}</text>
          <view class="cache-key">
            <text v-for="(key, index) in item.dictValue.split(',')" :key="index" class="cache-key-item">
              {{ key.trim() }}
            </text>
          </view>
        </view>
        <view class="cache-actions">
          <button
              class="clear-btn"
              @click="handleClear(item)"
          >
            清空
          </button>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-if="cacheList.length === 0" class="empty-state">
      <view class="empty-icon">🗑️</view>
      <text class="empty-text">暂无缓存数据</text>
      <text class="empty-subtext">系统缓存将在此处显示</text>
    </view>
  </view>
</template>

<script>
import {deleteCacheByKey} from '@/api/tools/cache/cacheApi'
import { getDictData } from '@/utils/dict';

export default {
  data() {
    return {
      cacheList: []
    }
  },
  onLoad(options) {
    if (options.pageTitle) {
      uni.setNavigationBarTitle({
        title: options.pageTitle
      });
    }
    this.initCacheList();
  },
  methods: {
    async initCacheList() {
      const result = await getDictData('cache_manage');
      this.cacheList = result;
    },
    /**
     * 处理清空缓存
     */
    handleClear(item) {
      this.$modal.confirm('确认清空缓存吗？').then(() => {
        this.clearCache(item);
      });
    },

    /**
     * 执行清空缓存操作
     */
    async clearCache(item) {
      // 调用清空接口
      const response = await deleteCacheByKey(item.dictValue);
      if (response.code === 200) {
        this.$modal.msgSuccess('清空成功');
      }
    }
  }
};
</script>

<style scoped lang="scss">
.cache-manager-page {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 15rpx;
  box-sizing: border-box;
}

.cache-list {
  background-color: #fff;
  border-radius: 10rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.03);
}

.cache-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 20rpx;
  border-bottom: 1rpx solid #f0f2f5;
  transition: all 0.3s ease;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    background-color: #fafafa;
  }

  .cache-info {
    flex: 1;
    margin-right: 20rpx;

    .cache-name {
      font-size: 28rpx;
      font-weight: 500;
      color: #303133;
      display: block;
      margin-bottom: 8rpx;
      line-height: 1.3;
    }

    .cache-key {
      font-size: 22rpx;
      color: #909399;
      display: block;
      line-height: 1.4;
      word-break: break-all;
      .cache-key-item {
        display: block;
        margin-bottom: 4rpx;
      }
    }
  }

  .cache-actions {
    .clear-btn {
      width: 140rpx;
      height: 68rpx;
      line-height: 68rpx;
      font-size: 26rpx;
      color: #f56c6c;
      background-color: #fef0f0;
      border: 1rpx solid #fbc4c4;
      border-radius: 8rpx;
      transition: all 0.3s ease;

      &:hover {
        background-color: #fde2e2;
        transform: translateY(-1rpx);
        box-shadow: 0 2rpx 8rpx rgba(245, 108, 108, 0.15);
      }

      &:active {
        background-color: #fbc4c4;
        transform: translateY(0);
      }

      &[disabled] {
        opacity: 0.6;
        cursor: not-allowed;
      }
    }
  }
}

.empty-state {
  background-color: #fff;
  border-radius: 12rpx;
  padding: 80rpx 20rpx;
  text-align: center;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
  margin-top: 40rpx;

  .empty-icon {
    font-size: 80rpx;
    margin-bottom: 20rpx;
  }

  .empty-text {
    font-size: 26rpx;
    color: #606266;
    display: block;
    margin-bottom: 8rpx;
  }

  .empty-subtext {
    font-size: 22rpx;
    color: #909399;
    display: block;
  }
}
</style>
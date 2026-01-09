<template>
  <view class="user-online-container">
    <!-- 搜索区域 -->
    <view class="search-box">
      <view class="search-form">
        <view class="search-item">
          <text class="label">账号名称：</text>
          <input
              v-model="queryParams.username"
              type="text"
              placeholder="请输入账号名称"
              @confirm="handleQuery"
          />
        </view>
      </view>
      <view class="btn-group">
        <button class="btn search-btn" @tap="handleQuery">
          <uni-icons type="search" size="14" color="#fff"></uni-icons>
          <text>搜索</text>
        </button>
        <button class="btn reset-btn" @tap="resetQuery">
          <uni-icons type="refresh" size="14" color="#606266"></uni-icons>
          <text>重置</text>
        </button>
      </view>
    </view>

    <!-- 字典列表 -->
    <view class="user-online-list" v-if="userOnlineList.length > 0">
      <view v-for="(item, index) in userOnlineList" :key="index" class="user-online-item">
        <view class="user-online-info">
          <view class="info-row">
            <text class="label">登录账号：</text>
            <text class="value">{{ item.username}}</text>
          </view>
          <view class="info-row">
            <text class="label">登录ip：</text>
            <text class="value">{{ item.ipaddr}}</text>
          </view>
          <view class="info-row">
            <text class="label">登录地点：</text>
            <text class="value">{{ item.loginLocation}}</text>
          </view>
          <view class="info-row">
            <text class="label">登录时间：</text>
            <text class="value">{{ item.loginTime }}</text>
          </view>
        </view>
      </view>

      <!-- 加载更多提示 -->
      <uni-load-more :status="loadMoreStatus" :icon-size="16" :content-text="contentText" />
    </view>

    <!-- 空数据提示 -->
    <view v-else class="empty-box">
      <uni-icons type="info" size="50" color="#999"></uni-icons>
      <text class="empty-text">暂无数据</text>
    </view>
  </view>
</template>

<script>

import { queryUserOnlinePage, forceLogout } from "@/api/monitor/user-online";
import { formatTime } from '@/utils/dateFormat.js'

export default {
  data() {
    return {
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        username: undefined
      },
      userOnlineList: [],
      loading: false,
      hasMore: true,
      total: 0,
      loadMoreStatus: 'more',
      contentText: {
        contentdown: '上拉显示更多',
        contentrefresh: '正在加载...',
        contentnomore: '没有更多数据了'
      }
    }
  },
  onLoad() {
    this.getList()
  },
  methods: {
    formatTime(time) {
      return formatTime(time)
    },
    // 获取列表数据
    async getList(type = 'more') {
      console.log(type)
      if (this.loading) return
      this.loading = true
      try {
        queryUserOnlinePage(this.queryParams).then(response => {
          console.log(response)
          if (response.code === 200) {
            const list = response.data
            const total = response.total

            if (type === 'refresh' || this.queryParams.pageNum === 1) {
              this.userOnlineList = list
            } else {
              this.userOnlineList = [...this.userOnlineList, ...list]
            }
            this.total = total
            this.hasMore = this.userOnlineList.length < total
            this.loadMoreStatus = this.hasMore ? 'more' : 'noMore'
          }
        });

      } catch (error) {
        this.loadMoreStatus = 'more'
        uni.showToast({
          title: '获取在线用户列表错误',
          icon: 'none'
        })
      } finally {
        this.loading = false
        if (type === 'refresh') {
          uni.stopPullDownRefresh()
        }
      }
    },
    // 搜索
    handleQuery() {
      this.queryParams.pageNum = 1
      this.userOnlineList = []
      this.hasMore = true
      this.loadMoreStatus = 'more'
      this.getList('refresh')
    },

    // 重置
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        username: undefined
      }
      this.handleQuery()
    },

    // 下拉刷新
    onPullDownRefresh() {
      this.queryParams.pageNum = 1
      this.userOnlineList = []
      this.hasMore = true
      this.getList('refresh')
    },

    // 上拉加载更多
    onReachBottom() {
      if (this.hasMore && !this.loading) {
        this.queryParams.pageNum++
        this.getList('more')
      }
    }
  }
}
</script>

<style lang="scss">
.user-online-container {
  padding: 20rpx;
  background-color: #f5f5f5;
  min-height: 100vh;

  .search-box {
    background-color: #fff;
    border-radius: 16rpx;
    padding: 24rpx;
    margin-bottom: 20rpx;
    box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);

    .search-form {
      .search-item {
        display: flex;
        align-items: center;
        margin-bottom: 20rpx;

        &:last-child {
          margin-bottom: 0;
        }

        .label {
          width: 140rpx;
          font-size: 28rpx;
          color: #606266;
          font-weight: 500;
        }

        input {
          flex: 1;
          height: 72rpx;
          background-color: #f8f9fa;
          border-radius: 8rpx;
          padding: 0 24rpx;
          font-size: 28rpx;
          color: #2c3e50;

          &::placeholder {
            color: #909399;
          }
        }

        .picker-box {
          flex: 1;
          height: 72rpx;
          background-color: #f8f9fa;
          border-radius: 8rpx;
          padding: 0 24rpx;
          font-size: 28rpx;
          display: flex;
          align-items: center;
          justify-content: space-between;

          text {
            color: #2c3e50;

            &.placeholder {
              color: #909399;
            }
          }

          .uni-icons {
            margin-left: 12rpx;
          }
        }
      }
    }

    .btn-group {
      display: flex;
      gap: 20rpx;
      margin-top: 30rpx;
      padding-top: 30rpx;
      border-top: 2rpx solid #f0f0f0;

      .btn {
        flex: 1;
        height: 80rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 12rpx;
        font-size: 28rpx;
        border-radius: 8rpx;
        transition: all 0.3s;

        &:active {
          transform: scale(0.98);
          opacity: 0.9;
        }

        &.search-btn {
          background-color: #409eff;
          color: #fff;

          &:active {
            background-color: #3a8ee6;
          }
        }

        &.reset-btn {
          background-color: #f5f7fa;
          color: #606266;
          border: 2rpx solid #dcdfe6;

          &:active {
            background-color: #e9e9eb;
          }
        }

        &.add-btn {
          background-color: #67c23a;
          color: #fff;

          &:active {
            background-color: #5daf34;
          }
        }
      }
    }
  }

  .user-online-list {
    .user-online-item {
      position: relative;
      background-color: #fff;
      padding: 32rpx;
      margin-bottom: 24rpx;
      border-radius: 16rpx;
      box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);

      .user-online-info {
        padding-right: 100rpx; // 为操作按钮预留空间

        .info-row {
          display: flex;
          align-items: center; // 核心：让label和value垂直居中
          margin-bottom: 16rpx;
          line-height: 1.5; // 统一行高，避免文字行高不一致导致错位

          &:last-child {
            margin-bottom: 0;
          }

          .label {
            width: 140rpx;
            font-size: 28rpx;
            color: #606266;
            flex-shrink: 0; // 防止label宽度被压缩
            text-align: justify; // 让label文字对齐更整齐（可选）
          }

          .value {
            flex: 1;
            font-size: 28rpx;
            color: #2c3e50;
            font-weight: 500;
            // 可选：如果value文字过长，自动换行并对齐
            word-break: break-all;

            &.text-success {
              color: #67c23a;
            }

            &.text-danger {
              color: #f56c6c;
            }
          }
        }
      }

      .user-online-actions {
        position: absolute;
        right: 32rpx;
        top: 32rpx;
        display: flex;
        gap: 16rpx;

        .action-btn {
          width: 56rpx;
          height: 56rpx;
          padding: 0;
          display: flex;
          align-items: center;
          justify-content: center;
          border-radius: 28rpx;
          background-color: #f8f9fa;
          border: none;

          &:active {
            opacity: 0.8;
          }

          &.edit-btn {
            &:active {
              background-color: #ecf5ff;
            }
          }

          &.delete-btn {
            &:active {
              background-color: #fef0f0;
            }
          }
        }
      }

      &:active {
        opacity: 0.9;
      }
    }
  }

  .empty-box {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 120rpx 0;
    background-color: #fff;
    border-radius: 16rpx;
    margin-top: 24rpx;

    .empty-text {
      margin-top: 24rpx;
      font-size: 28rpx;
      color: #909399;
      letter-spacing: 1rpx;
    }
  }
}
</style>


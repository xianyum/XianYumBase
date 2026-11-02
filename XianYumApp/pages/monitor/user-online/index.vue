<template>
  <view class="user-online-container">
    <view class="top-bar">
      <view class="search-input-wrap">
        <uni-icons type="search" size="16" color="#909399"></uni-icons>
        <input
            v-model="queryParams.username"
            type="text"
            placeholder="搜索账号"
            confirm-type="search"
            @confirm="handleQuery"
        />
        <uni-icons
            v-if="queryParams.username"
            type="clear"
            size="14"
            color="#c0c4cc"
            @tap="queryParams.username = ''; handleQuery()"
        ></uni-icons>
      </view>
      <view class="filter-btn" :class="{ active: hasFilter }" @tap="showFilter = true">
        <uni-icons type="list" size="18" color="#606266"></uni-icons>
      </view>
    </view>

    <view class="filter-drawer" v-if="showFilter" @tap="showFilter = false">
      <view class="drawer-mask"></view>
      <view class="drawer-content" @tap.stop="">
        <view class="drawer-header">
          <text class="drawer-title">筛选条件</text>
          <uni-icons type="closeempty" size="20" color="#909399" @tap="showFilter = false"></uni-icons>
        </view>
        <scroll-view scroll-y class="drawer-body">
          <view class="filter-group">
            <view class="filter-label">登录类型</view>
            <view class="chip-list">
              <view
                  class="chip"
                  :class="{ active: !queryParams.loginType }"
                  @tap="selectLoginType(null)"
              >全部</view>
              <view
                  v-for="item in loginTypeOptions"
                  :key="item.dictValue"
                  class="chip"
                  :class="{ active: queryParams.loginType === item.dictValue }"
                  @tap="selectLoginType(item.dictValue)"
              >{{ item.dictLabel }}</view>
            </view>
          </view>
        </scroll-view>
        <view class="drawer-footer">
          <view class="drawer-btn reset" @tap="resetQuery">重置</view>
          <view class="drawer-btn confirm" @tap="handleFilterConfirm">确定</view>
        </view>
      </view>
    </view>

    <view class="user-online-list" v-if="userOnlineList.length > 0">
      <view v-for="(item, index) in userOnlineList" :key="index" class="user-online-item">
        <view class="user-online-info">
          <view class="info-row">
            <text class="label">登录账号：</text>
            <text class="value">
              {{ item.username }}
              <text v-if="item.loginType" class="login-type-tag">({{ getDictLabel('sys_user_login_type', item.loginType) }})</text>
            </text>
          </view>
          <view class="info-row">
            <text class="label">登录地点：</text>
            <uni-tooltip placement="top" :content="`${item.loginLocation}(${item.ipaddr})`">
              <text class="value text-ellipsis">
                {{ item.loginLocation }}({{item.ipaddr}})
              </text>
            </uni-tooltip>
          </view>
          <view class="info-row">
            <text class="label">登录时间：</text>
            <uni-dateformat class="value" :date="item.loginTime" format="yyyy-MM-dd hh:mm:ss"></uni-dateformat>
          </view>
          <view class="info-row">
            <text class="label">登录系统：</text>
            <text class="value" :class="{ bold: isCurrentDevice(item) }">
              {{ item.os + (isCurrentDevice(item) ? '（本机）' : '') }}
            </text>
          </view>
          <view class="info-row">
            <text class="label">过期时间：</text>
            <text class="value">{{item.expireStr}}</text>
          </view>
        </view>
        <view class="user-online-actions">
          <button class="action-btn delete-btn" @tap.stop="handleDelete(item)" v-if="checkPermi(['monitor:online:exit'])">
            <uni-icons type="trash" size="14" color="#f56c6c"></uni-icons>
          </button>
        </view>
      </view>

      <uni-load-more :status="loadMoreStatus" :icon-size="16" :content-text="contentText" />
    </view>

    <view v-else class="empty-box">
      <uni-icons type="info" size="50" color="#999"></uni-icons>
      <text class="empty-text">暂无数据</text>
    </view>
  </view>
</template>

<script>
import { queryUserOnlinePage, forceLogout } from '@/api/monitor/user-online'
import { checkPermi } from '@/utils/permission'
import { getDictData, getDictLabel } from '@/utils/dict'

export default {
  data() {
    return {
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        username: undefined,
        loginType: undefined
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
      },
      loginTypeOptions: [],
      showFilter: false
    }
  },
  computed: {
    hasFilter() {
      return !!(this.queryParams.loginType)
    }
  },
  onLoad(options) {
    if (options.pageTitle) {
      uni.setNavigationBarTitle({
        title: options.pageTitle
      })
    }
    this.initDictData()
    this.getList()
  },
  methods: {
    async initDictData() {
      this.loginTypeOptions = await getDictData('sys_user_login_type') || []
    },
    getDictLabel,
    checkPermi,
    selectLoginType(val) {
      this.queryParams.loginType = val || undefined
    },
    handleFilterConfirm() {
      this.showFilter = false
      this.handleQuery()
    },
    async handleDelete(item) {
      const res = await this.$modal.confirm(`确定踢出【${item.username}】吗？`)
      if (res) {
        let tokenLists = [item.token]
        const result = await forceLogout(tokenLists)
        if (result.code === 200) {
          this.$modal.msgSuccess('踢出成功')
          this.handleQuery()
        }
      }
    },
    isCurrentDevice(item) {
      if (!item?.token || !this.$store.getters.token) return false
      return item.token === this.$store.getters.token;
    },
    async getList(type = 'more') {
      if (this.loading) return
      this.loading = true
      try {
        const response = await queryUserOnlinePage(this.queryParams);
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
      } catch (error) {
        this.loadMoreStatus = 'more'
      } finally {
        this.loading = false
        if (type === 'refresh') {
          uni.stopPullDownRefresh()
        }
      }
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.userOnlineList = []
      this.hasMore = true
      this.loadMoreStatus = 'more'
      this.getList('refresh')
    },
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        username: undefined,
        loginType: undefined
      }
      this.showFilter = false
      this.handleQuery()
    },
    onPullDownRefresh() {
      this.queryParams.pageNum = 1
      this.userOnlineList = []
      this.hasMore = true
      this.getList('refresh')
    },
    onReachBottom() {
      if (this.hasMore && !this.loading) {
        this.queryParams.pageNum++
        this.getList('more')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
$color-main: #409eff;
$color-success: #67c23a;
$color-gray: #909399;
$color-text: #303133;
$color-text-light: #606266;
$color-border: #ebeef5;
$color-bg: #f5f5f5;
$color-card: #fff;

.login-type-tag {
  color: #909399;
  font-size: 24rpx;
  font-weight: normal;
  margin-left: 8rpx;
}

.user-online-container {
  background-color: $color-bg;
  min-height: 100vh;

  .top-bar {
    position: sticky;
    top: 0;
    z-index: 100;
    display: flex;
    align-items: center;
    gap: 20rpx;
    padding: 20rpx 24rpx;
    background-color: $color-card;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);

    .search-input-wrap {
      flex: 1;
      display: flex;
      align-items: center;
      gap: 12rpx;
      height: 72rpx;
      padding: 0 24rpx;
      background-color: #f5f7fa;
      border-radius: 36rpx;

      input {
        flex: 1;
        font-size: 28rpx;
        color: $color-text;
        background: transparent;
        height: 100%;

        &::placeholder {
          color: #c0c4cc;
        }
      }
    }

    .filter-btn {
      width: 72rpx;
      height: 72rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      background-color: #f5f7fa;
      border-radius: 50%;
      flex-shrink: 0;
      position: relative;

      &.active {
        background-color: rgba($color-main, 0.1);

        &::after {
          content: '';
          position: absolute;
          top: 12rpx;
          right: 12rpx;
          width: 14rpx;
          height: 14rpx;
          background-color: $color-main;
          border-radius: 50%;
        }
      }

      &:active {
        opacity: 0.7;
      }
    }
  }

  .filter-drawer {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 2000;

    .drawer-mask {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background-color: rgba(0, 0, 0, 0.45);
    }

    .drawer-content {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      background-color: $color-card;
      border-radius: 24rpx 24rpx 0 0;
      display: flex;
      flex-direction: column;
      max-height: 70vh;

      .drawer-header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 32rpx;
        border-bottom: 1rpx solid $color-border;
        flex-shrink: 0;

        .drawer-title {
          font-size: 32rpx;
          font-weight: 600;
          color: $color-text;
        }
      }

      .drawer-body {
        flex: 1;
        height: 0;
        padding: 24rpx 32rpx;

        .filter-group {
          margin-bottom: 32rpx;

          &:last-child {
            margin-bottom: 0;
          }

          .filter-label {
            font-size: 28rpx;
            color: $color-text-light;
            margin-bottom: 20rpx;
          }

          .chip-list {
            display: flex;
            flex-wrap: wrap;
            gap: 16rpx;

            .chip {
              padding: 14rpx 32rpx;
              background-color: #f5f7fa;
              border-radius: 8rpx;
              font-size: 26rpx;
              color: $color-text-light;
              transition: all 0.2s;

              &.active {
                background-color: rgba($color-main, 0.1);
                color: $color-main;
                border: 1rpx solid $color-main;
              }

              &:active {
                opacity: 0.7;
              }
            }
          }
        }
      }

      .drawer-footer {
        display: flex;
        gap: 24rpx;
        padding: 24rpx 32rpx;
        padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
        border-top: 1rpx solid $color-border;
        flex-shrink: 0;

        .drawer-btn {
          flex: 1;
          height: 84rpx;
          line-height: 84rpx;
          text-align: center;
          border-radius: 12rpx;
          font-size: 30rpx;

          &.reset {
            background-color: #f5f7fa;
            color: $color-text-light;

            &:active {
              background-color: #e9e9eb;
            }
          }

          &.confirm {
            background-color: $color-main;
            color: #fff;

            &:active {
              background-color: darken($color-main, 5%);
            }
          }
        }
      }
    }
  }

  .user-online-list {
    padding: 20rpx 24rpx 40rpx;

    .user-online-item {
      position: relative;
      background-color: $color-card;
      padding: 32rpx;
      margin-bottom: 24rpx;
      border-radius: 16rpx;
      box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);

      .user-online-info {
        padding-right: 100rpx;

        .info-row {
          display: flex;
          align-items: center;
          margin-bottom: 16rpx;
          line-height: 1.5;

          &:last-child {
            margin-bottom: 0;
          }

          .label {
            width: 140rpx;
            font-size: 28rpx;
            color: $color-gray;
            flex-shrink: 0;
          }

          .value {
            flex: 1;
            font-size: 28rpx;
            color: $color-text;
            font-weight: 500;
            word-break: break-all;

            &.text-success {
              color: $color-success;
            }

            &.text-ellipsis {
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }

            &.bold {
              font-weight: 700 !important;
              color: $color-main;
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
    background-color: $color-card;
    border-radius: 16rpx;
    margin: 24rpx;

    .empty-text {
      margin-top: 24rpx;
      font-size: 28rpx;
      color: $color-gray;
      letter-spacing: 1rpx;
    }
  }
}
</style>
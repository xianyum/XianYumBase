<template>
  <view class="apikey-container">
    <view class="top-bar">
      <view class="search-input-wrap">
        <uni-icons type="search" size="16" color="#909399"></uni-icons>
        <input
            v-model="queryParams.title"
            type="text"
            placeholder="搜索名称"
            confirm-type="search"
            @confirm="handleQuery"
        />
        <uni-icons
            v-if="queryParams.title"
            type="clear"
            size="14"
            color="#c0c4cc"
            @tap="queryParams.title = ''; handleQuery()"
        ></uni-icons>
      </view>
      <view class="add-btn" v-if="checkPermi(['system:apiKey:save'])" @tap="handleAdd">
        <uni-icons type="plusempty" size="18" color="#67c23a"></uni-icons>
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
            <view class="filter-label">ApiKey</view>
            <view class="filter-input-wrap">
              <input
                  v-model="queryParams.apiKey"
                  type="text"
                  placeholder="请输入完整 ApiKey"
              />
            </view>
          </view>
          <view class="filter-group">
            <view class="filter-label">状态</view>
            <view class="chip-list">
              <view
                  class="chip"
                  :class="{ active: queryParams.isValid === undefined }"
                  @tap="selectValid(undefined)"
              >全部</view>
              <view
                  class="chip"
                  :class="{ active: queryParams.isValid === true }"
                  @tap="selectValid(true)"
              >生效</view>
              <view
                  class="chip"
                  :class="{ active: queryParams.isValid === false }"
                  @tap="selectValid(false)"
              >禁用</view>
            </view>
          </view>
        </scroll-view>
        <view class="drawer-footer">
          <view class="drawer-btn reset" @tap="resetQuery">重置</view>
          <view class="drawer-btn confirm" @tap="handleFilterConfirm">确定</view>
        </view>
      </view>
    </view>

    <view class="apikey-list" v-if="apiKeyList.length > 0">
      <view v-for="item in apiKeyList" :key="item.id" class="apikey-item" @tap="handleView(item)">
        <view class="item-header">
          <text class="item-title">{{ item.title || '未命名' }}</text>
          <view class="header-right">
            <view class="stat-badge stat-badge-today">
              <text class="badge-label">今日</text>
              <text class="badge-value">{{ formatCount(item.todayRequestCount) }}</text>
            </view>
            <view class="stat-badge stat-badge-total">
              <text class="badge-label">总</text>
              <text class="badge-value">{{ formatCount(item.totalRequestCount) }}</text>
            </view>
            <view class="status-tag" :class="item.isValid ? 'status-success' : 'status-danger'">
              {{ item.isValid ? '生效' : '禁用' }}
            </view>
            <view
                v-if="checkPermi(['system:apiKey:update']) || checkPermi(['system:apiKey:delete'])"
                class="more-btn"
                @tap.stop="openActionMenu(item)"
            >
              <uni-icons type="more-filled" size="18" color="#909399"></uni-icons>
            </view>
            <uni-icons v-else type="right" size="14" color="#c0c4cc"></uni-icons>
          </view>
        </view>

        <view class="intro" v-if="item.intro">{{ item.intro }}</view>

        <view class="meta-list">
          <view class="meta-row">
            <text class="label">归属账号</text>
            <text class="value">{{ getUserLabel(item) }}</text>
          </view>
          <view class="meta-row">
            <text class="label">授权范围</text>
            <view class="value scope-wrap">
              <text v-for="scope in getVisibleScopes(item.scopes)" :key="scope" class="scope-tag">{{ scope }}</text>
              <text v-if="getMoreScopeCount(item.scopes) > 0" class="scope-more">
                +{{ getMoreScopeCount(item.scopes) }}
              </text>
              <text v-if="!item.scopes || item.scopes.length === 0">-</text>
            </view>
          </view>
          <view class="meta-row">
            <text class="label">到期时间</text>
            <text class="value">{{ isNeverExpires(item.expiresTime) ? '永不过期' : formatTime(item.expiresTime) }}</text>
          </view>
        </view>
      </view>

      <uni-load-more :status="loadMoreStatus" :icon-size="16" :content-text="contentText" />
    </view>

    <view v-else class="empty-box">
      <uni-icons type="info" size="50" color="#999"></uni-icons>
      <text class="empty-text">暂无 ApiKey 数据</text>
    </view>

    <view v-if="showDetail" class="detail-modal" @tap="showDetail = false">
      <view class="detail-content" @tap.stop="">
        <view class="detail-header">
          <text class="detail-title">ApiKey详情</text>
          <uni-icons type="close" size="20" color="#909399" @tap="showDetail = false"></uni-icons>
        </view>
        <scroll-view scroll-y class="detail-body">
          <view class="detail-row">
            <text class="row-label">名称</text>
            <text class="row-value">{{ detailData.title || '-' }}</text>
          </view>
          <view class="detail-row">
            <text class="row-label">ApiKey</text>
            <view class="key-block">
              <text class="key-text">{{ detailKeyRevealed ? (detailData.apiKey || '-') : maskApiKey(detailData.apiKey) }}</text>
              <view class="key-actions">
                <view class="key-btn" @tap="detailKeyRevealed = !detailKeyRevealed">
                  <uni-icons
                      :type="detailKeyRevealed ? 'eye-filled' : 'eye-slash-filled'"
                      size="14"
                      color="#606266"
                  ></uni-icons>
                  <text>{{ detailKeyRevealed ? '隐藏' : '显示' }}</text>
                </view>
                <view class="key-btn primary" @tap="handleCopy(detailData)">复制</view>
              </view>
            </view>
          </view>
          <view class="detail-row">
            <text class="row-label">归属账号</text>
            <text class="row-value">{{ getUserLabel(detailData) }}</text>
          </view>
          <view class="detail-row">
            <text class="row-label">授权范围</text>
            <view class="row-value scope-wrap">
              <text v-for="scope in detailData.scopes" :key="scope" class="scope-tag">{{ scope }}</text>
              <text v-if="!detailData.scopes || detailData.scopes.length === 0">-</text>
            </view>
          </view>
          <view class="detail-row">
            <text class="row-label">到期时间</text>
            <text class="row-value">{{ isNeverExpires(detailData.expiresTime) ? '永不过期' : formatTime(detailData.expiresTime) }}</text>
          </view>
          <view class="detail-row">
            <text class="row-label">状态</text>
            <text class="row-value" :class="detailData.isValid ? 'text-success' : 'text-danger'">
              {{ detailData.isValid ? '生效' : '禁用' }}
            </text>
          </view>
          <view class="detail-row">
            <text class="row-label">今日请求</text>
            <text class="row-value">{{ formatCount(detailData.todayRequestCount) }}</text>
          </view>
          <view class="detail-row">
            <text class="row-label">总请求</text>
            <text class="row-value">{{ formatCount(detailData.totalRequestCount) }}</text>
          </view>
          <view class="detail-row">
            <text class="row-label">创建时间</text>
            <text class="row-value">{{ formatTime(detailData.createTime) || '-' }}</text>
          </view>
          <view class="detail-row" v-if="detailData.intro">
            <text class="row-label">介绍</text>
            <text class="row-value">{{ detailData.intro }}</text>
          </view>
        </scroll-view>
      </view>
    </view>

    <view v-if="showActionMenu" class="action-modal" @tap="showActionMenu = false">
      <view class="action-sheet" @tap.stop="">
        <view class="action-header">
          <text class="action-title">{{ actionData.title || '未命名' }}</text>
        </view>
        <view
            v-if="checkPermi(['system:apiKey:update'])"
            class="action-item"
            @tap="handleEditFromMenu"
        >修改</view>
        <view
            v-if="checkPermi(['system:apiKey:delete'])"
            class="action-item danger"
            @tap="handleDeleteFromMenu"
        >删除</view>
        <view class="action-cancel" @tap="showActionMenu = false">取消</view>
      </view>
    </view>
  </view>
</template>

<script>
import { listApiKey, getApiKey, delApiKey } from '@/api/system/apiKey'
import { listUser } from '@/api/system/user'
import { checkPermi } from '@/utils/permission'
import { formatTime } from '@/utils/dateFormat'

export default {
  data() {
    return {
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: undefined,
        apiKey: undefined,
        isValid: undefined
      },
      apiKeyList: [],
      userMap: {},
      showDetail: false,
      detailData: {},
      detailKeyRevealed: false,
      showActionMenu: false,
      actionData: {},
      loading: false,
      hasMore: true,
      total: 0,
      loadMoreStatus: 'more',
      contentText: {
        contentdown: '上拉显示更多',
        contentrefresh: '正在加载...',
        contentnomore: '没有更多数据了'
      },
      showFilter: false
    }
  },
  computed: {
    hasFilter() {
      return !!(this.queryParams.apiKey || this.queryParams.isValid !== undefined)
    }
  },
  onLoad(options) {
    if (options.pageTitle) {
      uni.setNavigationBarTitle({
        title: options.pageTitle
      })
    }
    this.getUserMap()
    this.getList()
  },
  methods: {
    checkPermi,
    formatTime,
    isNeverExpires(value) {
      if (value === null || value === undefined || value === '') {
        return true
      }
      return Number(value) === -1
    },
    getUserLabel(item) {
      return this.userMap[String(item.loginId)] || item.loginId || '-'
    },
    formatCount(value) {
      if (value === null || value === undefined || value === '') {
        return '-'
      }
      const num = Number(value)
      if (!isFinite(num) || num <= 0) {
        return '-'
      }
      if (num >= 10000) {
        return this.abbrNumber(num / 10000) + 'w'
      }
      if (num >= 1000) {
        return this.abbrNumber(num / 1000) + 'k'
      }
      return String(num)
    },
    abbrNumber(num) {
      return num.toFixed(1).replace(/\.0$/, '')
    },
    async getUserMap() {
      try {
        const response = await listUser({ pageNum: 1, pageSize: 10000 })
        const users = response.data || []
        const userMap = {}
        users.forEach(item => {
          userMap[String(item.id)] = item.username
        })
        this.userMap = userMap
      } catch (error) {
        // 无用户查询权限时仅展示 loginId
      }
    },
    selectValid(val) {
      this.queryParams.isValid = val
    },
    handleFilterConfirm() {
      this.showFilter = false
      this.handleQuery()
    },
    maskApiKey(apiKey) {
      return apiKey ? '************' : '-'
    },
    getVisibleScopes(scopes) {
      if (!scopes || scopes.length === 0) return []
      return scopes.slice(0, 3)
    },
    getMoreScopeCount(scopes) {
      return scopes && scopes.length > 3 ? scopes.length - 3 : 0
    },
    handleView(item) {
      this.detailData = item
      this.detailKeyRevealed = false
      this.showDetail = true
      this.loadDetail(item.id)
    },
    async loadDetail(id) {
      try {
        const response = await getApiKey(id)
        if (response.code === 200 && response.data) {
          this.detailData = response.data
        }
      } catch (error) {
        // 请求异常已由拦截器提示，保留列表数据展示
      }
    },
    openActionMenu(item) {
      this.actionData = item
      this.showActionMenu = true
    },
    handleEditFromMenu() {
      const item = this.actionData
      this.showActionMenu = false
      this.handleEdit(item)
    },
    handleDeleteFromMenu() {
      const item = this.actionData
      this.showActionMenu = false
      this.handleDelete(item)
    },
    handleCopy(item) {
      if (!item.apiKey) return
      uni.setClipboardData({
        data: item.apiKey,
        success: () => {
          this.$modal.msgSuccess('复制成功')
        }
      })
    },
    handleAdd() {
      this.$tab.navigateTo('/pages/system/apiKey/edit')
    },
    handleEdit(item) {
      this.$tab.navigateTo('/pages/system/apiKey/edit?id=' + item.id)
    },
    handleDelete(item) {
      this.$modal.confirm(`确定要删除【${item.title || item.apiKey}】吗？`).then(async () => {
        uni.showLoading({
          title: '删除中...',
          mask: true
        })
        try {
          const response = await delApiKey([item.id])
          if (response.code === 200) {
            this.$modal.msgSuccess('删除成功')
            this.handleQuery()
          }
        } catch (error) {
          // 请求异常已由拦截器提示
        } finally {
          uni.hideLoading()
        }
      }).catch(() => {})
    },
    async getList(type = 'more') {
      if (this.loading) return
      this.loading = true
      this.loadMoreStatus = 'loading'
      try {
        const response = await listApiKey(this.queryParams)
        if (response.code === 200) {
          const listData = response.data || []
          const total = response.total || 0

          if (type === 'refresh' || this.queryParams.pageNum === 1) {
            this.apiKeyList = listData
          } else {
            this.apiKeyList = [...this.apiKeyList, ...listData]
          }
          this.total = total
          this.hasMore = this.apiKeyList.length < total
          this.loadMoreStatus = this.hasMore ? 'more' : 'noMore'
        } else {
          this.loadMoreStatus = 'more'
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
      this.apiKeyList = []
      this.hasMore = true
      this.loadMoreStatus = 'more'
      this.getList('refresh')
    },
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        title: undefined,
        apiKey: undefined,
        isValid: undefined
      }
      this.showFilter = false
      this.handleQuery()
    },
    onPullDownRefresh() {
      this.queryParams.pageNum = 1
      this.apiKeyList = []
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
$color-danger: #f56c6c;
$color-gray: #909399;
$color-text: #303133;
$color-text-light: #606266;
$color-border: #ebeef5;
$color-bg: #f5f5f5;
$color-card: #fff;

.apikey-container {
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

    .add-btn {
      width: 72rpx;
      height: 72rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      background-color: rgba($color-success, 0.1);
      border-radius: 50%;
      flex-shrink: 0;

      &:active {
        opacity: 0.7;
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
      max-height: 75vh;

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

          .filter-input-wrap {
            height: 76rpx;
            background-color: #f5f7fa;
            border-radius: 12rpx;
            padding: 0 24rpx;
            display: flex;
            align-items: center;

            input {
              flex: 1;
              font-size: 28rpx;
              color: $color-text;
              background: transparent;

              &::placeholder {
                color: #c0c4cc;
              }
            }
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

  .apikey-list {
    padding: 20rpx 24rpx 40rpx;

    .apikey-item {
      background-color: $color-card;
      margin-bottom: 24rpx;
      border-radius: 16rpx;
      box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
      overflow: hidden;

      .item-header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 24rpx 28rpx;
        border-bottom: 1rpx solid #f0f2f5;

        .item-title {
          flex: 1;
          font-size: 30rpx;
          font-weight: 600;
          color: $color-text;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          margin-right: 16rpx;
        }

        .header-right {
          display: flex;
          align-items: center;
          gap: 12rpx;
          flex-shrink: 0;

          .stat-badge {
            display: flex;
            align-items: center;
            gap: 4rpx;
            padding: 6rpx 14rpx;
            border-radius: 999rpx;
            font-size: 22rpx;
            line-height: 1;
            flex-shrink: 0;
            white-space: nowrap;

            .badge-label {
              font-size: 20rpx;
              opacity: 0.8;
              flex-shrink: 0;
            }

            .badge-value {
              font-size: 22rpx;
              font-weight: 600;
            }

            &.stat-badge-today {
              color: $color-main;
              background-color: #ecf5ff;
            }

            &.stat-badge-total {
              color: $color-gray;
              background-color: #f4f4f5;
            }
          }

          .more-btn {
            width: 44rpx;
            height: 44rpx;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 50%;

            &:active {
              opacity: 0.6;
              background-color: #f5f7fa;
            }
          }
        }

        .status-tag {
          font-size: 24rpx;
          padding: 8rpx 20rpx;
          border-radius: 6rpx;
          flex-shrink: 0;

          &.status-success {
            background-color: #f0f9eb;
            color: $color-success;
          }

          &.status-danger {
            background-color: #fef0f0;
            color: $color-danger;
          }
        }
      }

      .intro {
        margin: 16rpx 28rpx 0;
        font-size: 26rpx;
        color: $color-gray;
        line-height: 1.5;
        word-break: break-all;
      }

      .meta-list {
        padding: 16rpx 28rpx 20rpx;

        .meta-row {
          display: flex;
          align-items: flex-start;
          padding: 6rpx 0;
          line-height: 1.6;

          .label {
            width: 130rpx;
            font-size: 24rpx;
            color: $color-gray;
            flex-shrink: 0;
          }

          .value {
            flex: 1;
            font-size: 24rpx;
            color: $color-text;
            word-break: break-all;
          }

          .scope-wrap {
            display: flex;
            flex-wrap: wrap;
            gap: 10rpx;

            .scope-tag {
              font-size: 22rpx;
              color: $color-main;
              background-color: #ecf5ff;
              padding: 4rpx 14rpx;
              border-radius: 6rpx;
            }

            .scope-more {
              font-size: 22rpx;
              color: $color-gray;
              background-color: #f5f7fa;
              padding: 4rpx 14rpx;
              border-radius: 6rpx;
            }
          }
        }
      }

      &:active {
        opacity: 0.95;
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

.detail-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 1000;
  display: flex;
  align-items: flex-end;

  .detail-content {
    width: 100%;
    height: 65vh;
    background-color: $color-card;
    border-radius: 24rpx 24rpx 0 0;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    padding-bottom: env(safe-area-inset-bottom);
    box-sizing: border-box;

    .detail-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 32rpx;
      border-bottom: 1rpx solid $color-border;
      flex-shrink: 0;

      .detail-title {
        font-size: 32rpx;
        font-weight: 600;
        color: $color-text;
      }
    }

    .detail-body {
      flex: 1;
      height: 0;
      padding: 8rpx 32rpx 32rpx;
      box-sizing: border-box;
    }

    .detail-row {
      display: flex;
      align-items: flex-start;
      padding: 20rpx 0;
      border-bottom: 1rpx solid #f5f5f5;

      &:last-child {
        border-bottom: none;
      }

      .row-label {
        width: 140rpx;
        font-size: 26rpx;
        color: $color-gray;
        flex-shrink: 0;
      }

      .row-value {
        flex: 1;
        font-size: 26rpx;
        color: $color-text;
        word-break: break-all;

        &.text-success {
          color: $color-success;
        }

        &.text-danger {
          color: $color-danger;
        }
      }

      .key-block {
        flex: 1;
        min-width: 0;

        .key-text {
          display: block;
          padding: 16rpx 20rpx;
          font-size: 24rpx;
          color: $color-text-light;
          font-family: monospace;
          background-color: #f5f7fa;
          border-radius: 10rpx;
          word-break: break-all;
          line-height: 1.5;
        }

        .key-actions {
          display: flex;
          justify-content: flex-end;
          gap: 16rpx;
          margin-top: 16rpx;

          .key-btn {
            display: flex;
            align-items: center;
            gap: 6rpx;
            padding: 8rpx 24rpx;
            font-size: 24rpx;
            color: $color-text-light;
            background-color: #f5f7fa;
            border-radius: 8rpx;

            &.primary {
              color: $color-main;
              background-color: #ecf5ff;
            }

            &:active {
              opacity: 0.7;
            }
          }
        }
      }

      .scope-wrap {
        display: flex;
        flex-wrap: wrap;
        gap: 10rpx;

        .scope-tag {
          font-size: 22rpx;
          color: $color-main;
          background-color: #ecf5ff;
          padding: 4rpx 14rpx;
          border-radius: 6rpx;
        }
      }
    }
  }
}

.action-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 2500;
  display: flex;
  align-items: flex-end;

  .action-sheet {
    width: 100%;
    background-color: $color-card;
    border-radius: 24rpx 24rpx 0 0;
    padding: 0 24rpx;
    padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
    box-sizing: border-box;

    .action-header {
      padding: 28rpx 8rpx;
      text-align: center;
      border-bottom: 1rpx solid $color-border;

      .action-title {
        font-size: 26rpx;
        color: $color-gray;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }

    .action-item {
      height: 96rpx;
      line-height: 96rpx;
      text-align: center;
      font-size: 30rpx;
      color: $color-text;
      border-bottom: 1rpx solid #f5f5f5;

      &.danger {
        color: $color-danger;
      }

      &:active {
        background-color: #fafafa;
      }
    }

    .action-cancel {
      margin-top: 16rpx;
      height: 96rpx;
      line-height: 96rpx;
      text-align: center;
      font-size: 30rpx;
      color: $color-text-light;
      background-color: #f5f7fa;
      border-radius: 16rpx;

      &:active {
        opacity: 0.8;
      }
    }
  }
}
</style>

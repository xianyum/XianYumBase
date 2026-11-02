<template>
  <view class="energy-consume-container">
    <view class="top-bar">
      <view class="search-input-wrap" @tap="showFilter = true">
        <uni-icons type="calendar" size="16" color="#909399"></uni-icons>
        <text class="filter-display">{{ dateRangeText || '驾驶日期筛选' }}</text>
      </view>
      <view class="add-btn" @tap="handleAdd">
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
            <view class="filter-label">驾驶日期</view>
            <view class="chip-list">
              <view
                  class="chip"
                  :class="{ active: pendingQuickDateType === 'month' }"
                  @tap="pendingQuickDateType = 'month'"
              >当月</view>
              <view
                  class="chip"
                  :class="{ active: pendingQuickDateType === 'lastMonth' }"
                  @tap="pendingQuickDateType = 'lastMonth'"
              >上个月</view>
              <view
                  class="chip"
                  :class="{ active: pendingQuickDateType === 'year' }"
                  @tap="pendingQuickDateType = 'year'"
              >近一年</view>
            </view>
          </view>
        </scroll-view>
        <view class="drawer-footer">
          <view class="drawer-btn reset" @tap="resetQuery">重置</view>
          <view class="drawer-btn confirm" @tap="handleFilterConfirm">确定</view>
        </view>
      </view>
    </view>

    <view class="energy-consume-list" v-if="energyConsumeList.length > 0">
      <view class="energy-consume-item summary-item">
        <view class="summary-header">
          <uni-icons type="barchart" size="18" color="#409eff"></uni-icons>
          <text class="summary-title">合计</text>
        </view>
        <view class="energy-consume-info summary-content">
          <view class="info-row">
            <text class="label">总行驶公里数：</text>
            <text class="value">{{ summaryInfo.totalDistanceKm}}公里</text>
          </view>
          <view class="info-row">
            <text class="label">总消耗电量：</text>
            <text class="value">{{ summaryInfo.totalElectricityConsumed}}度</text>
          </view>
          <view class="info-row">
            <text class="label">每公里电量消耗：</text>
            <text class="value">{{ summaryInfo.electricityPerKm}}度/公里</text>
          </view>
        </view>
      </view>

      <view v-for="(item, index) in energyConsumeList" :key="index" class="energy-consume-item">
        <view class="energy-consume-info">
          <view class="info-row">
            <text class="label">驾驶日期：</text>
            <uni-dateformat class="value" :date="item.driveDate" format="yyyy-MM-dd"></uni-dateformat>
          </view>
          <view class="info-row">
            <text class="label">行驶公里数：</text>
            <text class="value">{{ item.distanceKm }}公里</text>
          </view>
          <view class="info-row">
            <text class="label">消耗电量：</text>
            <text class="value">{{ item.electricityConsumed }}度</text>
          </view>
          <view class="info-row">
            <text class="label">每公里电量消耗：</text>
            <text class="value">{{ item.electricityPerKm }}度/公里</text>
          </view>
        </view>

        <view class="user-online-actions" v-if="checkPermi(['ev-drive-records:delete'])">
          <button class="action-btn delete-btn" @tap.stop="handleDelete(item)">
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
import { formatTime } from "@/utils/dateFormat";
import { getEvDriveRecordsPage,delEvDriveRecords } from "@/api/ev-drive/list";
import { checkPermi } from '@/utils/permission'

const DEFAULT_QUICK_DATE_TYPE = 'month';

export default {
  data() {
    return {
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        params: {
          beginTime: undefined,
          endTime: undefined
        }
      },
      dateRangeText: '',
      quickDateType: DEFAULT_QUICK_DATE_TYPE,
      pendingQuickDateType: DEFAULT_QUICK_DATE_TYPE,
      energyConsumeList: [],
      loading: false,
      hasMore: true,
      total: 0,
      loadMoreStatus: 'more',
      contentText: {
        contentdown: '上拉显示更多',
        contentrefresh: '正在加载...',
        contentnomore: '没有更多数据了'
      },
      summaryInfo: {
        totalDistanceKm: 0,
        totalElectricityConsumed: 0,
        electricityPerKm: 0
      },
      showFilter: false
    }
  },
  computed: {
    hasFilter() {
      return this.quickDateType !== DEFAULT_QUICK_DATE_TYPE;
    }
  },
  watch: {
    showFilter(val) {
      if (val) {
        this.pendingQuickDateType = this.quickDateType;
      }
    }
  },
  onLoad(options) {
    if (options.pageTitle) {
      uni.setNavigationBarTitle({
        title: options.pageTitle
      });
    }
    this.applyQuickDate(DEFAULT_QUICK_DATE_TYPE);
    this.handleQuery();
  },
  methods: {
    checkPermi,
    handleAdd() {
      this.$tab.navigateTo('/pages/ev-drive/list/add')
    },
    formatDateTime(date, isStart) {
      const fmt = isStart ? '{y}-{m}-{d} 00:00:00' : '{y}-{m}-{d} 23:59:59';
      return formatTime(date, fmt);
    },
    applyQuickDate(type) {
      this.quickDateType = type;
      const now = new Date();
      let beginTime, endTime, beginText, endText;

      switch(type) {
        case 'month': {
          const monthFirst = new Date(now.getFullYear(), now.getMonth(), 1);
          const monthLast = new Date(now.getFullYear(), now.getMonth() + 1, 0);
          beginTime = this.formatDateTime(monthFirst, true);
          endTime = this.formatDateTime(monthLast, false);
          beginText = formatTime(monthFirst, '{y}-{m}-{d}');
          endText = formatTime(monthLast, '{y}-{m}-{d}');
          break;
        }
        case 'lastMonth': {
          const lastMonthFirst = new Date(now.getFullYear(), now.getMonth() - 1, 1);
          const lastMonthLast = new Date(now.getFullYear(), now.getMonth(), 0);
          beginTime = this.formatDateTime(lastMonthFirst, true);
          endTime = this.formatDateTime(lastMonthLast, false);
          beginText = formatTime(lastMonthFirst, '{y}-{m}-{d}');
          endText = formatTime(lastMonthLast, '{y}-{m}-{d}');
          break;
        }
        case 'year': {
          const lastYear = new Date(now.getTime() - 365 * 24 * 60 * 60 * 1000);
          beginTime = this.formatDateTime(lastYear, true);
          endTime = this.formatDateTime(now, false);
          beginText = formatTime(lastYear, '{y}-{m}-{d}');
          endText = formatTime(now, '{y}-{m}-{d}');
          break;
        }
      }
      this.queryParams.params.beginTime = beginTime;
      this.queryParams.params.endTime = endTime;
      this.dateRangeText = `${beginText} 至 ${endText}`;
    },
    handleFilterConfirm() {
      this.showFilter = false;
      if (this.pendingQuickDateType !== this.quickDateType) {
        this.applyQuickDate(this.pendingQuickDateType);
        this.handleQuery();
      }
    },
    async getEnergyConsumeList(type = 'more') {
      if (this.loading) return;
      this.loading = true;
      try {
        const response = await getEvDriveRecordsPage(this.queryParams);
        if (response.code === 200) {
          const list = response.data;
          const total = response.total;

          this.summaryInfo = {
            totalDistanceKm: response.otherInfo?.totalDistanceKm || 0,
            totalElectricityConsumed: response.otherInfo?.totalElectricityConsumed || 0,
            electricityPerKm: response.otherInfo?.electricityPerKm || 0
          };

          if (type === 'refresh' || this.queryParams.pageNum === 1) {
            this.energyConsumeList = list;
          } else {
            this.energyConsumeList = [...this.energyConsumeList, ...list];
          }
          this.total = total;
          this.hasMore = this.energyConsumeList.length < total;
          this.loadMoreStatus = this.hasMore ? 'more' : 'noMore';
        }
      } catch (error) {
        this.loadMoreStatus = 'more';
        this.$modal.msg('获取能耗数据错误');
      } finally {
        this.loading = false;
        if (type === 'refresh') {
          uni.stopPullDownRefresh();
        }
      }
    },
    async handleDelete(row) {
      const ids = [row.id]
      this.$modal.confirm('确定要删除这条能耗记录吗？').then(function() {
        return delEvDriveRecords(ids)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getEnergyConsumeList('refresh');
      }).catch(() => {
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.energyConsumeList = [];
      this.hasMore = true;
      this.loadMoreStatus = 'more';
      this.summaryInfo = { totalDistanceKm: 0, totalElectricityConsumed: 0, electricityPerKm: 0 };
      this.getEnergyConsumeList('refresh');
    },
    resetQuery() {
      this.pendingQuickDateType = DEFAULT_QUICK_DATE_TYPE;
      this.applyQuickDate(DEFAULT_QUICK_DATE_TYPE);
      this.showFilter = false;
      this.handleQuery();
    },
    onPullDownRefresh() {
      this.queryParams.pageNum = 1;
      this.energyConsumeList = [];
      this.hasMore = true;
      this.summaryInfo = { totalDistanceKm: 0, totalElectricityConsumed: 0, electricityPerKm: 0 };
      this.getEnergyConsumeList('refresh');
    },
    onReachBottom() {
      if (this.hasMore && !this.loading) {
        this.queryParams.pageNum++;
        this.getEnergyConsumeList('more');
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

.energy-consume-container {
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

      .filter-display {
        flex: 1;
        font-size: 28rpx;
        color: $color-text;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
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

  .energy-consume-list {
    padding: 24rpx;

    .energy-consume-item {
      position: relative;
      background-color: $color-card;
      padding: 32rpx;
      margin-bottom: 24rpx;
      border-radius: 16rpx;
      box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);

      .energy-consume-info {
        .info-row {
          display: flex;
          align-items: center;
          margin-bottom: 16rpx;
          line-height: 1.5;

          &:last-child { margin-bottom: 0; }

          .label {
            width: 240rpx;
            font-size: 28rpx;
            color: #606266;
            flex-shrink: 0;
          }

          .value {
            flex: 1;
            font-size: 28rpx;
            color: #2c3e50;
            font-weight: 500;
            word-break: break-all;
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

          &.delete-btn:active {
            background-color: #fef0f0;
          }
        }
      }

      &:active {
        opacity: 0.9;
      }
    }

    .summary-item {
      background: linear-gradient(135deg, #e8f4ff 0%, #f0f8ff 100%);
      margin-bottom: 36rpx;
      padding: 24rpx 32rpx 32rpx;
      box-shadow: 0 4rpx 16rpx rgba(64, 158, 255, 0.15);

      .summary-header {
        display: flex;
        align-items: center;
        gap: 12rpx;
        padding-bottom: 20rpx;
        margin-bottom: 20rpx;
        border-bottom: 2rpx solid rgba(64, 158, 255, 0.2);

        .summary-title {
          font-size: 30rpx;
          font-weight: 700;
        }
      }

      .summary-content {
        .info-row {
          margin-bottom: 10rpx;

          &:last-child {
            margin-bottom: 0;
          }

          .label {
            font-size: 30rpx;
            font-weight: 600;
            width: 260rpx;
          }

          .value {
            font-size: 30rpx;
            font-weight: 700;
            letter-spacing: 1rpx;
          }
        }
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
<template>
  <view class="energy-consume-container" @scroll="onPageScroll">
    <view class="search-box">
      <view class="search-form">
        <view class="search-item">
          <!-- 日期范围选择器 -->
          <text class="label">驾驶日期：</text>
          <view
              class="date-range-picker"
              :class="{ placeholder: !dateRangeText }"
              @tap="openDatePicker"
          >
            <text class="picker-text">{{ dateRangeText || '请选择日期范围' }}</text>
            <uni-icons type="arrowdown" size="16" color="#909399"></uni-icons>
          </view>
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
        <button class="btn add-btn" @tap="handleAdd">
          <uni-icons type="plusempty" size="14" color="#fff"></uni-icons>
          <text>新增</text>
        </button>
      </view>
    </view>

    <!-- 能耗列表 -->
    <view class="energy-consume-list" v-if="energyConsumeList.length > 0">
      <view class="energy-consume-item summary-item">
        <view class="energy-consume-info">
          <view class="info-row">
            <text class="label">合计：</text>
            <text class="value"></text>
          </view>
          <view class="info-row">
            <text class="label">总行驶公里数：</text>
            <text class="value">{{ summaryInfo.totalDistanceKm}}km</text>
          </view>
          <view class="info-row">
            <text class="label">总消耗电量：</text>
            <text class="value">{{ summaryInfo.totalElectricityConsumed}}kWh</text>
          </view>
          <view class="info-row">
            <text class="label">每公里电量消耗：</text>
            <text class="value">{{ summaryInfo.electricityPerKm}}kWh/km</text>
          </view>
        </view>
<!--        <view class="user-online-actions">-->
<!--          <button class="action-btn delete-btn" @tap.stop="handleDelete(item)" v-if="checkPermi(['monitor:online:exit'])">-->
<!--            <uni-icons type="trash" size="14" color="#f56c6c"></uni-icons>-->
<!--          </button>-->
<!--        </view>-->
      </view>

      <!-- 列表项 -->
      <view v-for="(item, index) in energyConsumeList" :key="index" class="energy-consume-item">
        <view class="energy-consume-info">
          <view class="info-row">
            <text class="label">驾驶日期：</text>
            <text class="value">{{ formatTime(item.driveDate,'{y}-{m}-{d}') }}</text>
          </view>
          <view class="info-row">
            <text class="label">行驶公里数：</text>
            <text class="value">{{ item.distanceKm }}km</text>
          </view>
          <view class="info-row">
            <text class="label">消耗电量：</text>
            <text class="value">{{ item.electricityConsumed }}kWh</text>
          </view>
          <view class="info-row">
            <text class="label">每公里电量消耗：</text>
            <text class="value">{{ item.electricityPerKm }}kWh/km</text>
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

    <!-- 仅保留快捷日期选择弹窗（移除自定义日期选择器） -->
    <uni-popup ref="quickDatePopupRef" type="bottom" :mask-click="false">
      <view class="quick-date-popup">
        <view class="popup-title">快捷选择</view>
        <view class="quick-date-list">
          <view
              class="quick-date-item"
              :class="{ active: quickDateType === 'month' }"
              @tap="selectQuickDate('month')"
          >
            当月
          </view>
          <view
              class="quick-date-item"
              :class="{ active: quickDateType === 'lastMonth' }"
              @tap="selectQuickDate('lastMonth')"
          >
            上个月
          </view>
          <view
              class="quick-date-item"
              :class="{ active: quickDateType === 'year' }"
              @tap="selectQuickDate('year')"
          >
            近一年
          </view>
        </view>
        <view class="popup-btn-group">
          <button class="popup-btn cancel-btn" @tap="closeQuickDatePopup">取消</button>
          <button class="popup-btn confirm-btn" @tap="confirmQuickDate">确定</button>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script>
import { formatTime } from "@/utils/dateFormat";
import { getEvDriveRecordsPage,delEvDriveRecords } from "@/api/ev-drive/list";

export default {
  data() {
    return {
      // 查询参数结构
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        params: {
          beginTime: undefined,
          endTime: undefined
        }
      },
      // 日期选择器相关
      dateRangeText: '', // 显示的日期范围文本
      startDate: '2020-01-01', // 选择器最小日期
      endDate: formatTime(new Date()), // 选择器最大日期
      // 快捷日期选择相关
      quickDateType: 'month', // 默认选中当月
      // 能耗数据列表
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
      // 合计信息
      summaryInfo: {
        totalDistanceKm: 0,
        totalElectricityConsumed: 0,
        electricityPerKm: 0
      }
    }
  },
  onLoad() {
    // 初始化默认日期范围（当月）
    this.initDefaultDateRange();
    this.getEnergyConsumeList();
  },
  methods: {
    formatTime,
    // 新增字典
    handleAdd() {
      this.$tab.navigateTo('/pages/ev-drive/list/add')
    },
    // 初始化默认日期范围（当月）
    initDefaultDateRange() {
      const now = new Date();
      // 当月第一天 00:00:00
      const monthFirstDay = new Date(now.getFullYear(), now.getMonth(), 1);
      // 当月最后一天 23:59:59
      const monthLastDay = new Date(now.getFullYear(), now.getMonth() + 1, 0);

      const begin = this.formatDateTime(monthFirstDay, true);
      const end = this.formatDateTime(monthLastDay, false);
      // 赋值到查询参数
      this.queryParams.params.beginTime = begin;
      this.queryParams.params.endTime = end;
      // 赋值到显示文本
      this.dateRangeText = `${formatTime(monthFirstDay, '{y}-{m}-{d}')} 至 ${formatTime(monthLastDay, '{y}-{m}-{d}')}`;
    },
    // 格式化日期为 yyyy-MM-dd HH:mm:ss 格式
    formatDateTime(date, isStart) {
      const fmt = isStart ? '{y}-{m}-{d} 00:00:00' : '{y}-{m}-{d} 23:59:59';
      return formatTime(date, fmt);
    },
    // 打开日期快捷选择弹窗
    openDatePicker() {
      this.$refs.quickDatePopupRef.open();
    },
    // 选择快捷日期类型（当月/上个月/近一年）
    selectQuickDate(type) {
      this.quickDateType = type;
      const now = new Date();
      let beginTime, endTime, beginText, endText;

      switch(type) {
        case 'month': // 当月
          const monthFirst = new Date(now.getFullYear(), now.getMonth(), 1);
          const monthLast = new Date(now.getFullYear(), now.getMonth() + 1, 0);
          beginTime = this.formatDateTime(monthFirst, true);
          endTime = this.formatDateTime(monthLast, false);
          beginText = formatTime(monthFirst, '{y}-{m}-{d}');
          endText = formatTime(monthLast, '{y}-{m}-{d}');
          break;
        case 'lastMonth': // 上个月
          const lastMonthFirst = new Date(now.getFullYear(), now.getMonth() - 1, 1);
          const lastMonthLast = new Date(now.getFullYear(), now.getMonth(), 0);
          beginTime = this.formatDateTime(lastMonthFirst, true);
          endTime = this.formatDateTime(lastMonthLast, false);
          beginText = formatTime(lastMonthFirst, '{y}-{m}-{d}');
          endText = formatTime(lastMonthLast, '{y}-{m}-{d}');
          break;
        case 'year': // 近一年
          const lastYear = new Date(now.getTime() - 365 * 24 * 60 * 60 * 1000);
          beginTime = this.formatDateTime(lastYear, true);
          endTime = this.formatDateTime(now, false);
          beginText = formatTime(lastYear, '{y}-{m}-{d}');
          endText = formatTime(now, '{y}-{m}-{d}');
          break;
      }
      // 临时存储选中的日期（确认后赋值到查询参数）
      this.tempDate = { beginTime, endTime, beginText, endText };
    },
    // 确认快捷日期选择
    confirmQuickDate() {
      if (this.tempDate) {
        // 赋值到查询参数
        this.queryParams.params.beginTime = this.tempDate.beginTime;
        this.queryParams.params.endTime = this.tempDate.endTime;
        // 赋值到显示文本
        this.dateRangeText = `${this.tempDate.beginText} 至 ${this.tempDate.endText}`;
        // 关闭弹窗
        this.closeQuickDatePopup();
      }
    },
    // 关闭快捷日期弹窗
    closeQuickDatePopup() {
      this.$refs.quickDatePopupRef.close();
    },
    // 获取能耗列表数据
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
        uni.showToast({
          title: "获取能耗数据错误",
          icon: 'none'
        });
      } finally {
        this.loading = false;
        if (type === 'refresh') {
          uni.stopPullDownRefresh();
        }
      }
    },
    // 搜索按钮逻辑
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.energyConsumeList = [];
      this.hasMore = true;
      this.loadMoreStatus = 'more';
      this.summaryInfo = { totalDistanceKm: 0, totalElectricityConsumed: 0, electricityPerKm: 0 };
      this.getEnergyConsumeList('refresh');
    },
    // 重置按钮逻辑
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        params: { beginTime: undefined, endTime: undefined }
      };
      this.summaryInfo = { totalDistanceKm: 0, totalElectricityConsumed: 0, electricityPerKm: 0 };
      // 恢复默认当月日期
      this.initDefaultDateRange();
      this.handleQuery();
    },
    // 下拉刷新
    onPullDownRefresh() {
      this.queryParams.pageNum = 1;
      this.energyConsumeList = [];
      this.hasMore = true;
      this.summaryInfo = { totalDistanceKm: 0, totalElectricityConsumed: 0, electricityPerKm: 0 };
      this.getEnergyConsumeList('refresh');
    },
    // 上拉加载更多
    onReachBottom() {
      if (this.hasMore && !this.loading) {
        this.queryParams.pageNum++;
        this.getEnergyConsumeList('more');
      }
    },
    // 页面滚动事件（空实现，保留原代码）
    onPageScroll() {}
  }
}
</script>

<style lang="scss">
.energy-consume-container {
  padding: 20rpx;
  background-color: #f5f5f5;
  min-height: 100vh;
  overflow-y: auto;

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

        .label {
          width: 140rpx;
          font-size: 28rpx;
          color: #606266;
          font-weight: 500;
        }

        .date-range-picker {
          flex: 1;
          height: 72rpx;
          background-color: #f8f9fa;
          border-radius: 8rpx;
          padding: 0 24rpx;
          font-size: 28rpx;
          display: flex;
          align-items: center;
          justify-content: space-between;
          color: #2c3e50;

          &.placeholder {
            .picker-text {
              color: #909399;
            }
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
          &:active { background-color: #3a8ee6; }
        }

        &.reset-btn {
          background-color: #f5f7fa;
          color: #606266;
          border: 2rpx solid #dcdfe6;
          &:active { background-color: #e9e9eb; }
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

  .energy-consume-list {
    .energy-consume-item {
      position: relative;
      background-color: #fff;
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

      &:active { opacity: 0.9; }
    }

    // 合计行样式区分
    .summary-item {
      background-color: #f9fafc;
      border: 2rpx solid #e8f4ff;
      margin-bottom: 30rpx;

      .label {
        font-weight: 600;
        color: #409eff;
      }

      .value {
        font-weight: 600;
        color: #333;
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

  // 快捷日期选择弹窗样式
  .quick-date-popup {
    background-color: #fff;
    border-radius: 20rpx 20rpx 0 0;
    padding: 30rpx;

    .popup-title {
      font-size: 32rpx;
      font-weight: 600;
      color: #333;
      text-align: center;
      margin-bottom: 30rpx;
    }

    .quick-date-list {
      display: flex;
      flex-wrap: wrap;
      gap: 20rpx;
      margin-bottom: 40rpx;

      .quick-date-item {
        flex: 1;
        min-width: 160rpx;
        height: 80rpx;
        line-height: 80rpx;
        text-align: center;
        background-color: #f5f7fa;
        border-radius: 8rpx;
        font-size: 28rpx;
        color: #606266;
        transition: all 0.2s;

        &.active {
          background-color: #e8f4ff;
          color: #409eff;
          font-weight: 600;
        }

        &:active {
          opacity: 0.9;
        }
      }
    }

    .popup-btn-group {
      display: flex;
      gap: 20rpx;

      .popup-btn {
        flex: 1;
        height: 80rpx;
        border-radius: 8rpx;
        font-size: 28rpx;

        &.cancel-btn {
          background-color: #f5f7fa;
          color: #606266;
          border: 2rpx solid #dcdfe6;
        }

        &.confirm-btn {
          background-color: #409eff;
          color: #fff;
        }
      }
    }
  }
}
</style>
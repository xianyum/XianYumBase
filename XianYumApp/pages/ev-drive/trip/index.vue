<template>
  <view class="trip-list-page">
    <view class="filter-bar">
      <view class="filter-left" @tap="openDatePopup">
        <text class="filter-text">{{ dateRangeText || '全部时间' }}</text>
        <uni-icons type="arrowdown" size="14" color="#666"></uni-icons>
      </view>
      <view class="filter-right" @tap="handleFilter">
        <uni-icons type="list" size="20" color="#666"></uni-icons>
      </view>
    </view>

    <view class="trip-content" v-if="groupedTrips.length > 0">
      <view v-for="(group, gIndex) in groupedTrips" :key="gIndex" class="trip-group">
        <view class="group-date">
          <text>{{ group.date }}</text>
          <text class="group-weekday">{{ group.weekday }}</text>
        </view>
        <view
          v-for="(item, index) in group.trips"
          :key="item.id"
          class="trip-card"
          @tap="goToDetail(item)"
        >
          <view class="card-main">
            <view class="distance-row">
              <view class="distance-main">
                <text class="distance-value">{{ item.distanceKm }}</text>
                <text class="distance-unit">km</text>
              </view>
              <view class="consume-info">
                <text class="consume-value">{{ item.consumeKwh }}</text>
                <text class="consume-unit">kWh</text>
              </view>
            </view>
            <view class="info-row">
              <view class="info-item">
                <uni-icons type="calendar" size="12" color="#999"></uni-icons>
                <text class="info-text">{{ item.timeRange }}</text>
              </view>
              <view class="info-item">
                <uni-icons type="loop" size="12" color="#999"></uni-icons>
                <text class="info-text">{{ item.durationText }}</text>
              </view>
              <view class="info-item">
                <uni-icons type="heart" size="12" color="#999"></uni-icons>
                <text class="info-text">{{ item.avgSpeed }} km/h</text>
              </view>
            </view>
            <view class="device-row" v-if="item.deviceInfo">
              <uni-icons type="location" size="12" color="#bbb"></uni-icons>
              <text class="device-text">{{ item.deviceInfo }}</text>
            </view>
          </view>
          <view class="card-arrow">
            <uni-icons type="right" size="16" color="#ccc"></uni-icons>
          </view>
        </view>
      </view>

      <view class="load-more">
        <uni-load-more :status="loadMoreStatus" :content-text="contentText" />
      </view>
    </view>

    <view v-else class="empty-box">
      <uni-icons type="info" size="60" color="#ccc"></uni-icons>
      <text class="empty-text">暂无轨迹数据</text>
    </view>

    <uni-popup ref="datePopupRef" type="bottom" :mask-click="true">
      <view class="date-popup">
        <view class="popup-title">时间范围</view>
        <view class="date-options">
          <view
            class="date-option"
            :class="{ active: dateFilterType === 'all' }"
            @tap="selectDateFilter('all')"
          >全部时间</view>
          <view
            class="date-option"
            :class="{ active: dateFilterType === 'week' }"
            @tap="selectDateFilter('week')"
          >近一周</view>
          <view
            class="date-option"
            :class="{ active: dateFilterType === 'month' }"
            @tap="selectDateFilter('month')"
          >近一月</view>
          <view
            class="date-option"
            :class="{ active: dateFilterType === 'year' }"
            @tap="selectDateFilter('year')"
          >近一年</view>
        </view>
        <view class="popup-btn" @tap="confirmDateFilter">确定</view>
      </view>
      <view class="mask-close" @tap="closeDatePopup"></view>
    </uni-popup>
  </view>
</template>

<script>
import { getTripPage } from "@/api/ev-drive/list";

export default {
  name: 'TripList',
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
      dateFilterType: 'all',
      dateRangeText: '全部时间',
      tripList: [],
      loading: false,
      hasMore: true,
      total: 0,
      loadMoreStatus: 'more',
      contentText: {
        contentdown: '上拉显示更多',
        contentrefresh: '正在加载...',
        contentnomore: '没有更多数据了'
      }
    };
  },
  computed: {
    groupedTrips() {
      const groups = [];
      const dateMap = {};
      this.tripList.forEach(item => {
        const date = this.formatDate(item.tripStartTime);
        if (!dateMap[date]) {
          dateMap[date] = [];
        }
        const processed = this.processTripItem(item);
        dateMap[date].push(processed);
      });
      Object.keys(dateMap).forEach(date => {
        const weekday = this.getWeekday(dateMap[date][0].startTime || date);
        groups.push({ date, weekday, trips: dateMap[date] });
      });
      return groups;
    }
  },
  onLoad(options) {
    if (options.pageTitle) {
      uni.setNavigationBarTitle({ title: options.pageTitle });
    } else {
      uni.setNavigationBarTitle({ title: '全部轨迹' });
    }
    this.getTripList();
  },
  onPullDownRefresh() {
    this.refreshData();
  },
  methods: {
    processTripItem(item) {
      const startOdo = item.startOdometer || 0;
      const endOdo = item.endOdometer || 0;
      const distance = (endOdo - startOdo).toFixed(1);

      const start = this.formatTime(item.tripStartTime);
      const end = this.formatTime(item.tripEndTime);
      const timeRange = `${start}-${end}`;

      const durationMs = this.getDurationMs(item.tripStartTime, item.tripEndTime);
      const durationText = this.formatDuration(durationMs);

      const avgSpeed = durationMs > 0 ? Math.round((parseFloat(distance) / (durationMs / 3600000))) : 0;

      const consumeKwh = (item.tripConsumeKwh != null ? item.tripConsumeKwh : item.consumeKwh) || 0;

      return {
        ...item,
        distanceKm: distance,
        consumeKwh: consumeKwh,
        timeRange,
        durationText,
        avgSpeed,
        deviceInfo: this.stripProvince(item.endAddress)
      };
    },
    formatDate(dateStr) {
      if (!dateStr) return '';
      const d = new Date(dateStr);
      const y = d.getFullYear();
      const m = String(d.getMonth() + 1).padStart(2, '0');
      const day = String(d.getDate()).padStart(2, '0');
      return `${y}.${m}.${day}`;
    },
    getWeekday(dateStr) {
      if (!dateStr) return '';
      const d = new Date(dateStr);
      const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
      return weekdays[d.getDay()];
    },
    formatTime(dateStr) {
      if (!dateStr) return '';
      const d = new Date(dateStr);
      const h = String(d.getHours()).padStart(2, '0');
      const mi = String(d.getMinutes()).padStart(2, '0');
      return `${h}:${mi}`;
    },
    getDurationMs(startStr, endStr) {
      if (!startStr || !endStr) return 0;
      const start = new Date(startStr.replace('T', ' ')).getTime();
      const end = new Date(endStr.replace('T', ' ')).getTime();
      return end - start;
    },
    formatDuration(ms) {
      if (ms <= 0) return '00:00:00';
      const hours = Math.floor(ms / 3600000);
      const minutes = Math.floor((ms % 3600000) / 60000);
      const seconds = Math.floor((ms % 60000) / 1000);
      return `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
    },
    stripProvince(address) {
      if (!address) return '';
      return address.replace(/^[\u4e00-\u9fa5]+?省/, '');
    },
    async getTripList(type = 'more') {
      if (this.loading) return;
      this.loading = true;
      try {
        const response = await getTripPage(this.queryParams);
        if (response.code === 200) {
          const list = response.data || [];
          const total = response.total || 0;
          if (type === 'refresh' || this.queryParams.pageNum === 1) {
            this.tripList = list;
          } else {
            this.tripList = [...this.tripList, ...list];
          }
          this.total = total;
          this.hasMore = this.tripList.length < total;
          this.loadMoreStatus = this.hasMore ? 'more' : 'noMore';
        }
      } catch (error) {
        this.loadMoreStatus = 'more';
        this.$modal.msg('获取轨迹数据失败');
      } finally {
        this.loading = false;
        if (type === 'refresh') {
          uni.stopPullDownRefresh();
        }
      }
    },
    refreshData() {
      this.queryParams.pageNum = 1;
      this.tripList = [];
      this.hasMore = true;
      this.loadMoreStatus = 'more';
      this.getTripList('refresh');
    },
    onReachBottom() {
      if (this.hasMore && !this.loading) {
        this.queryParams.pageNum++;
        this.getTripList('more');
      }
    },
    openDatePopup() {
      this.$refs.datePopupRef.open();
    },
    closeDatePopup() {
      this.$refs.datePopupRef.close();
    },
    selectDateFilter(type) {
      this.dateFilterType = type;
    },
    confirmDateFilter() {
      const now = new Date();
      let beginTime, endTime, text;
      switch (this.dateFilterType) {
        case 'all':
          beginTime = undefined;
          endTime = undefined;
          text = '全部时间';
          break;
        case 'week':
          beginTime = this.formatDateTime(new Date(now.getTime() - 7 * 24 * 3600 * 1000), true);
          endTime = this.formatDateTime(now, false);
          text = '近一周';
          break;
        case 'month':
          beginTime = this.formatDateTime(new Date(now.getTime() - 30 * 24 * 3600 * 1000), true);
          endTime = this.formatDateTime(now, false);
          text = '近一月';
          break;
        case 'year':
          beginTime = this.formatDateTime(new Date(now.getTime() - 365 * 24 * 3600 * 1000), true);
          endTime = this.formatDateTime(now, false);
          text = '近一年';
          break;
      }
      this.queryParams.params.beginTime = beginTime;
      this.queryParams.params.endTime = endTime;
      this.dateRangeText = text;
      this.closeDatePopup();
      this.refreshData();
    },
    formatDateTime(date, isStart) {
      const y = date.getFullYear();
      const m = String(date.getMonth() + 1).padStart(2, '0');
      const d = String(date.getDate()).padStart(2, '0');
      if (isStart) return `${y}-${m}-${d} 00:00:00`;
      return `${y}-${m}-${d} 23:59:59`;
    },
    handleFilter() {
      this.openDatePopup();
    },
    goToDetail(item) {
      uni.navigateTo({
        url: `/pages/ev-drive/trip/detail?id=${item.id}`
      });
    }
  }
};
</script>

<style lang="scss">
.trip-list-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 120rpx;
}

.filter-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 32rpx;
  background-color: #fff;
  position: sticky;
  top: 0;
  z-index: 10;

  .filter-left {
    display: flex;
    align-items: center;
    gap: 8rpx;

    .filter-text {
      font-size: 30rpx;
      color: #333;
      font-weight: 500;
    }
  }

  .filter-right {
    padding: 12rpx;
  }
}

.trip-content {
  padding: 0 24rpx;
}

.trip-group {
  margin-top: 24rpx;

  .group-date {
    font-size: 26rpx;
    color: #999;
    padding: 16rpx 8rpx;
    font-weight: 500;
    display: flex;
    align-items: center;
    gap: 12rpx;

    .group-weekday {
      font-size: 24rpx;
      color: #b2b2b2;
      font-weight: 400;
    }
  }
}

.trip-card {
  display: flex;
  align-items: stretch;
  background-color: #fff;
  border-radius: 20rpx;
  padding: 32rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
  transition: all 0.2s ease;

  &:active {
    transform: scale(0.98);
    opacity: 0.9;
  }

  .card-main {
    flex: 1;
    min-width: 0;
    overflow: hidden;
  }

  .distance-row {
    display: flex;
    align-items: baseline;
    justify-content: space-between;
    margin-bottom: 20rpx;

    .distance-main {
      display: flex;
      align-items: baseline;
    }

    .distance-value {
      font-size: 56rpx;
      font-weight: 700;
      color: #222;
      line-height: 1.1;
    }

    .distance-unit {
      font-size: 26rpx;
      color: #666;
      margin-left: 8rpx;
      font-weight: 400;
    }

    .consume-info {
      display: flex;
      align-items: baseline;

      .consume-value {
        font-size: 30rpx;
        font-weight: 600;
        color: #fa8c16;
        line-height: 1.1;
      }

      .consume-unit {
        font-size: 12rpx;
        color: #fa8c16;
        margin-left: 4rpx;
        font-weight: 400;
      }
    }
  }

  .info-row {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 16rpx 24rpx;
    margin-bottom: 16rpx;

    .info-item {
      display: flex;
      align-items: center;
      gap: 6rpx;
      flex-shrink: 0;

      .info-text {
        font-size: 24rpx;
        color: #666;
      }
    }
  }

  .device-row {
    display: flex;
    align-items: center;
    gap: 6rpx;
    min-width: 0;

    .device-text {
      font-size: 24rpx;
      color: #aaa;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      min-width: 0;
    }
  }

  .card-arrow {
    flex-shrink: 0;
    display: flex;
    align-items: center;
    margin-left: 16rpx;
  }
}

.load-more {
  padding: 40rpx 0;
}

.empty-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 200rpx 0;

  .empty-text {
    margin-top: 24rpx;
    font-size: 28rpx;
    color: #999;
  }
}

.date-popup {
  background-color: #fff;
  border-radius: 24rpx 24rpx 0 0;
  padding: 40rpx 32rpx;
  padding-bottom: calc(40rpx + env(safe-area-inset-bottom));

  .popup-title {
    font-size: 34rpx;
    font-weight: 600;
    color: #333;
    text-align: center;
    margin-bottom: 36rpx;
  }

  .date-options {
    display: flex;
    flex-direction: column;
    gap: 20rpx;
    margin-bottom: 40rpx;

    .date-option {
      height: 88rpx;
      line-height: 88rpx;
      text-align: center;
      background-color: #f7f8fa;
      border-radius: 12rpx;
      font-size: 30rpx;
      color: #333;
      transition: all 0.2s ease;

      &.active {
        background-color: #e8f4ff;
        color: #409eff;
        font-weight: 600;
      }

      &:active {
        opacity: 0.8;
      }
    }
  }

  .popup-btn {
    height: 88rpx;
    line-height: 88rpx;
    text-align: center;
    background-color: #409eff;
    color: #fff;
    border-radius: 12rpx;
    font-size: 32rpx;
    font-weight: 500;

    &:active {
      background-color: #3a8ee6;
    }
  }
}

.mask-close {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: -1;
}
</style>
<template>
  <view class="trip-list-page">
    <view class="top-bar">
      <view class="search-input-wrap" @tap="showFilter = true">
        <uni-icons type="calendar" size="16" color="#909399"></uni-icons>
        <text class="filter-display">{{ dateRangeText }}</text>
      </view>
      <view class="filter-btn" :class="{ active: hasFilter }" @tap="showFilter = true">
        <uni-icons type="list" size="18" color="#606266"></uni-icons>
      </view>
      <view class="filter-total" v-if="total > 0">
        <text class="total-num">{{ total }}</text>
        <text class="total-label">条</text>
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
            <view class="filter-label">时间范围</view>
            <view class="chip-list">
              <view
                  v-for="opt in dateOptions"
                  :key="opt.value"
                  class="chip"
                  :class="{ active: pendingDateFilterType === opt.value }"
                  @tap="pendingDateFilterType = opt.value"
              >{{ opt.label }}</view>
            </view>
          </view>
        </scroll-view>
        <view class="drawer-footer">
          <view class="drawer-btn reset" @tap="resetQuery">重置</view>
          <view class="drawer-btn confirm" @tap="handleFilterConfirm">确定</view>
        </view>
      </view>
    </view>

    <view class="trip-content" v-if="state === 'loading'">
      <view class="skeleton-card" v-for="i in 4" :key="i">
        <view class="skeleton-line line-lg"></view>
        <view class="skeleton-line line-sm"></view>
        <view class="skeleton-line line-xs"></view>
      </view>
    </view>

    <view class="trip-content" v-else-if="groupedTrips.length > 0">
      <view v-for="group in groupedTrips" :key="group.date" class="trip-group">
        <view class="group-date">
          <view class="date-dot"></view>
          <text class="date-text">{{ group.date }}</text>
          <text class="group-weekday">{{ group.weekday }}</text>
        </view>
        <view
          v-for="item in group.trips"
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
                <view class="consume-badge">
                  <uni-icons type="bolt" size="12" color="#fa8c16"></uni-icons>
                  <text class="consume-value">{{ item.consumeKwh }}</text>
                  <text class="consume-unit">kWh</text>
                </view>
              </view>
            </view>
            <view class="info-row">
              <view class="info-item">
                <uni-icons type="clock" size="12" color="#999"></uni-icons>
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
        <uni-load-more :status="loadMoreStatus" :content-text="loadMoreText" />
      </view>
    </view>

    <view v-else class="empty-box">
      <image class="empty-image" src="https://a0ai.marscode.cn/api/ide/v1/text_to_image?prompt=empty%20trip%20list%20illustration%2C%20a%20car%20with%20dashed%20route%20line%20on%20minimalist%20background%2C%20flat%20design%2C%20blue%20and%20gray%20tones&image_size=square_hd"></image>
      <text class="empty-title">暂无轨迹数据</text>
      <text class="empty-desc">快去驾驶爱车，开启你的第一次旅程吧</text>
    </view>
  </view>
</template>

<script>
import { getTripPage } from "@/api/ev-drive/list";

const DEFAULT_DATE_FILTER_TYPE = 'all';

const DATE_FILTER_OPTIONS = [
  { value: 'all', label: '全部' },
  { value: 'yesterday', label: '昨天' },
  { value: 'lastWeek', label: '上一周' },
  { value: 'lastMonth', label: '上一月' }
];

const WEEKDAYS = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];

export default {
  name: 'TripList',
  data() {
    return {
      state: 'loading',
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        params: {
          beginTime: undefined,
          endTime: undefined
        }
      },
      dateFilterType: DEFAULT_DATE_FILTER_TYPE,
      pendingDateFilterType: DEFAULT_DATE_FILTER_TYPE,
      dateRangeText: '全部',
      tripList: [],
      loading: false,
      hasMore: true,
      total: 0,
      loadMoreStatus: 'more',
      loadMoreText: {
        contentdown: '上拉加载更多',
        contentrefresh: '加载中...',
        contentnomore: '— 没有更多了 —'
      },
      dateOptions: DATE_FILTER_OPTIONS,
      showFilter: false
    };
  },
  computed: {
    groupedTrips() {
      const map = new Map();
      for (const item of this.tripList) {
        const date = this._formatDate(item.tripStartTime);
        if (!map.has(date)) {
          map.set(date, []);
        }
        map.get(date).push(this._processItem(item));
      }
      const groups = [];
      map.forEach((trips, date) => {
        groups.push({
          date,
          weekday: this._getWeekday(trips[0].startTime || date),
          trips
        });
      });
      return groups;
    },
    hasFilter() {
      return this.dateFilterType !== DEFAULT_DATE_FILTER_TYPE;
    }
  },
  watch: {
    showFilter(val) {
      if (val) {
        this.pendingDateFilterType = this.dateFilterType;
      }
    }
  },
  onLoad(options) {
    const title = (options && options.pageTitle) || '全部轨迹';
    uni.setNavigationBarTitle({ title });
    this._resolveAndApplyDateRange(DEFAULT_DATE_FILTER_TYPE);
    this._getTripList('init');
  },
  onPullDownRefresh() {
    this._refresh();
  },
  onReachBottom() {
    if (this.hasMore && !this.loading) {
      this.queryParams.pageNum++;
      this._getTripList('more');
    }
  },
  methods: {
    handleFilterConfirm() {
      this.showFilter = false;
      if (this.pendingDateFilterType !== this.dateFilterType) {
        this._resolveAndApplyDateRange(this.pendingDateFilterType);
        this._refresh();
      }
    },
    resetQuery() {
      this.pendingDateFilterType = DEFAULT_DATE_FILTER_TYPE;
      this._resolveAndApplyDateRange(DEFAULT_DATE_FILTER_TYPE);
      this.showFilter = false;
      this._refresh();
    },
    _resolveAndApplyDateRange(type) {
      this.dateFilterType = type;
      const { begin, end, text } = this._resolveDateRange(type);
      this.queryParams.params.beginTime = begin;
      this.queryParams.params.endTime = end;
      this.dateRangeText = text;
    },
    _resolveDateRange(type) {
      const now = new Date();
      const _setDayStart = (d) => { d.setHours(0, 0, 0, 0); return d; };
      const _setDayEnd = (d) => { d.setHours(23, 59, 59, 999); return d; };

      switch (type) {
        case 'yesterday': {
          const y = new Date(now);
          y.setDate(y.getDate() - 1);
          return {
            begin: this._formatDateTime(_setDayStart(new Date(y)), true),
            end: this._formatDateTime(_setDayEnd(new Date(y)), false),
            text: '昨天'
          };
        }
        case 'lastWeek': {
          const dow = now.getDay();
          const offsetToMonday = dow === 0 ? -6 : 1 - dow;
          const lastMonday = new Date(now);
          lastMonday.setDate(lastMonday.getDate() + offsetToMonday - 7);
          const lastSunday = new Date(lastMonday);
          lastSunday.setDate(lastSunday.getDate() + 6);
          return {
            begin: this._formatDateTime(_setDayStart(new Date(lastMonday)), true),
            end: this._formatDateTime(_setDayEnd(new Date(lastSunday)), false),
            text: '上一周'
          };
        }
        case 'lastMonth': {
          const lmStart = new Date(now.getFullYear(), now.getMonth() - 1, 1);
          const lmEnd = new Date(now.getFullYear(), now.getMonth(), 0);
          return {
            begin: this._formatDateTime(_setDayStart(lmStart), true),
            end: this._formatDateTime(_setDayEnd(lmEnd), false),
            text: '上一月'
          };
        }
        default:
          return { begin: undefined, end: undefined, text: '全部' };
      }
    },
    _processItem(item) {
      const startOdo = item.startOdometer || 0;
      const endOdo = item.endOdometer || 0;
      const distance = (endOdo - startOdo).toFixed(1);
      const durationMs = this._durationMs(item.tripStartTime, item.tripEndTime);
      const avgSpeed = durationMs > 0
        ? Math.round(parseFloat(distance) / (durationMs / 3600000))
        : 0;

      return {
        ...item,
        distanceKm: distance,
        consumeKwh: (item.tripConsumeKwh != null ? item.tripConsumeKwh : item.consumeKwh) || 0,
        timeRange: `${this._formatTime(item.tripStartTime)}-${this._formatTime(item.tripEndTime)}`,
        durationText: this._formatDuration(durationMs),
        avgSpeed,
        deviceInfo: this._stripProvince(item.endAddress)
      };
    },
    _formatDate(dateStr) {
      if (!dateStr) return '';
      const d = new Date(dateStr);
      return `${d.getFullYear()}.${this._pad(d.getMonth() + 1)}.${this._pad(d.getDate())}`;
    },
    _formatTime(dateStr) {
      if (!dateStr) return '';
      const d = new Date(dateStr);
      return `${this._pad(d.getHours())}:${this._pad(d.getMinutes())}`;
    },
    _formatDuration(ms) {
      if (ms <= 0) return '00:00:00';
      const h = Math.floor(ms / 3600000);
      const m = Math.floor((ms % 3600000) / 60000);
      const s = Math.floor((ms % 60000) / 1000);
      return `${this._pad(h)}:${this._pad(m)}:${this._pad(s)}`;
    },
    _durationMs(start, end) {
      if (!start || !end) return 0;
      return new Date(end.replace('T', ' ')).getTime() - new Date(start.replace('T', ' ')).getTime();
    },
    _getWeekday(dateStr) {
      if (!dateStr) return '';
      return WEEKDAYS[new Date(dateStr).getDay()];
    },
    _pad(n) {
      return String(n).padStart(2, '0');
    },
    _stripProvince(address) {
      return address ? address.replace(/^[\u4e00-\u9fa5]+?省/, '') : '';
    },
    _formatDateTime(date, isStart) {
      const y = date.getFullYear();
      const m = this._pad(date.getMonth() + 1);
      const d = this._pad(date.getDate());
      return isStart ? `${y}-${m}-${d} 00:00:00` : `${y}-${m}-${d} 23:59:59`;
    },

    async _getTripList(type) {
      if (this.loading) return;
      this.loading = true;
      if (type === 'init') this.state = 'loading';
      try {
        const response = await getTripPage(this.queryParams);
        if (response.code === 200) {
          const list = response.data || [];
          this.tripList = (type === 'more')
            ? [...this.tripList, ...list]
            : list;
          this.total = response.total || 0;
          this.hasMore = this.tripList.length < this.total;
          this.loadMoreStatus = this.hasMore ? 'more' : 'noMore';
        } else {
          this.loadMoreStatus = 'more';
        }
      } catch (error) {
        this.loadMoreStatus = 'more';
        this.$modal.msg('获取轨迹数据失败');
      } finally {
        this.loading = false;
        this.state = 'loaded';
        if (type === 'refresh') uni.stopPullDownRefresh();
      }
    },
    _refresh() {
      this.queryParams.pageNum = 1;
      this.tripList = [];
      this.hasMore = true;
      this.loadMoreStatus = 'more';
      this._getTripList('refresh');
    },

    goToDetail(item) {
      uni.navigateTo({
        url: `/pages/ev-drive/trip/detail?id=${item.id}`
      });
    }
  }
};
</script>

<style lang="scss" scoped>
$primary: #409eff;
$primary-light: #ecf5ff;
$warning: #fa8c16;
$text-primary: #222;
$text-secondary: #666;
$text-tertiary: #999;
$text-placeholder: #b2b2b2;
$bg-page: #f5f6fa;
$bg-card: #ffffff;
$shadow-card: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
$color-border: #ebeef5;

.trip-list-page {
  min-height: 100vh;
  background-color: $bg-page;
  padding-bottom: 120rpx;
}

.top-bar {
  position: sticky;
  top: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 20rpx 24rpx;
  padding-top: calc(20rpx + env(safe-area-inset-top));
  background-color: $bg-card;
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
      color: $text-primary;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
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
      background-color: rgba($primary, 0.1);

      &::after {
        content: '';
        position: absolute;
        top: 12rpx;
        right: 12rpx;
        width: 14rpx;
        height: 14rpx;
        background-color: $primary;
        border-radius: 50%;
      }
    }

    &:active {
      opacity: 0.7;
    }
  }

  .filter-total {
    display: flex;
    align-items: baseline;
    gap: 6rpx;
    flex-shrink: 0;

    .total-num {
      font-size: 32rpx;
      font-weight: 700;
      color: $primary;
    }

    .total-label {
      font-size: 24rpx;
      color: $text-tertiary;
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
    background-color: $bg-card;
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
        color: $text-primary;
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
          color: $text-secondary;
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
            color: $text-secondary;
            transition: all 0.2s;

            &.active {
              background-color: rgba($primary, 0.1);
              color: $primary;
              border: 1rpx solid $primary;
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
          color: $text-secondary;

          &:active {
            background-color: #e9e9eb;
          }
        }

        &.confirm {
          background-color: $primary;
          color: #fff;

          &:active {
            background-color: darken($primary, 5%);
          }
        }
      }
    }
  }
}

.trip-content {
  padding: 8rpx 24rpx 0;
}

.trip-group {
  margin-top: 28rpx;

  .group-date {
    font-size: 26rpx;
    color: $text-tertiary;
    padding: 16rpx 8rpx;
    font-weight: 500;
    display: flex;
    align-items: center;
    gap: 12rpx;

    .date-dot {
      width: 12rpx;
      height: 12rpx;
      border-radius: 50%;
      background: linear-gradient(135deg, $primary, #5a77d7);
    }

    .date-text {
      font-size: 28rpx;
      color: $text-primary;
      font-weight: 600;
    }

    .group-weekday {
      font-size: 24rpx;
      color: $text-placeholder;
      font-weight: 400;
    }
  }
}

.trip-card {
  display: flex;
  align-items: stretch;
  background-color: $bg-card;
  border-radius: 20rpx;
  padding: 32rpx;
  margin-bottom: 20rpx;
  box-shadow: $shadow-card;
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
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20rpx;

    .distance-main {
      display: flex;
      align-items: baseline;
    }

    .distance-value {
      font-size: 60rpx;
      font-weight: 700;
      color: $text-primary;
      line-height: 1;
      letter-spacing: -1rpx;
    }

    .distance-unit {
      font-size: 26rpx;
      color: $text-secondary;
      margin-left: 8rpx;
      font-weight: 400;
    }

    .consume-badge {
      display: flex;
      align-items: baseline;
      gap: 4rpx;
      padding: 8rpx 16rpx;
      background: linear-gradient(135deg, rgba(250, 140, 22, 0.1), rgba(250, 140, 22, 0.04));
      border-radius: 24rpx;

      .consume-value {
        font-size: 28rpx;
        font-weight: 600;
        color: $warning;
      }

      .consume-unit {
        font-size: 20rpx;
        color: $warning;
        opacity: 0.8;
      }
    }
  }

  .info-row {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 14rpx 22rpx;
    margin-bottom: 14rpx;

    .info-item {
      display: flex;
      align-items: center;
      gap: 6rpx;
      flex-shrink: 0;

      .info-text {
        font-size: 24rpx;
        color: $text-secondary;
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
      color: $text-tertiary;
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
    margin-left: 8rpx;
  }
}

.skeleton-card {
  background-color: $bg-card;
  border-radius: 20rpx;
  padding: 32rpx;
  margin-bottom: 20rpx;
  box-shadow: $shadow-card;

  .skeleton-line {
    background: linear-gradient(90deg, #f0f0f0 25%, #e8e8e8 50%, #f0f0f0 75%);
    background-size: 200% 100%;
    animation: skeletonShimmer 1.4s ease infinite;
    border-radius: 8rpx;
    margin-bottom: 16rpx;
  }

  .line-lg {
    height: 48rpx;
    width: 60%;
  }

  .line-sm {
    height: 24rpx;
    width: 80%;
  }

  .line-xs {
    height: 20rpx;
    width: 40%;
  }
}

@keyframes skeletonShimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

.load-more {
  padding: 40rpx 0;
}

.empty-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 160rpx 60rpx;

  .empty-image {
    width: 280rpx;
    height: 280rpx;
    margin-bottom: 32rpx;
  }

  .empty-title {
    font-size: 32rpx;
    color: $text-primary;
    font-weight: 600;
    margin-bottom: 12rpx;
  }

  .empty-desc {
    font-size: 26rpx;
    color: $text-tertiary;
  }
}
</style>
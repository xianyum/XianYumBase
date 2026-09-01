<template>
  <view class="location-page">
    <view class="map-container">
      <map
        v-if="mapReady"
        id="locationMap"
        class="location-map"
        :latitude="mapCenter.latitude"
        :longitude="mapCenter.longitude"
        :scale="mapScale"
        :markers="markers"
        :show-location="false"
      ></map>
      <view class="map-loading" v-if="!mapReady">
        <uni-icons type="spinner-cycle" size="24" color="#409eff"></uni-icons>
        <text class="loading-text">地图加载中...</text>
      </view>
      <view class="map-no-data" v-if="mapReady && !hasLocation">
        <view class="no-data-mask"></view>
        <view class="no-data-content">
          <uni-icons type="info" size="40" color="#fff"></uni-icons>
          <text class="no-data-text">暂无位置数据</text>
        </view>
      </view>
      <view class="map-controls">
        <view class="control-btn" @tap="refreshData">
          <uni-icons type="reload" size="18" color="#fff"></uni-icons>
        </view>
      </view>
      <view class="status-badge" :class="statusClass">
        <text class="status-text">{{ statusText }}</text>
      </view>
    </view>

    <view class="info-section">
      <view class="info-card soc-card" :class="{ 'refreshing': isRefreshing }" @click="onRefresh">
        <view class="soc-main">
          <view class="soc-ring">
            <view class="soc-inner">
              <text class="soc-value">{{ valOrDash(location.soc) }}</text>
              <text class="soc-unit">%</text>
            </view>
          </view>
          <view class="soc-info">
            <text class="soc-label">当前电量</text>
          </view>
        </view>
      </view>

      <view class="info-card">
        <view class="card-title">车辆状态</view>
        <view class="status-grid">
          <view class="status-item">
            <view class="status-icon temp">
              <uni-icons type="fire" size="20" color="#fff"></uni-icons>
            </view>
            <text class="status-value">{{ valOrDash(location.battTemp) }}°C</text>
            <text class="status-label">电池温度</text>
          </view>
          <view class="status-item">
            <view class="status-icon power">
              <uni-icons type="heart" size="20" color="#fff"></uni-icons>
            </view>
            <text class="status-value">{{ valOrDash(location.power) }}</text>
            <text class="status-label">瞬时功率(kW)</text>
          </view>
          <view class="status-item">
            <view class="status-icon vehicle">
              <uni-icons type="navigate" size="20" color="#fff"></uni-icons>
            </view>
            <text class="status-value">{{ vehicleStatus }}</text>
            <text class="status-label">车辆状态</text>
          </view>
          <view class="status-item">
            <view class="status-icon odometer">
              <uni-icons type="loop" size="20" color="#fff"></uni-icons>
            </view>
            <text class="status-value">{{ formatOdometer(location.odometer) }}</text>
            <text class="status-label">总里程(km)</text>
          </view>
        </view>
      </view>

      <view class="info-card">
        <view class="card-title">位置信息</view>
        <view class="info-row">
          <uni-icons type="clock" size="14" color="#409eff"></uni-icons>
          <text class="row-label">{{ formatDateTime(location.reportTime) }}</text>
        </view>
        <view class="info-row" v-if="location.formattedAddress">
          <uni-icons type="location-filled" size="14" color="#67c23a"></uni-icons>
          <text class="row-label address-text">{{ location.formattedAddress }}</text>
        </view>
        <view class="info-row" v-else>
          <uni-icons type="location-filled" size="14" color="#c0c4cc"></uni-icons>
          <text class="row-label">暂无位置信息</text>
        </view>
      </view>

      <view class="info-card">
        <view class="card-title">轮胎气压(kPa)</view>
        <view class="tire-grid">
          <view class="tire-item">
            <text class="tire-label">前左</text>
            <text class="tire-value" :class="getTireClass(location.tirePressureFl)">{{ valOrDash(location.tirePressureFl) }}</text>
          </view>
          <view class="tire-item">
            <text class="tire-label">前右</text>
            <text class="tire-value" :class="getTireClass(location.tirePressureFr)">{{ valOrDash(location.tirePressureFr) }}</text>
          </view>
          <view class="tire-item">
            <text class="tire-label">后左</text>
            <text class="tire-value" :class="getTireClass(location.tirePressureRl)">{{ valOrDash(location.tirePressureRl) }}</text>
          </view>
          <view class="tire-item">
            <text class="tire-label">后右</text>
            <text class="tire-value" :class="getTireClass(location.tirePressureRr)">{{ valOrDash(location.tirePressureRr) }}</text>
          </view>
        </view>
      </view>

      <view class="bottom-space"></view>
    </view>
  </view>
</template>

<script>
import { getCurrentLocation } from "@/api/ev-drive/list";

export default {
  name: 'CurrentLocation',
  data() {
    return {
      location: {},
      mapReady: false,
      mapCenter: { latitude: 34.24, longitude: 108.90 },
      mapScale: 15,
      isRefreshing: false
    };
  },
  computed: {
    hasLocation() {
      return this.location.lat != null && this.location.lon != null;
    },
    markers() {
      if (!this.hasLocation) return [];
      return [{
        id: 1,
        latitude: Number(this.location.lat),
        longitude: Number(this.location.lon),
        width: 56,
        height: 56,
        iconPath: '/static/images/map/car.png',
        rotate: this.location.heading || 0,
        callout: {
          content: '车辆当前位置',
          color: '#fff',
          fontSize: 12,
          borderRadius: 8,
          bgColor: '#409eff',
          padding: 8,
          display: 'ALWAYS'
        }
      }];
    },
    statusClass() {
      if (this.location.isCharging === 1) return 'status-charging';
      if (this.location.isParked === 1) return 'status-parked';
      return 'status-driving';
    },
    statusText() {
      if (this.location.isCharging === 1) return '充电中';
      if (this.location.isParked === 1) return '已停车';
      return '行驶中';
    },
    vehicleStatus() {
      return this.location.isParked === 0 ? '行驶中' : '已驻车';
    }
  },
  onLoad() {
    if (options.pageTitle) {
      uni.setNavigationBarTitle({ title: options.pageTitle });
    } else {
      uni.setNavigationBarTitle({ title: '车辆信息' });
    }
    this.loadData();
  },
  onShow() {
    this.loadData();
  },
  methods: {
    async loadData() {
      try {
        uni.showLoading({ title: '加载中...', mask: true });
        const response = await getCurrentLocation();
        if (response.code === 200) {
          this.location = response.data || {};
          this.initMap();
        }
      } catch (error) {
        this.$modal.msg('加载位置信息失败');
      } finally {
        uni.hideLoading();
      }
    },
    onRefresh() {
      if (this.isRefreshing) return;
      this.isRefreshing = true;
      this.loadData().finally(() => {
        setTimeout(() => {
          this.isRefreshing = false;
        }, 400);
      });
    },
    initMap() {
      this.mapReady = true;
      if (this.hasLocation) {
        this.mapCenter = {
          latitude: Number(this.location.lat),
          longitude: Number(this.location.lon)
        };
        this.$nextTick(() => {
          setTimeout(() => {
            const mapCtx = uni.createMapContext('locationMap', this);
            mapCtx && mapCtx.moveToLocation({
              latitude: Number(this.location.lat),
              longitude: Number(this.location.lon)
            });
          }, 100);
        });
      }
    },
    refreshData() {
      this.loadData();
    },
    formatDateTime(dateStr) {
      if (!dateStr) return '--';
      const d = new Date(dateStr);
      const y = d.getFullYear();
      const m = String(d.getMonth() + 1).padStart(2, '0');
      const day = String(d.getDate()).padStart(2, '0');
      const h = String(d.getHours()).padStart(2, '0');
      const mi = String(d.getMinutes()).padStart(2, '0');
      const s = String(d.getSeconds()).padStart(2, '0');
      return `${y}-${m}-${day} ${h}:${mi}:${s}`;
    },
    formatOdometer(val) {
      if (val == null) return '--';
      return Number(val).toFixed(1);
    },
    getTireClass(val) {
      if (val == null) return '';
      if (val < 250) return 'tire-low';
      if (val > 320) return 'tire-high';
      return '';
    },
    valOrDash(val) {
      if (val === null || val === undefined || val === '') return '--';
      return val;
    }
  }
};
</script>

<style lang="scss">
.location-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.map-container {
  position: relative;
  width: 100%;
  height: 500rpx;

  .location-map {
    width: 100%;
    height: 100%;
  }

  .map-loading {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background-color: rgba(255, 255, 255, 0.8);
    z-index: 5;

    .loading-text {
      margin-top: 16rpx;
      font-size: 26rpx;
      color: #666;
    }
  }

  .map-no-data {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 5;

    .no-data-mask {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background-color: rgba(0, 0, 0, 0.3);
    }

    .no-data-content {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      display: flex;
      flex-direction: column;
      align-items: center;

      .no-data-text {
        margin-top: 16rpx;
        font-size: 28rpx;
        color: #fff;
      }
    }
  }

  .map-controls {
    position: absolute;
    right: 20rpx;
    bottom: 20rpx;
    display: flex;
    flex-direction: column;
    gap: 16rpx;

    .control-btn {
      width: 72rpx;
      height: 72rpx;
      background-color: #409eff;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 4rpx 12rpx rgba(64, 158, 255, 0.4);

      &:active {
        opacity: 0.8;
      }
    }
  }

  .status-badge {
    position: absolute;
    left: 20rpx;
    top: 20rpx;
    padding: 8rpx 20rpx;
    border-radius: 24rpx;
    z-index: 10;

    .status-text {
      font-size: 24rpx;
      color: #fff;
      font-weight: 500;
    }

    &.status-parked {
      background-color: #909399;
    }

    &.status-charging {
      background-color: #67c23a;
    }

    &.status-driving {
      background-color: #409eff;
    }
  }
}

.info-section {
  padding: 24rpx;
  margin-top: -20rpx;
  position: relative;
  z-index: 10;
}

.info-card {
  background-color: #fff;
  border-radius: 20rpx;
  padding: 28rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);

  .card-title {
    font-size: 30rpx;
    font-weight: 600;
    color: #333;
    margin-bottom: 20rpx;
    padding-left: 8rpx;
  }
}

.soc-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 16rpx 20rpx;
  transition: transform 0.2s, box-shadow 0.2s;

  &:active {
    transform: scale(0.97);
    opacity: 0.9;
  }

  &.refreshing .soc-ring {
    animation: spin 0.8s linear infinite;
  }

  .soc-main {
    display: flex;
    align-items: center;
    gap: 16rpx;
  }

  .soc-ring {
    width: 100rpx;
    height: 100rpx;
    border-radius: 50%;
    border: 5rpx solid rgba(255, 255, 255, 0.3);
    border-top-color: #fff;
    border-right-color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;

    .soc-inner {
      display: flex;
      align-items: baseline;

      .soc-value {
        font-size: 34rpx;
        font-weight: 700;
        color: #fff;
      }

      .soc-unit {
        font-size: 20rpx;
        color: rgba(255, 255, 255, 0.8);
        margin-left: 2rpx;
      }
    }
  }

  .soc-info {
    display: flex;
    flex-direction: column;

    .soc-label {
      font-size: 26rpx;
      font-weight: 600;
      color: #fff;
    }

    .soc-desc {
      font-size: 24rpx;
      color: rgba(255, 255, 255, 0.75);
      margin-top: 8rpx;
    }
  }
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.status-grid {
  display: flex;
  justify-content: space-between;

  .status-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    flex: 1;

    .status-icon {
      width: 72rpx;
      height: 72rpx;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 12rpx;

      &.temp {
        background: linear-gradient(135deg, #ff9a9e, #fad0c4);
      }

      &.power {
        background: linear-gradient(135deg, #a1c4fd, #c2e9fb);
      }

      &.vehicle {
        background: linear-gradient(135deg, #fbc2eb, #a6c1ee);
      }

      &.odometer {
        background: linear-gradient(135deg, #84fab0, #8fd3f4);
      }
    }

    .status-value {
      font-size: 30rpx;
      font-weight: 700;
      color: #222;
    }

    .status-label {
      font-size: 22rpx;
      color: #999;
      margin-top: 4rpx;
    }
  }
}

.info-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 12rpx 0;
  border-bottom: 1rpx solid #f5f5f5;

  &:last-child {
    border-bottom: none;
  }

  .row-label {
    flex: 1;
    font-size: 28rpx;
    color: #333;

    &.placeholder {
      color: #999;
    }

    &.time-text {
      font-size: 32rpx;
      font-weight: 600;
      color: #409eff;
      letter-spacing: 0.5rpx;
    }

    &.address-text {
      color: #67c23a;
      line-height: 1.5;
    }
  }
}

.tire-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16rpx;

  .tire-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 16rpx 0;
    background-color: #f8f9fb;
    border-radius: 12rpx;

    .tire-label {
      font-size: 22rpx;
      color: #999;
      margin-bottom: 6rpx;
    }

    .tire-value {
      font-size: 28rpx;
      font-weight: 600;
      color: #333;

      &.tire-low {
        color: #e6a23c;
      }

      &.tire-high {
        color: #f56c6c;
      }
    }
  }
}

.bottom-space {
  height: 40rpx;
}
</style>
<template>
  <view class="trip-detail">
    <view class="map-container">
      <map
        v-if="mapReady"
        id="tripMap"
        class="trip-map"
        :latitude="mapCenter.latitude"
        :longitude="mapCenter.longitude"
        :scale="mapScale"
        :markers="markers"
        :polyline="polyline"
        :show-location="false"
        @updated="onMapUpdated"
      ></map>
      <view class="map-loading" v-if="!mapReady">
        <uni-icons type="spinner-cycle" size="24" color="#409eff"></uni-icons>
        <text class="loading-text">地图加载中...</text>
      </view>
      <view class="map-legend" v-if="trackList.length > 0">
        <view class="legend-item">
          <view class="legend-dot start"></view>
          <text>起点</text>
        </view>
        <view class="legend-item">
          <view class="legend-dot end"></view>
          <text>终点</text>
        </view>
        <view class="legend-item">
          <view class="legend-line"></view>
          <text>行驶轨迹</text>
        </view>
      </view>
      <view class="map-controls">
        <view class="control-btn" @tap="moveToTrack" v-if="trackList.length > 0">
          <uni-icons type="location" size="18" color="#fff"></uni-icons>
        </view>
      </view>
    </view>

    <view class="info-section">
      <view class="info-card">
        <view class="card-title">行程概览</view>
        <view class="overview-row">
          <view class="overview-item">
            <text class="overview-value">{{ distanceKm }}</text>
            <text class="overview-label">行驶距离(km)</text>
          </view>
          <view class="overview-item">
            <text class="overview-value">{{ trip.consumeKwh || trip.tripConsumeKwh || '-' }}</text>
            <text class="overview-label">耗电(kWh)</text>
          </view>
          <view class="overview-item">
            <text class="overview-value">{{ durationText || '-' }}</text>
            <text class="overview-label">用时</text>
          </view>
          <view class="overview-item">
            <text class="overview-value">{{ avgSpeed }}</text>
            <text class="overview-label">均速(km/h)</text>
          </view>
        </view>
      </view>

      <view class="info-card">
        <view class="card-title">起点信息</view>
        <view class="info-row">
          <uni-icons type="location" size="14" color="#409eff"></uni-icons>
          <text class="row-label">{{ stripProvince(trip.startAddress) || '-' }}</text>
        </view>
        <view class="info-row">
          <uni-icons type="clock" size="14" color="#409eff"></uni-icons>
          <text class="row-label">{{ formatDateTime(trip.tripStartTime) }}</text>
          <text class="row-tag">SOC {{ trip.startSoc }}%</text>
        </view>
      </view>

      <view class="info-card">
        <view class="card-title">终点信息</view>
        <view class="info-row">
          <uni-icons type="location" size="14" color="#f56c6c"></uni-icons>
          <text class="row-label">{{ stripProvince(trip.endAddress) || '-' }}</text>
        </view>
        <view class="info-row">
          <uni-icons type="clock" size="14" color="#f56c6c"></uni-icons>
          <text class="row-label">{{ formatDateTime(trip.tripEndTime) }}</text>
          <text class="row-tag">SOC {{ trip.endSoc }}%</text>
        </view>
      </view>

      <view class="info-card">
        <view class="card-title">电池温度</view>
        <view class="temp-row">
          <view class="temp-item">
            <text class="temp-label">最高温度</text>
            <text class="temp-value">{{ trip.maxBattTemp || '-' }}°C</text>
          </view>
          <view class="temp-item">
            <text class="temp-label">平均温度</text>
            <text class="temp-value">{{ trip.avgBattTemp || '-' }}°C</text>
          </view>
          <view class="temp-item">
            <text class="temp-label">最低温度</text>
            <text class="temp-value">{{ trip.minBattTemp || '-' }}°C</text>
          </view>
        </view>
      </view>
      <view class="bottom-space"></view>
    </view>

    <view class="playback-bar" v-if="trackList.length > 0">
      <view class="progress-container">
        <view class="progress-bar" @tap="seekProgress">
          <view class="progress-fill" :style="{ width: progressPercent + '%' }"></view>
          <view class="progress-thumb" :style="{ left: progressPercent + '%' }"></view>
        </view>
      </view>
      <view class="playback-controls">
        <view class="control-item" @tap="toggleSpeed">
          <text class="speed-text">{{ playbackSpeed }}x</text>
        </view>
        <view class="control-item play-btn" @tap="togglePlay">
          <image class="play-icon" src="/static/images/map/play.png" v-if="!isPlaying"></image>
          <image class="play-icon" src="/static/images/map/pause.png" v-else></image>
        </view>
        <view class="control-item" @tap="resetPlayback">
          <uni-icons type="reload" size="20" color="#666"></uni-icons>
        </view>
      </view>
      <view class="time-display">
        <text class="time-label">{{ currentPlaybackTime }}</text>
      </view>
    </view>
  </view>
</template>

<script>
import { getTripById } from "@/api/ev-drive/list";

export default {
  name: 'TripDetail',
  data() {
    return {
      trip: {},
      trackList: [],
      mapReady: false,
      mapCenter: { latitude: 34.24, longitude: 108.90 },
      mapScale: 13,
      polyline: [],
      allTrackPoints: [],
      isPlaying: false,
      currentIndex: 0,
      playbackSpeed: 1,
      playTimer: null,
      distanceKm: '0.0',
      durationText: '-',
      avgSpeed: 0,
      playbackSpeedOptions: [1, 4, 16, 32],
      showCarMarker: false
    };
  },
  computed: {
    markers() {
      if (this.trackList.length === 0) return [];
      const start = this.trackList[0];
      const end = this.trackList[this.trackList.length - 1];
      const curPoint = this.trackList[this.currentIndex] || start;
      const list = [
        {
          id: 1,
          latitude: Number(start.lat),
          longitude: Number(start.lon),
          width: 48,
          height: 48,
          iconPath: '/static/images/map/start.png',
          callout: {
            content: '起点',
            color: '#fff',
            fontSize: 12,
            borderRadius: 8,
            bgColor: '#409eff',
            padding: 8,
            display: 'ALWAYS'
          }
        },
        {
          id: 2,
          latitude: Number(end.lat),
          longitude: Number(end.lon),
          width: 48,
          height: 48,
          iconPath: '/static/images/map/end.png',
          callout: {
            content: '终点',
            color: '#fff',
            fontSize: 12,
            borderRadius: 8,
            bgColor: '#f56c6c',
            padding: 8,
            display: 'ALWAYS'
          }
        }
      ];
      if (this.showCarMarker) {
        list.push({
          id: 3,
          latitude: Number(curPoint.lat),
          longitude: Number(curPoint.lon),
          width: 56,
          height: 56,
          iconPath: '/static/images/map/car.png',
          rotate: curPoint.heading || 0
        });
      }
      return list;
    },
    progressPercent() {
      if (this.trackList.length <= 1) return 0;
      return (this.currentIndex / (this.trackList.length - 1)) * 100;
    },
    currentPlaybackTime() {
      if (this.trackList.length === 0) return '';
      const point = this.trackList[this.currentIndex];
      return point ? this.formatTimeOnly(point.reportTime) : '';
    }
  },
  onLoad(options) {
    if (options.id) {
      this.loadDetail(options.id);
    }
    uni.setNavigationBarTitle({ title: '行程详情' });
  },
  onUnload() {
    this.stopPlayback();
  },
  methods: {
    async loadDetail(id) {
      try {
        const response = await getTripById(id);
        if (response.code === 200) {
          this.trip = response.data || {};
          this.trackList = response.data?.trackList || [];
          this.calcTripStats();
          this.initMap();
        }
      } catch (error) {
        this.$modal.msg('加载行程详情失败');
      }
    },
    calcTripStats() {
      const startOdo = this.trip.startOdometer || 0;
      const endOdo = this.trip.endOdometer || 0;
      this.distanceKm = (endOdo - startOdo).toFixed(1);

      const durationMs = this.getDurationMs(this.trip.tripStartTime, this.trip.tripEndTime);
      this.durationText = this.formatDuration(durationMs);
      this.avgSpeed = durationMs > 0 ? Math.round((parseFloat(this.distanceKm) / (durationMs / 3600000))) : 0;
    },
    // 一进来初始化地图中心和缩放比例
    calcMapBounds(points) {
      if (!points || points.length === 0) return null;
      let minLat = Infinity, maxLat = -Infinity;
      let minLon = Infinity, maxLon = -Infinity;
      points.forEach(p => {
        const lat = Number(p.lat);
        const lon = Number(p.lon);
        if (lat < minLat) minLat = lat;
        if (lat > maxLat) maxLat = lat;
        if (lon < minLon) minLon = lon;
        if (lon > maxLon) maxLon = lon;
      });
      const latSpan = maxLat - minLat;
      const lonSpan = maxLon - minLon;
      const maxSpan = Math.max(latSpan, lonSpan);
      const centerLat = (minLat + maxLat) / 2;
      const centerLon = (minLon + maxLon) / 2;
      let scale = 13;
      if (maxSpan > 10) scale = 3;
      else if (maxSpan > 5) scale = 4;
      else if (maxSpan > 2) scale = 6;
      else if (maxSpan > 1) scale = 7;
      else if (maxSpan > 0.5) scale = 9;
      else if (maxSpan > 0.2) scale = 10;
      else if (maxSpan > 0.1) scale = 11;
      else if (maxSpan > 0.05) scale = 12;
      else if (maxSpan > 0.01) scale = 13;
      return {
        center: { latitude: centerLat, longitude: centerLon },
        scale
      };
    },
    initMap() {
      if (this.trackList.length > 0) {
        const bounds = this.calcMapBounds(this.trackList);
        if (bounds) {
          this.mapCenter = bounds.center;
          this.mapScale = bounds.scale;
        }

        this.allTrackPoints = this.trackList.map(p => ({
          latitude: Number(p.lat),
          longitude: Number(p.lon)
        }));

        if (this.allTrackPoints.length >= 2) {
          this.polyline = [{
            points: this.allTrackPoints,
            color: '#5a77d7',
            width: 15
          }];
        }
      } else if (this.trip.startLat) {
        this.mapCenter = {
          latitude: Number(this.trip.startLat),
          longitude: Number(this.trip.startLon)
        };
      }
      this.mapReady = true;
    },
    onMapUpdated() {
    },
    moveToTrack() {
      if (this.trackList.length === 0) return;
      const bounds = this.calcMapBounds(this.trackList);
      if (bounds) {
        this.mapCenter = bounds.center;
        this.mapScale = bounds.scale;
      }
    },
    updateCarMarker() {
      if (this.trackList.length === 0) return;
      const point = this.trackList[this.currentIndex];
      this.mapCenter = {
        latitude: Number(point.lat),
        longitude: Number(point.lon)
      };
    },
    togglePlay() {
      if (this.isPlaying) {
        this.stopPlayback();
      } else {
        this.startPlayback();
      }
    },
    startPlayback() {
      if (this.trackList.length === 0) return;
      if (this.currentIndex >= this.trackList.length - 1) {
        this.currentIndex = 0;
      }
      this.isPlaying = true;
      this.showCarMarker = true;
      this.updateCarMarker();
      this.scheduleNextFrame();
    },
    stopPlayback() {
      this.isPlaying = false;
      if (this.playTimer) {
        clearTimeout(this.playTimer);
        this.playTimer = null;
      }
    },
    scheduleNextFrame() {
      if (!this.isPlaying) return;
      if (this.currentIndex >= this.trackList.length - 1) {
        this.isPlaying = false;
        return;
      }
      const frameInterval = Math.max(30, 500 / this.playbackSpeed);
      this.playTimer = setTimeout(() => {
        this.currentIndex++;
        this.updateCarMarker();
        this.scheduleNextFrame();
      }, frameInterval);
    },
    resetPlayback() {
      this.stopPlayback();
      this.currentIndex = 0;
      this.showCarMarker = true;
      this.updateCarMarker();
      this.moveToTrack();
    },
    toggleSpeed() {
      const currentIdx = this.playbackSpeedOptions.indexOf(this.playbackSpeed);
      this.playbackSpeed = this.playbackSpeedOptions[(currentIdx + 1) % this.playbackSpeedOptions.length];
    },
    seekProgress(e) {
      if (this.trackList.length <= 1) return;
      const query = uni.createSelectorQuery().in(this);
      query.select('.progress-bar').boundingClientRect();
      query.exec((res) => {
        if (res && res[0]) {
          const rect = res[0];
          const tapX = e.detail.x - rect.left;
          const ratio = Math.max(0, Math.min(1, tapX / rect.width));
          this.currentIndex = Math.floor(ratio * (this.trackList.length - 1));
          this.showCarMarker = true;
          this.updateCarMarker();
        }
      });
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
    formatDateTime(dateStr) {
      if (!dateStr) return '';
      const d = new Date(dateStr);
      const y = d.getFullYear();
      const m = String(d.getMonth() + 1).padStart(2, '0');
      const day = String(d.getDate()).padStart(2, '0');
      const h = String(d.getHours()).padStart(2, '0');
      const mi = String(d.getMinutes()).padStart(2, '0');
      return `${y}-${m}-${day} ${h}:${mi}`;
    },
    formatTimeOnly(dateStr) {
      if (!dateStr) return '';
      const d = new Date(dateStr);
      const h = String(d.getHours()).padStart(2, '0');
      const mi = String(d.getMinutes()).padStart(2, '0');
      const s = String(d.getSeconds()).padStart(2, '0');
      return `${h}:${mi}:${s}`;
    },
    stripProvince(address) {
      if (!address) return '';
      return address.replace(/^[\u4e00-\u9fa5]+?省/, '');
    }
  }
};
</script>

<style lang="scss">
.trip-detail {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 160rpx;
}

.map-container {
  position: relative;
  width: 100%;
  height: 600rpx;

  .trip-map {
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

  .map-legend {
    position: absolute;
    left: 20rpx;
    bottom: 20rpx;
    background-color: rgba(255, 255, 255, 0.9);
    padding: 16rpx 20rpx;
    border-radius: 12rpx;
    display: flex;
    gap: 20rpx;
    box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);

    .legend-item {
      display: flex;
      align-items: center;
      gap: 8rpx;
      font-size: 22rpx;
      color: #666;
    }

    .legend-dot {
      width: 16rpx;
      height: 16rpx;
      border-radius: 50%;

      &.start {
        background-color: #409eff;
      }

      &.end {
        background-color: #f56c6c;
      }
    }

    .legend-line {
      width: 24rpx;
      height: 4rpx;
      background-color: #409eff;
      border-radius: 2rpx;
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

.overview-row {
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
  gap: 16rpx;

  .overview-item {
    display: flex;
    flex-direction: column;
    align-items: center;

    .overview-value {
      font-size: 40rpx;
      font-weight: 700;
      color: #222;
    }

    .overview-label {
      font-size: 24rpx;
      color: #999;
      margin-top: 6rpx;
    }
  }
}

.info-row {
  display: flex;
  align-items: flex-start;
  flex-wrap: wrap;
  gap: 10rpx;
  padding: 12rpx 0;
  border-bottom: 1rpx solid #f5f5f5;

  &:last-child {
    border-bottom: none;
  }

  .row-label {
    flex: 1;
    font-size: 28rpx;
    color: #333;
    white-space: normal;
    word-break: break-all;
    line-height: 1.5;
  }

  .row-tag {
    font-size: 24rpx;
    color: #409eff;
    background-color: #ecf5ff;
    padding: 4rpx 12rpx;
    border-radius: 8rpx;
    flex-shrink: 0;
  }
}

.temp-row {
  display: flex;
  justify-content: space-around;

  .temp-item {
    display: flex;
    flex-direction: column;
    align-items: center;

    .temp-label {
      font-size: 24rpx;
      color: #999;
      margin-bottom: 8rpx;
    }

    .temp-value {
      font-size: 32rpx;
      font-weight: 700;
      color: #409eff;
    }
  }
}

.bottom-space {
  height: 40rpx;
}

.playback-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: #fff;
  padding: 20rpx 24rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  box-shadow: 0 -2rpx 20rpx rgba(0, 0, 0, 0.08);
  z-index: 100;

  .progress-container {
    margin-bottom: 16rpx;
  }

  .progress-bar {
    position: relative;
    height: 8rpx;
    background-color: #e8e8e8;
    border-radius: 4rpx;

    .progress-fill {
      position: absolute;
      left: 0;
      top: 0;
      bottom: 0;
      background-color: #409eff;
      border-radius: 4rpx;
      transition: width 0.2s ease;
    }

    .progress-thumb {
      position: absolute;
      top: 50%;
      width: 24rpx;
      height: 24rpx;
      background-color: #409eff;
      border-radius: 50%;
      transform: translate(-50%, -50%);
      box-shadow: 0 2rpx 8rpx rgba(64, 158, 255, 0.4);
      transition: left 0.2s ease;
    }
  }

  .playback-controls {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 80rpx;

    .control-item {
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .speed-btn {
      width: 64rpx;
      height: 64rpx;
      background-color: #ecf5ff;
      border-radius: 50%;
      box-shadow: 0 2rpx 8rpx rgba(64, 158, 255, 0.15);

      .speed-text {
        font-size: 26rpx;
        font-weight: 700;
        color: #409eff;
      }

      &:active {
        transform: scale(0.92);
      }
    }

    .play-btn {
      position: relative;
      width: 112rpx;
      height: 112rpx;
      display: flex;
      align-items: center;
      justify-content: center;

      &:active {
        transform: scale(0.95);
      }

      .play-ring {
        position: absolute;
        top: -6rpx;
        left: -6rpx;
        right: -6rpx;
        bottom: -6rpx;
        border: 3rpx solid rgba(90, 119, 215, 0.3);
        border-radius: 50%;
        transition: all 0.3s ease;
      }

      &.playing .play-ring {
        top: -12rpx;
        left: -12rpx;
        right: -12rpx;
        bottom: -12rpx;
        border-color: rgba(90, 119, 215, 0.15);
        animation: ringPulse 1.5s ease-out infinite;
      }

      .play-icon {
        width: 80rpx;
        height: 80rpx;
      }

      .play-triangle {
        width: 0;
        height: 0;
        border-left: 28rpx solid #fff;
        border-top: 18rpx solid transparent;
        border-bottom: 18rpx solid transparent;
        margin-left: 6rpx;
      }

      .pause-bar {
        width: 8rpx;
        height: 32rpx;
        background-color: #fff;
        border-radius: 4rpx;

        &.left {
          margin-right: 6rpx;
        }
        &.right {
          margin-left: 6rpx;
        }
      }
    }

    .reset-btn {
      width: 64rpx;
      height: 64rpx;
      background-color: #f0f4fa;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;

      &:active {
        transform: scale(0.92);
      }
    }
  }

  .time-display {
    text-align: center;
    margin-top: 12rpx;

    .time-label {
      font-size: 24rpx;
      color: #999;
      font-family: monospace;
    }
  }
}
</style>
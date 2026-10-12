<template>
  <view class="index-container">
    <view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>

    <view class="header">
      <view class="header-bg">
        <view class="blob blob-1"></view>
        <view class="blob blob-2"></view>
        <view class="blob blob-3"></view>
      </view>
      <view class="header-content">
        <view class="welcome">
          <text class="greeting">{{ greeting }} 👋</text>
          <text class="username">{{ user.nickName || user.username || '未登录' }}</text>
          <text class="today-date">{{ currentDate }}</text>
        </view>
        <view class="avatar-wrapper" @tap="handleToInfo">
          <image class="avatar" :src="avatarSrc" mode="aspectFill"></image>
          <view class="avatar-ring"></view>
        </view>
      </view>
    </view>

    <view class="overview-card">
      <view class="overview-item" v-for="(item, index) in overviewData" :key="index">
        <text class="item-value">{{ formatNumber(item.value) }}</text>
        <text class="item-label">{{ item.label }}</text>
      </view>
    </view>

    <view class="quick-actions">
      <view class="section-header">
        <view class="section-title-wrap">
          <view class="title-bar"></view>
          <text class="section-title">快捷功能</text>
        </view>
        <text class="section-sub">常用菜单直达</text>
      </view>
      <view class="action-grid">
        <view class="action-item" v-for="(item, index) in quickActions" :key="index" @tap="handleQuickAction(item)">
          <view class="icon-box" :style="{ backgroundColor: item.bgColor || defaultBgColor }">
            <uni-icons custom-prefix="iconfont" :type="item.icon" size="26" color="#fff"></uni-icons>
          </view>
          <text class="action-name">{{ item.name }}</text>
        </view>
        <view class="action-item placeholder" v-if="quickActions.length === 0">
          <view class="icon-box empty-box">
            <uni-icons type="help" size="26" color="#c0c4cc"></uni-icons>
          </view>
          <text class="action-name">暂无数据</text>
        </view>
      </view>
    </view>

    <view class="version-section">
      <view class="section-header">
        <view class="section-title-wrap">
          <view class="title-bar"></view>
          <text class="section-title">版本动态</text>
        </view>
        <text class="section-sub">左右滑动切换</text>
      </view>
      <scroll-view
          class="version-swipe-container"
          scroll-x="true"
          @touchstart="handleTouchStart"
          @touchend="handleTouchEnd"
          scroll-with-animation="true"
          show-scrollbar="false"
          :style="{ pointerEvents: loading ? 'none' : 'auto' }"
      >
        <view class="version-swipe-content">
          <view class="version-list">
            <view
                class="version-item"
                v-for="(item, index) in versionList"
                :key="index"
                @tap="showVersionDetail(item)"
            >
              <view class="version-dot"></view>
              <view class="version-main">
                <view class="version-header">
                  <text class="version-title">{{ item.updateTitle }}</text>
                  <view class="version-badge">
                    <uni-icons type="right" size="12" color="#409eff"></uni-icons>
                  </view>
                </view>
                <view class="version-meta">
                  <view class="meta-item">
                    <uni-icons type="calendar" size="12" color="#909399"></uni-icons>
                    <text>{{ formatTime(item.createTime) }}</text>
                  </view>
                </view>
              </view>
            </view>

            <view class="load-more" v-if="loading">
              <uni-load-more status="loading" />
            </view>

            <view class="no-more" v-if="!loading && pageNum >= totalPage && versionList.length > 0">
              <view class="divider-line"></view>
              <text>已加载全部版本</text>
              <view class="divider-line"></view>
            </view>

            <view class="no-data" v-if="versionList.length === 0 && !loading">
              <image class="no-data-icon" src="https://a0ai.marscode.cn/api/ide/v1/text_to_image?prompt=no%20data%20empty%20state%20illustration%20minimalist%20soft%20colors&image_size=square" mode="aspectFit"></image>
              <text class="no-data-text">暂无版本更新记录</text>
            </view>

            <view class="page-indicator" v-if="total > 0 && !loading">
              <view class="indicator-dots">
                <view
                    class="indicator-dot"
                    :class="{ active: i === pageNum - 1 }"
                    v-for="i in totalPage"
                    :key="i"
                ></view>
              </view>
              <text class="indicator-text">{{ pageNum }} / {{ totalPage }}</text>
            </view>

          </view>
        </view>
      </scroll-view>
    </view>

    <uni-popup ref="versionPopup" type="center" :mask-click="true">
      <view class="version-detail-popup" @touchmove.stop="handlePopupTouch">
        <view class="popup-header">
          <view class="popup-header-bg"></view>
          <view class="popup-header-content">
            <view class="popup-icon-box">
              <uni-icons type="info" size="20" color="#fff"></uni-icons>
            </view>
            <text class="popup-title">版本详情</text>
          </view>
          <view class="popup-close" @tap="closeVersionPopup">
            <uni-icons type="closeempty" size="20" color="#909399"></uni-icons>
          </view>
        </view>
        <scroll-view class="popup-content" scroll-y="true">
          <view class="detail-item">
            <view class="detail-icon">
              <uni-icons type="flag" size="14" color="#409eff"></uni-icons>
            </view>
            <text class="label">版本号</text>
            <text class="value">v{{ currentVersion.version }}</text>
          </view>
          <view class="detail-item">
            <view class="detail-icon">
              <uni-icons type="star" size="14" color="#e6a23c"></uni-icons>
            </view>
            <text class="label">更新标题</text>
            <text class="value">{{ currentVersion.updateTitle }}</text>
          </view>
          <view class="detail-item">
            <view class="detail-icon">
              <uni-icons type="calendar" size="14" color="#67c23a"></uni-icons>
            </view>
            <text class="label">发布时间</text>
            <text class="value">{{ formatTime(currentVersion.createTime) }}</text>
          </view>
          <view class="detail-item">
            <view class="detail-icon">
              <uni-icons type="settings" size="14" color="#909399"></uni-icons>
            </view>
            <text class="label">更新类型</text>
            <text class="value">
              <text class="tag">{{ currentVersion.packageType === 1 ? 'WGT包' : 'APK包' }}</text>
              <text class="tag warn" v-if="currentVersion.isForceUpdate === 0">强制更新</text>
              <text class="tag normal" v-else>普通更新</text>
            </text>
          </view>
          <view class="detail-item log-item">
            <view class="detail-icon">
              <uni-icons type="list" size="14" color="#409eff"></uni-icons>
            </view>
            <text class="label">更新日志</text>
            <view class="log-content">
              <view class="log-line" v-for="(line, idx) in formatUpdateLog(currentVersion.updateLog)" :key="idx">
                <text class="log-dot">•</text>
                <text class="log-text">{{ line }}</text>
              </view>
            </view>
          </view>
        </scroll-view>
        <view class="popup-footer">
          <button class="close-btn" @tap="closeVersionPopup">知道了</button>
        </view>
      </view>
    </uni-popup>

    <UpdatePopup
        :updateInfo="updateInfo"
        :showPopup="showUpdatePopup"
        @close="showUpdatePopup = false"
    />
  </view>
</template>

<script>
import {getUserProfile} from "@/api/system/user";
import defaultAvatar from '@/static/images/profile.jpg'
import {getMessageLogCount} from "@/api/message/monitor"
import {getJobLogCount} from "@/api/job/jobLog"
import {getOperLogCount} from "@/api/monitor/operlog"
import { queryMqttTotalCount} from '@/api/iot/fish'
import UpdatePopup from '@/components/update-popup/update-popup.vue';
import {getLastAppVersion, getAppVersionControlPage} from "@/api/app/appVersionControl";
import {getMenuClickRank, reportMenuClick} from "@/api/system/menu/menu";

export default {
  components: {
    UpdatePopup
  },
  data() {
    return {
      avatarSrc: '',
      statusBarHeight: 0,
      defaultAvatar,
      user: {},
      showUpdatePopup: false,
      updateInfo: {},
      overviewData: [
        { label: '操作日志', value: 0 },
        { label: 'IOT上报', value: 0 },
        { label: '消息发送', value: 0 },
        { label: '任务调度', value: 0 },
      ],
      quickActions: [],
      defaultBgColor: '#607d8b',
      versionList: [],
      pageNum: 1,
      pageSize: 5,
      total: 0,
      totalPage: 0,
      loading: false,
      touchStartX: 0,
      touchEndX: 0,
      swipeThreshold: 50,
      currentVersion: {},
    }
  },
  computed: {
    greeting() {
      const hour = new Date().getHours()
      if (hour < 6) return '凌晨好'
      if (hour < 9) return '早上好'
      if (hour < 12) return '上午好'
      if (hour < 14) return '中午好'
      if (hour < 17) return '下午好'
      if (hour < 19) return '傍晚好'
      return '晚上好'
    },
    currentDate() {
      const now = new Date()
      const month = now.getMonth() + 1
      const day = now.getDate()
      const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
      return `${month}月${day}日 ${weekDays[now.getDay()]}`
    }
  },
  onLoad() {
    this.getAllLogCounts()
    this.getVersionList()
    this.handleToUpgrade()
  },
  async onShow() {
    const systemInfo = uni.getSystemInfoSync()
    this.statusBarHeight = systemInfo.statusBarHeight
    await this.getUser()
    this.getQuickActions()
  },
  onPullDownRefresh() {
    this.refreshData();
  },
  methods: {
    handleTouchStart(e) {
      this.touchStartX = e.changedTouches[0].clientX;
    },
    handleTouchEnd(e) {
      this.touchEndX = e.changedTouches[0].clientX;
      this.judgeSwipeDirection();
    },
    judgeSwipeDirection() {
      const diffX = this.touchEndX - this.touchStartX;
      if (diffX < -this.swipeThreshold && this.pageNum < this.totalPage) {
        this.pageNum++;
        this.getVersionList();
      } else if (diffX > this.swipeThreshold && this.pageNum > 1) {
        this.pageNum--;
        this.getVersionList();
      }
    },
    handlePopupTouch(e) {
      e.stopPropagation();
    },
    formatNumber(value) {
      if (value >= 10000) {
        return (value / 10000).toFixed(1) + 'w'
      }
      if (value >= 1000) {
        return (value / 1000).toFixed(1) + 'k'
      }
      return String(value)
    },
    async getQuickActions() {
      const res = await getMenuClickRank();
      if (res.code === 200 && res.data) {
        this.quickActions = res.data.map((item, idx) => ({
          name: item.menuName,
          icon: item.icon,
          bgColor: item.iconBgColor || this.defaultBgColor,
          path: item.component,
          menuId: item.menuId
        }));
      }
    },
    async getVersionList() {
      if (this.loading) return
      this.loading = true
      try {
        const systemInfo = uni.getSystemInfoSync();
        const queryParams = {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          appId: systemInfo.appId
        }
        const response = await getAppVersionControlPage(queryParams)
        if (response.code === 200) {
          this.versionList = response.data || [];
          this.total = response.total || 0;
          this.totalPage = Math.ceil(this.total / this.pageSize);
        }
      } catch (error) {
        this.versionList = [];
      } finally {
        this.loading = false;
        uni.stopPullDownRefresh();
      }
    },
    async showVersionDetail(version) {
      this.currentVersion = version;
      this.$refs.versionPopup.open();
    },
    formatTime(timeStr) {
      if (!timeStr) return '';
      const date = new Date(timeStr);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hour = String(date.getHours()).padStart(2, '0');
      const minute = String(date.getMinutes()).padStart(2, '0');
      return `${year}-${month}-${day} ${hour}:${minute}`;
    },
    formatUpdateLog(log) {
      if (!log) return ['暂无更新日志'];
      return log.split('\n').filter(line => line.trim());
    },
    closeVersionPopup() {
      this.$refs.versionPopup.close();
      this.currentVersion = {};
    },
    handleToUpgrade() {
      // #ifdef APP-PLUS
      const systemInfo = uni.getSystemInfoSync();
      const requestObject = {
        appId: systemInfo.appId,
        version: systemInfo.appWgtVersion
      };
      try{
        getLastAppVersion(requestObject).then(response => {
          this.$modal.closeLoading();
          if (response && response.data) {
            this.updateInfo = response.data;
            this.showUpdatePopup = true;
          }
        });
      }catch(error){
        this.$modal.closeLoading();
      }
      // #endif
    },
    async getAllLogCounts() {
      try {
        const [operLogRes, messageLogRes, jobLogRes,mqttRes] = await Promise.all([
          getOperLogCount(),
          getMessageLogCount(),
          getJobLogCount(),
          queryMqttTotalCount()
        ]);
        this.overviewData = [
          { label: '操作日志', value: operLogRes.data || 0 },
          { label: 'IOT上报', value: mqttRes.data || 0 },
          { label: '消息发送', value: messageLogRes.data || 0 },
          { label: '任务调度', value: jobLogRes.data || 0 }
        ];
      } catch (error) {
        console.error('获取统计数据失败：', error);
      }
    },
    refreshData(){
      try {
        this.pageNum = 1;
        this.getVersionList();
        this.getUser();
        this.getAllLogCounts();
        this.handleToUpgrade();
        this.getQuickActions();
        uni.stopPullDownRefresh();
        this.$modal.msgSuccess("刷新成功");
      }catch (error) {
        uni.stopPullDownRefresh();
        this.$modal.msgError("刷新失败");
      }
    },
    handleToInfo() {
      this.$tab.navigateTo('/pages/mine/info/index');
    },
    async getUser() {
      await getUserProfile().then(response => {
        if (response && response.data) {
          this.user = response.data;
          if (this.user.avatar) {
            uni.getImageInfo({
              src: this.user.avatar,
              success: () => {
                this.avatarSrc = this.user.avatar;
              },
              fail: () => {
                this.avatarSrc = this.defaultAvatar;
              }
            });
          } else {
            this.avatarSrc = this.defaultAvatar;
          }
        }
      }).catch(error => {
        console.error('获取用户信息失败：', error);
      });
    },
    async handleQuickAction(item) {
      if (!item.path) {
        return;
      }
      try {
        await reportMenuClick(item);
      } catch (error) {
        console.error('菜单埋点上报失败:', error);
      }
      let url = item.path;
      if (item.name) {
        const separator = url.includes('?') ? '&' : '?';
        url += `${separator}pageTitle=${encodeURIComponent(item.name)}`;
      }
      uni.navigateTo({
        url: url
      });
    }
  }
}
</script>

<style lang="scss" scoped>
.index-container {
  min-height: 100vh;
  background: linear-gradient(180deg, #eef2f7 0%, #f5f7fa 300rpx);
  padding-bottom: 40rpx;

  .status-bar {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  }

  .header {
    position: relative;
    padding: 20rpx 40rpx 80rpx;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    overflow: hidden;

    .header-bg {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      pointer-events: none;

      .blob {
        position: absolute;
        border-radius: 50%;
        opacity: 0.15;
      }

      .blob-1 {
        width: 300rpx;
        height: 300rpx;
        background: #fff;
        top: -100rpx;
        right: -80rpx;
      }

      .blob-2 {
        width: 200rpx;
        height: 200rpx;
        background: #ffd700;
        bottom: -60rpx;
        left: -40rpx;
        opacity: 0.1;
      }

      .blob-3 {
        width: 120rpx;
        height: 120rpx;
        background: #fff;
        top: 30%;
        right: 25%;
        opacity: 0.08;
      }
    }

    .header-content {
      position: relative;
      z-index: 2;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .welcome {
      flex: 1;

      .greeting {
        font-size: 34rpx;
        font-weight: 600;
        color: #fff;
        margin-bottom: 6rpx;
        display: block;
        letter-spacing: 1rpx;
      }

      .username {
        font-size: 30rpx;
        color: rgba(255, 255, 255, 0.95);
        display: block;
        margin-bottom: 6rpx;
        font-weight: 500;
      }

      .today-date {
        font-size: 22rpx;
        color: rgba(255, 255, 255, 0.7);
      }
    }

    .avatar-wrapper {
      position: relative;
      width: 100rpx;
      height: 100rpx;

      .avatar {
        width: 100rpx;
        height: 100rpx;
        border-radius: 50%;
        border: 4rpx solid rgba(255, 255, 255, 0.9);
        box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.15);
        position: relative;
        z-index: 2;
      }

      .avatar-ring {
        position: absolute;
        top: -6rpx;
        left: -6rpx;
        right: -6rpx;
        bottom: -6rpx;
        border-radius: 50%;
        border: 2rpx dashed rgba(255, 255, 255, 0.5);
        animation: rotate 20s linear infinite;
      }
    }
  }

  .overview-card {
    margin: -50rpx 40rpx 0;
    padding: 30rpx 24rpx;
    background-color: #fff;
    border-radius: 24rpx;
    display: flex;
    justify-content: space-around;
    box-shadow: 0 10rpx 40rpx rgba(102, 126, 234, 0.12);
    position: relative;
    z-index: 5;

    .overview-item {
      text-align: center;
      flex: 1;
      padding: 0 10rpx;

      .item-value {
        font-size: 36rpx;
        font-weight: 700;
        color: #2c3e50;
        margin-bottom: 8rpx;
        display: block;
        line-height: 1.2;
        white-space: nowrap;
      }

      .item-label {
        font-size: 24rpx;
        color: #909399;
        white-space: nowrap;
      }
    }
  }

  .section-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin: 40rpx 0 24rpx;

    .section-title-wrap {
      display: flex;
      align-items: center;
    }

    .title-bar {
      width: 8rpx;
      height: 28rpx;
      background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
      border-radius: 4rpx;
      margin-right: 14rpx;
    }

    .section-title {
      font-size: 32rpx;
      font-weight: 600;
      color: #2c3e50;
    }

    .section-sub {
      font-size: 24rpx;
      color: #c0c4cc;
    }
  }

  .quick-actions {
    padding: 0 40rpx;

    .action-grid {
      display: flex;
      flex-wrap: wrap;

      .action-item {
        width: 25%;
        padding: 24rpx 10rpx;
        text-align: center;
        box-sizing: border-box;

        &:active {
          transform: scale(0.92);
          transition: transform 0.2s ease;
        }

        .icon-box {
          width: 96rpx;
          height: 96rpx;
          margin: 0 auto 16rpx;
          border-radius: 28rpx;
          display: flex;
          align-items: center;
          justify-content: center;
          box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.1);
          transition: transform 0.3s;

          &.empty-box {
            background: linear-gradient(135deg, #e0e0e0 0%, #f5f5f5 100%);
            box-shadow: none;
          }
        }

        .action-name {
          font-size: 24rpx;
          color: #606266;
          font-weight: 500;
        }

        &.placeholder {
          opacity: 0.6;
        }
      }
    }
  }

  .version-section {
    margin-bottom: 20rpx;
    padding: 0 40rpx;

    .version-swipe-container {

      .version-swipe-content {
        width: 100%;

        .version-list {
          padding: 0;

          .version-item {
            display: flex;
            align-items: center;
            padding: 28rpx 0;
            border-bottom: 2rpx solid #f5f5f8;
            position: relative;

            &:last-child {
              border-bottom: none;
            }

            .version-dot {
              width: 20rpx;
              height: 20rpx;
              border-radius: 50%;
              background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
              margin-right: 20rpx;
              flex-shrink: 0;
              box-shadow: 0 4rpx 10rpx rgba(102, 126, 234, 0.3);
              position: relative;

              &::after {
                content: '';
                position: absolute;
                top: 50%;
                left: 50%;
                transform: translate(-50%, -50%);
                width: 6rpx;
                height: 6rpx;
                background: #fff;
                border-radius: 50%;
              }
            }

            .version-main {
              flex: 1;
              min-width: 0;

              .version-header {
                display: flex;
                align-items: center;
                justify-content: space-between;
                margin-bottom: 10rpx;

                .version-title {
                  font-size: 28rpx;
                  color: #2c3e50;
                  font-weight: 500;
                  flex: 1;
                  overflow: hidden;
                  text-overflow: ellipsis;
                  white-space: nowrap;
                  margin-right: 12rpx;
                }

                .version-badge {
                  width: 40rpx;
                  height: 40rpx;
                  border-radius: 50%;
                  background: #f0f3ff;
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  flex-shrink: 0;
                }
              }

              .version-meta {
                display: flex;
                align-items: center;

                .meta-item {
                  display: flex;
                  align-items: center;
                  gap: 6rpx;

                  text {
                    font-size: 22rpx;
                    color: #909399;
                  }
                }
              }
            }
          }

          .load-more {
            padding: 20rpx 0;
            text-align: center;
          }

          .no-more {
            padding: 30rpx 0;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 20rpx;

            .divider-line {
              width: 60rpx;
              height: 2rpx;
              background: linear-gradient(90deg, transparent, #e0e0e0, transparent);
            }

            text {
              font-size: 22rpx;
              color: #c0c4cc;
            }
          }

          .no-data {
            padding: 60rpx 0;
            text-align: center;

            .no-data-icon {
              width: 160rpx;
              height: 160rpx;
              margin-bottom: 20rpx;
            }

            .no-data-text {
              font-size: 26rpx;
              color: #c0c4cc;
            }
          }

          .page-indicator {
            padding: 24rpx 0 10rpx;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 20rpx;

            .indicator-dots {
              display: flex;
              gap: 8rpx;

              .indicator-dot {
                width: 12rpx;
                height: 12rpx;
                border-radius: 6rpx;
                background: #e0e0e0;
                transition: all 0.3s ease;

                &.active {
                  width: 28rpx;
                  background: linear-gradient(90deg, #667eea, #764ba2);
                }
              }
            }

            .indicator-text {
              font-size: 22rpx;
              color: #c0c4cc;
            }
          }
        }
      }
    }
  }

  .version-detail-popup {
    width: 620rpx;
    background-color: #fff;
    border-radius: 28rpx;
    overflow: hidden;
    max-height: 85vh;
    display: flex;
    flex-direction: column;

    .popup-header {
      position: relative;
      padding: 30rpx 30rpx 40rpx;
      display: flex;
      align-items: flex-end;
      justify-content: space-between;

      .popup-header-bg {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 120rpx;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      }

      .popup-header-content {
        position: relative;
        z-index: 2;
        display: flex;
        align-items: center;

        .popup-icon-box {
          width: 60rpx;
          height: 60rpx;
          border-radius: 16rpx;
          background: rgba(255, 255, 255, 0.25);
          backdrop-filter: blur(10px);
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 16rpx;
        }

        .popup-title {
          font-size: 32rpx;
          font-weight: 600;
          color: #fff;
        }
      }

      .popup-close {
        position: relative;
        z-index: 2;
        width: 52rpx;
        height: 52rpx;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.95);
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }

    .popup-content {
      flex: 1;
      padding: 10rpx 30rpx 30rpx;
      overflow-y: auto;

      .detail-item {
        margin-bottom: 28rpx;
        display: flex;
        align-items: flex-start;
        flex-wrap: wrap;

        &.log-item {
          flex-wrap: wrap;
        }

        .detail-icon {
          width: 44rpx;
          height: 44rpx;
          border-radius: 12rpx;
          background: #f5f7fa;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 14rpx;
          margin-top: 2rpx;
          flex-shrink: 0;
        }

        .label {
          font-size: 28rpx;
          color: #606266;
          font-weight: 500;
          flex: 1;
          margin-bottom: 10rpx;
        }

        .value {
          font-size: 28rpx;
          color: #2c3e50;
          width: 100%;
          padding-left: 58rpx;
          display: flex;
          flex-wrap: wrap;
          gap: 10rpx;

          .tag {
            display: inline-block;
            padding: 4rpx 16rpx;
            border-radius: 8rpx;
            font-size: 22rpx;
            background: #ecf5ff;
            color: #409eff;

            &.warn {
              background: #fef0f0;
              color: #f56c6c;
            }

            &.normal {
              background: #f0f9eb;
              color: #67c23a;
            }
          }
        }

        .log-content {
          width: 100%;
          padding-left: 58rpx;

          .log-line {
            display: flex;
            align-items: flex-start;
            margin-bottom: 10rpx;

            .log-dot {
              color: #409eff;
              margin-right: 10rpx;
              line-height: 1.6;
              font-size: 30rpx;
            }

            .log-text {
              flex: 1;
              font-size: 26rpx;
              color: #2c3e50;
              line-height: 1.6;
            }
          }
        }
      }
    }

    .popup-footer {
      padding: 24rpx 30rpx 30rpx;
      background: #fff;

      .close-btn {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: #fff;
        border: none;
        border-radius: 48rpx;
        font-size: 30rpx;
        padding: 16rpx 0;
        width: 100%;
        min-height: 80rpx;
        box-sizing: border-box;
        font-weight: 500;
        letter-spacing: 2rpx;
      }
    }
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
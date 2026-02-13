<template>
  <view class="index-container">
    <!-- 顶部状态栏 -->
    <view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>

    <!-- 头部区域 -->
    <view class="header">
      <view class="welcome">
        <text class="greeting">{{ greeting }}</text>
        <text class="username">{{ user.nickName || user.username || '未登录' }}</text>
      </view>
      <image class="avatar" :src="avatarSrc" mode="aspectFill" @tap="handleToInfo"></image>
    </view>

    <!-- 数据概览 -->
    <view class="overview">
      <view class="overview-item" v-for="(item, index) in overviewData" :key="index">
        <view class="item-value">{{ item.value }}</view>
        <view class="item-label">{{ item.label }}</view>
      </view>
    </view>

    <!-- 快捷功能区 -->
    <view class="quick-actions">
      <view class="section-title">快捷功能</view>
      <view class="action-grid">
        <view class="action-item" v-for="(item, index) in quickActions" :key="index" @tap="handleQuickAction(item)">
          <view class="icon-box" :style="{ backgroundColor: item.bgColor }">
            <uni-icons custom-prefix="iconfont" :type="item.icon" size="24" color="#fff"></uni-icons>
          </view>
          <text class="action-name">{{ item.name }}</text>
        </view>
      </view>
    </view>

    <view class="version-section">
      <view class="section-title">版本动态</view>
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
              <view class="version-content">
                <text class="version-title">{{ item.updateTitle }}</text>
                <text class="version-time">{{ formatTime(item.createTime) }}</text>
              </view>
              <uni-icons type="right" size="16" color="#999"></uni-icons>
            </view>

            <!-- 加载状态 -->
            <view class="load-more" v-if="loading">
              <uni-load-more status="loading" />
            </view>

            <!-- 无更多数据 -->
            <view class="no-more" v-if="!loading && pageNum >= totalPage && versionList.length > 0">
              <text>已加载全部版本</text>
            </view>

            <!-- 无数据 -->
            <view class="no-data" v-if="versionList.length === 0 && !loading">
              <text>暂无版本更新记录</text>
            </view>

            <view class="page-indicator" v-if="total > 0 && !loading">
              第 {{ pageNum }} 页 / 共 {{ totalPage }} 页,左右滑动切换
            </view>

          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 版本详情弹窗 -->
    <uni-popup ref="versionPopup" type="center" :mask-click="false">
      <view class="version-detail-popup" @touchmove.stop="handlePopupTouch">
        <view class="popup-header">
          <text class="popup-title">版本详情</text>
          <uni-icons type="close" size="20" color="#999" @tap="closeVersionPopup"></uni-icons>
        </view>
        <scroll-view class="popup-content" scroll-y="true">
          <view class="detail-item">
            <text class="label">版本号：</text>
            <text class="value">v{{ currentVersion.version }}</text>
          </view>
          <view class="detail-item">
            <text class="label">更新标题：</text>
            <text class="value">{{ currentVersion.updateTitle }}</text>
          </view>
          <view class="detail-item">
            <text class="label">发布时间：</text>
            <text class="value">{{ formatTime(currentVersion.createTime) }}</text>
          </view>
          <view class="detail-item">
            <text class="label">更新类型：</text>
            <text class="value">{{ currentVersion.packageType === 1 ? 'WGT包' : 'APK包' }}({{ currentVersion.isForceUpdate === 0 ? '强制更新' : '普通更新' }})</text>
          </view>
          <view class="detail-item log-item">
            <text class="label">更新日志：</text>
            <view class="log-content">
              <text v-for="(line, idx) in formatUpdateLog(currentVersion.updateLog)" :key="idx">{{ line }}</text>
            </view>
          </view>
        </scroll-view>
        <view class="popup-footer">
          <button class="close-btn" @tap="closeVersionPopup">关闭</button>
        </view>
      </view>
    </uni-popup>

    <!-- 引入更新弹窗组件 -->
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
        { label: '操作日志量', value: 0 },
        { label: '消息发送量', value: 0 },
        { label: '任务调度量', value: 0 }
      ],
      quickActions: [
        { name: '智能鱼缸', icon: 'icon-yugang', bgColor: '#607d8b', path: '/pages/iot/fish/index' },
        { name: '行驶报表', icon: 'icon-BIshujuzhongxin', bgColor: '#27b0a5', path: '/pages/ev-drive/report/index' },
        { name: '在线用户', icon: 'icon-zaixianyonghu', bgColor: '#b08927', path: '/pages/monitor/user-online/index' },
        { name: '服务监控', icon: 'icon-zaixianyonghujiankong', bgColor: '#795548', path: '/pages/monitor/server/index' }
      ],
      // 版本列表相关
      versionList: [], // 版本列表数据
      pageNum: 1, // 当前页码
      pageSize: 5, // 每页条数（固定5条）
      total: 0, // 总记录数
      totalPage: 0, // 总页数
      loading: false, // 加载状态
      // 滑动相关
      touchStartX: 0, // 触摸起始X坐标
      touchEndX: 0, // 触摸结束X坐标
      swipeThreshold: 50, // 滑动判定阈值（像素）
      currentVersion: {}, // 当前选中的版本详情
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
    }
  },
  onLoad() {
    this.getAllLogCounts()
    // 初始化加载版本列表
    this.getVersionList()
    // 获取更新
    this.handleToUpgrade()
  },
  async onShow() {
    // 获取状态栏高度
    const systemInfo = uni.getSystemInfoSync()
    this.statusBarHeight = systemInfo.statusBarHeight
    await this.getUser()
  },
  onPullDownRefresh() {
    this.refreshData();
  },
  methods: {
    // ========== 滑动核心逻辑 ==========
    // 记录触摸起始位置
    handleTouchStart(e) {
      this.touchStartX = e.changedTouches[0].clientX;
    },
    // 处理触摸结束，判断滑动方向
    handleTouchEnd(e) {
      this.touchEndX = e.changedTouches[0].clientX;
      this.judgeSwipeDirection();
    },
    // 判断滑动方向并切换页码
    judgeSwipeDirection() {
      const diffX = this.touchEndX - this.touchStartX;

      // 左滑（下一页）：结束位置 < 起始位置，且差值超过阈值
      if (diffX < -this.swipeThreshold && this.pageNum < this.totalPage) {
        this.pageNum++;
        this.getVersionList();
      }
      // 右滑（上一页）：结束位置 > 起始位置，且差值超过阈值
      else if (diffX > this.swipeThreshold && this.pageNum > 1) {
        this.pageNum--;
        this.getVersionList();
      }
    },
    // ========== 原有逻辑调整 ==========
    // 处理弹窗触摸事件，阻止冒泡
    handlePopupTouch(e) {
      e.stopPropagation();
    },
    // 获取版本列表（重写分页逻辑）
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
          // 计算总页数
          this.totalPage = Math.ceil(this.total / this.pageSize);
        }
      } catch (error) {
        console.error('获取版本列表失败：', error);
        this.$modal.msgError('获取版本记录失败');
        this.versionList = [];
      } finally {
        this.loading = false;
        uni.stopPullDownRefresh();
      }
    },
    // 查看版本详情
    async showVersionDetail(version) {
      this.currentVersion = version;
      this.$refs.versionPopup.open();
    },
    // 格式化时间
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
    // 格式化更新日志
    formatUpdateLog(log) {
      if (!log) return ['暂无更新日志'];
      return log.split('\n').filter(line => line.trim());
    },
    // 关闭版本详情弹窗
    closeVersionPopup() {
      this.$refs.versionPopup.close();
      this.currentVersion = {};
    },
    // 检查更新
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
    // 获取统计数据
    async getAllLogCounts() {
      try {
        const [operLogRes, messageLogRes, jobLogRes,mqttRes] = await Promise.all([
          getOperLogCount(),
          getMessageLogCount(),
          getJobLogCount(),
          queryMqttTotalCount()
        ]);

        this.overviewData = [
          { label: '操作日志量', value: operLogRes.data || 0 },
          { label: 'IOT上报量', value: mqttRes.data || 0 },
          { label: '消息发送量', value: messageLogRes.data || 0 },
          { label: '任务调度量', value: jobLogRes.data || 0 }
        ];
      } catch (error) {
        console.error('获取统计数据失败：', error);
      }
    },
    // 刷新数据
    refreshData(){
      try {
        this.pageNum = 1;
        this.getVersionList();
        this.getUser();
        this.getAllLogCounts();
        this.handleToUpgrade();
        uni.stopPullDownRefresh();
        this.$modal.msgSuccess("刷新成功");
      }catch (error) {
        uni.stopPullDownRefresh();
        this.$modal.msgError("刷新失败");
      }
    },
    // 跳转到个人信息
    handleToInfo() {
      this.$tab.navigateTo('/pages/mine/info/index');
    },
    // 获取用户信息
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
    // 快捷功能跳转
    handleQuickAction(item) {
      if (item.path) {
        uni.navigateTo({
          url: item.path
        });
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.index-container {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding-bottom: 40rpx;

  .status-bar {
    background-color: #409eff;
  }

  .header {
    background-color: #409eff;
    padding: 30rpx 40rpx;
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: #fff;
    border-radius: 0 0 40rpx 40rpx;
    box-shadow: 0 4rpx 12rpx rgba(64,158,255,0.2);

    .welcome {
      .greeting {
        font-size: 36rpx;
        font-weight: 600;
        margin-bottom: 8rpx;
        display: block;
      }

      .username {
        font-size: 28rpx;
        opacity: 0.9;
      }
    }

    .avatar {
      width: 100rpx;
      height: 100rpx;
      border-radius: 50%;
      border: 4rpx solid rgba(255,255,255,0.3);
    }
  }

  .overview {
    margin: -20rpx 40rpx 0;
    padding: 30rpx;
    background-color: #fff;
    border-radius: 24rpx;
    display: flex;
    justify-content: space-between;
    box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.05);

    .overview-item {
      text-align: center;
      flex: 1;
      padding: 0 10rpx;

      .item-value {
        font-size: 36rpx;
        font-weight: 600;
        color: #2c3e50;
        margin-bottom: 8rpx;
        white-space: nowrap;
      }

      .item-label {
        font-size: 24rpx;
        color: #909399;
        white-space: nowrap;
      }
    }
  }

  .section-title {
    font-size: 32rpx;
    font-weight: 600;
    color: #2c3e50;
    margin: 40rpx 40rpx 24rpx;
  }

  .quick-actions {
    .action-grid {
      padding: 0 20rpx;
      display: flex;
      flex-wrap: wrap;

      .action-item {
        width: 25%;
        padding: 20rpx;
        text-align: center;

        .icon-box {
          width: 96rpx;
          height: 96rpx;
          margin: 0 auto 16rpx;
          border-radius: 24rpx;
          display: flex;
          align-items: center;
          justify-content: center;
          box-shadow: 0 4rpx 12rpx rgba(0,0,0,0.1);
          transition: transform 0.3s;

          &:active {
            transform: scale(0.95);
          }
        }

        .action-name {
          font-size: 26rpx;
          color: #606266;
        }
      }
    }
  }

  // 版本列表样式 - 保持原有垂直样式，仅添加滑动容器
  .version-section {
    margin: 0;

    // 页码指示器
    .page-indicator {
      padding: 10rpx 0 20rpx;
      text-align: center;
      font-size: 20rpx;
      color: #c0c4cc;
      opacity: 0.7;
      letter-spacing: 0.5rpx;
    }

    // 滑动提示
    .swipe-tip {
      text-align: center;
      font-size: 22rpx;
      color: #409eff;
      margin-top: 10rpx;
      opacity: 0.8;
    }

    // 横向滑动容器 - 仅用于捕获滑动事件，不改变内部布局
    .version-swipe-container {
      padding: 0 40rpx;

      // 固定宽度，确保滑动判定准确
      .version-swipe-content {
        width: 100%;

        // 版本列表 - 保持原有样式不变
        .version-list {
          background-color: #fff;
          border-radius: 24rpx;
          padding: 0 30rpx;
          box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.05);

          .version-item {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 30rpx 0;
            border-bottom: 2rpx solid #f0f2f5;

            &:last-child {
              border-bottom: none;
            }

            .version-content {
              flex: 1;
              margin-right: 20rpx;

              .version-title {
                font-size: 28rpx;
                color: #2c3e50;
                margin-bottom: 8rpx;
                display: block;
              }

              .version-time {
                font-size: 24rpx;
                color: #909399;
                margin-right: 16rpx;
              }
            }
          }

          .load-more {
            padding: 20rpx 0;
            text-align: center;
          }

          .no-more, .no-data {
            padding: 30rpx 0;
            text-align: center;
            font-size: 24rpx;
            color: #909399;
          }
        }
      }
    }
  }

  // 版本详情弹窗样式
  .version-detail-popup {
    width: 100%;
    min-width: 600rpx;
    max-width: 600rpx;
    background-color: #fff;
    border-radius: 16rpx;
    padding: 0;
    overflow: hidden;
    max-height: 80vh;
    display: flex;
    flex-direction: column;
    margin: 0 auto;

    .popup-header {
      flex-shrink: 0;
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 24rpx 30rpx;
      border-bottom: 2rpx solid #f0f2f5;

      .popup-title {
        font-size: 30rpx;
        font-weight: 600;
        color: #2c3e50;
      }
    }

    .popup-content {
      height: 600rpx !important;
      box-sizing: border-box;
      padding: 30rpx 40rpx 30rpx 40rpx;

      .detail-item {
        margin-bottom: 24rpx;
        display: flex;
        flex-direction: column;

        &.log-item {
          margin-bottom: 0;
        }

        .label {
          font-size: 26rpx;
          color: #606266;
          margin-bottom: 8rpx;
          font-weight: 500;
        }

        .value {
          font-size: 28rpx;
          color: #2c3e50;
        }

        .log-content {
          font-size: 26rpx;
          color: #2c3e50;
          line-height: 1.6;

          text {
            display: block;
            margin-bottom: 8rpx;
          }
        }
      }
    }

    .popup-footer {
      flex-shrink: 0;
      padding: 20rpx 40rpx;
      border-top: 2rpx solid #f0f2f5;
      background-color: #fff;
      position: relative;
      z-index: 10;

      .close-btn {
        background-color: #409eff;
        color: #fff;
        border: none;
        border-radius: 8rpx;
        font-size: 28rpx;
        padding: 12rpx 30rpx;
        width: 100%;
        min-height: 60rpx;
        box-sizing: border-box;
      }
    }
  }
}
</style>
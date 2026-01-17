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
      <image v-if="userLoaded" class="avatar" :src="user.avatar || defaultAvatar" mode="aspectFill" @tap="handleToInfo"></image>
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

    <!-- 系统公告 -->
    <view class="notice-section">
      <view class="section-title">系统公告</view>
      <view class="notice-list">
        <view class="notice-item" v-for="(item, index) in notices" :key="index" @tap="viewNotice(item)">
          <view class="notice-content">
            <text class="notice-title">{{ item.title }}</text>
            <text class="notice-time">{{ item.createTime }}</text>
          </view>
          <uni-icons type="right" size="16" color="#999"></uni-icons>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import {getUserProfile} from "@/api/system/user";
import defaultAvatar from '@/static/images/profile.jpg'
import {getMessageLogCount} from "@/api/message/monitor"
import {getJobLogCount} from "@/api/job/jobLog"
import {getOperLogCount} from "@/api/monitor/operlog"
import { queryMqttTotalCount} from '@/api/iot/fish'

export default {
  data() {
    return {
      statusBarHeight: 0,
      userLoaded: false,
      defaultAvatar,
      user: {},
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
      notices: [
        { title: '系统更新通知：新版本功能发布', createTime: '2026-01-09' },
        { title: '关于系统维护的通知', createTime: '2025-12-20' },
        { title: '重要：安全更新提醒', createTime: '2025-03-19' }
      ]
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
    this.getUser();
    this.getAllLogCounts();
  },
  async onShow() {
    // 获取状态栏高度
    const systemInfo = uni.getSystemInfoSync()
    this.statusBarHeight = systemInfo.statusBarHeight

    // 确保用户信息已加载
    if(!this.user){
      await this.getUser()
    }
  },
  onPullDownRefresh() {
    this.refreshData();
  },
  methods: {
    async getAllLogCounts() {
      try {
        // 并行发起所有请求
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

        return {
          operLogCount: operLogRes.data || 0,
          messageLogCount: messageLogRes.data || 0,
          jobLogCount: jobLogRes.data || 0
        };
      } catch (error) {
        this.operLogCount = 0;
        this.messageLogCount = 0;
        this.jobLogCount = 0;
      }
    },
    refreshData(){
      try {
        this.getUser();
        this.getAllLogCounts();
        uni.stopPullDownRefresh();
        this.$modal.msgSuccess("刷新成功")
      }catch (error) {
        uni.stopPullDownRefresh();
        this.$modal.msgError("刷新失败")
      }
    },
    handleToInfo() {
      this.$tab.navigateTo('/pages/mine/info/index')
    },
    async getUser() {
      await getUserProfile().then(response => {
        if (response && response.data) {
          this.user = response.data;
        }
      }).catch(error => {
        console.error('获取用户信息失败：', error);
      }).finally(() => {
        this.userLoaded = true;
      });
    },
    handleQuickAction(item) {
      if (item.path) {
        uni.navigateTo({
          url: item.path
        })
      }
    },
    viewNotice(notice) {
      // 查看公告详情
      uni.showToast({
        title: '查看公告：' + notice.title,
        icon: 'none'
      })
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

  .notice-section {
    margin: 0 40rpx;

    .notice-list {
      background-color: #fff;
      border-radius: 24rpx;
      padding: 0 30rpx;
      box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.05);

      .notice-item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 30rpx 0;
        border-bottom: 2rpx solid #f0f2f5;

        &:last-child {
          border-bottom: none;
        }

        .notice-content {
          flex: 1;
          margin-right: 20rpx;

          .notice-title {
            font-size: 28rpx;
            color: #2c3e50;
            margin-bottom: 8rpx;
            display: block;
          }

          .notice-time {
            font-size: 24rpx;
            color: #909399;
          }
        }
      }
    }
  }
}
</style>

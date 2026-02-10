<template>
  <view class="mine-page">
    <!-- 用户信息卡片 -->
    <view class="user-card">
      <view class="user-header" @click="handleToInfo">
        <image @click.stop="handleToAvatar" class="avatar" :src="avatarSrc" mode="aspectFill"></image>
        <view class="user-info">
          <text class="nickname">{{ user.nickName || user.username || '未登录' }}</text>
          <text class="role-tag">{{ user.groupRoleName || '普通用户' }}</text>
        </view>
        <!-- 扫一扫按钮 -->
        <view class="qr-code" @click.stop="scanQRCode">
          <uni-icons type="scan" size="20" color="#333"></uni-icons>
        </view>
        <uni-icons type="right" size="20" color="#333"></uni-icons>
      </view>
    </view>

    <!-- 第一组菜单卡片：编辑资料、修改密码 -->
    <view class="service-card" style="margin-bottom: 30rpx;">
      <view class="service-list">
        <view class="service-item" @tap="handleToEditInfo">
          <view class="left">
            <view class="service-icon">
              <uni-icons type="person" size="20" color="#409eff"></uni-icons>
            </view>
            <text class="service-name">编辑资料</text>
          </view>
          <view class="right">
            <uni-icons type="right" size="14" color="#999"></uni-icons>
          </view>
        </view>
        <view class="service-item" @tap="handleToPwd">
          <view class="left">
            <view class="service-icon">
              <uni-icons type="locked" size="20" color="#67c23a"></uni-icons>
            </view>
            <text class="service-name">修改密码</text>
          </view>
          <view class="right">
            <uni-icons type="right" size="14" color="#999"></uni-icons>
          </view>
        </view>
        <view class="service-item" @tap="handleToThirdAuth">
          <view class="left">
            <view class="service-icon">
              <uni-icons type="link" size="20" color="#fa8c16"></uni-icons>
            </view>
            <text class="service-name">三方授权</text>
          </view>
          <view class="right">
            <uni-icons type="right" size="14" color="#999"></uni-icons>
          </view>
        </view>
      </view>
    </view>

    <!-- 第二组菜单卡片：应用设置、分享APP、关于我们 -->
    <view class="service-card" style="margin-bottom: 30rpx;">
      <view class="service-list">
        <view class="service-item" @tap="handleToSetting">
          <view class="left">
            <view class="service-icon">
              <uni-icons type="gear" size="20" color="#909399"></uni-icons>
            </view>
            <text class="service-name">应用设置</text>
          </view>
          <view class="right">
            <uni-icons type="right" size="14" color="#999"></uni-icons>
          </view>
        </view>
        <view class="service-item" @tap="shareApp">
          <view class="left">
            <view class="service-icon">
              <uni-icons type="paperplane" size="20" color="#3b82f6"></uni-icons>
            </view>
            <text class="service-name">分享APP</text>
          </view>
          <view class="right">
            <uni-icons type="right" size="14" color="#999"></uni-icons>
          </view>
        </view>
        <view class="service-item" @tap="handleAbout">
          <view class="left">
            <view class="service-icon">
              <uni-icons type="info" size="20" color="#f56c6c"></uni-icons>
            </view>
            <text class="service-name">关于我们</text>
          </view>
          <view class="right">
            <uni-icons type="right" size="14" color="#999"></uni-icons>
          </view>
        </view>
      </view>
    </view>

    <!-- 退出登录区域 - 宽度占满屏幕 -->
    <view class="logout-section">
      <view class="logout-item" @tap="handleLogout">
        <text class="logout-text">退出登录</text>
      </view>
    </view>
  </view>
</template>

<script>
import defaultAvatar from '@/static/images/profile.jpg'
import { getUserProfile } from "@/api/system/user";

export default {
  name: 'mine-page',
  data() {
    return {
      user: {},
      defaultAvatar,
      avatarSrc: ''
    }
  },
  onLoad() {
    this.getUser()
    uni.$on('refreshAvatar', this.getUser)
  },
  onUnload() {
    // 页面卸载时解绑事件（关键！避免重复触发）
    uni.$off('refreshAvatar', this.getUser)
  },
  methods: {
    scanQRCode() {
      uni.scanCode({
        scanType: ['barCode', 'qrCode'],
        success: (res) => {
          // 新增：弹窗展示扫码内容
          uni.showModal({
            title: '扫码结果',
            content: `码类型：${res.scanType}\n内容：${res.result}`,
            showCancel: true,
            cancelText: '关闭',
            confirmText: '复制内容',
            success: (modalRes) => {
              if (modalRes.confirm) {
                // 点击确认按钮，复制扫码内容到剪贴板
                uni.setClipboardData({
                  data: res.result,
                  success: () => {
                    uni.showToast({
                      title: '内容已复制',
                      icon: 'success',
                      duration: 2000
                    })
                  }
                })
              }
            }
          })
        },
        fail: (err) => {
          // 区分取消扫码和扫码失败
          if (err.errMsg !== 'scanCode:fail cancel') {
            uni.showToast({
              title: '扫码失败，请重试',
              icon: 'none',
              duration: 2000
            })
          }
        }
      })
    },
    handleToThirdAuth() {
      this.$tab.navigateTo('/pages/mine/third/index')
    },
    shareApp() {
      this.$tab.navigateTo('/pages/mine/share/index')
    },
    handleToAvatar() {
      this.$tab.navigateTo('/pages/mine/avatar/index')
    },
    handleToEditInfo() {
      this.$tab.navigateTo('/pages/mine/info/edit')
    },
    handleToInfo() {
      this.$tab.navigateTo('/pages/mine/info/index')
    },
    handleToPwd() {
      this.$tab.navigateTo('/pages/mine/pwd/index')
    },
    handleToSetting() {
      this.$tab.navigateTo('/pages/mine/setting/index')
    },
    handleHelp() {
      this.$tab.navigateTo('/pages/mine/help/index')
    },
    handleAbout() {
      this.$tab.navigateTo('/pages/mine/about/index')
    },
    async getUser() {
      getUserProfile().then(response => {
        if (response && response.data) {
          this.user = response.data;
          if (this.user.avatar) {
            uni.getImageInfo({
              src: this.user.avatar,
              success: (res) => {
                this.avatarSrc = this.user.avatar;
              },
              fail: (err) => {
                this.avatarSrc = this.defaultAvatar;
              }
            });
          } else {
            this.avatarSrc = this.defaultAvatar;
          }
        }
      }).catch(error => {
        console.error('获取用户信息失败：', error);
      })
    },
    handleLogout() {
      this.$modal.confirm('确定退出系统吗？').then(() => {
        this.$store.dispatch('LogOut').then(() => {}).finally(() => {
          this.$tab.reLaunch('/pages/index')
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.mine-page {
  min-height: 100vh;
  background: #f5f6fa;
  padding: 30rpx;
}

// 用户信息卡片
.user-card {
  background: #fff;
  border-radius: 24rpx;
  padding: 40rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.04);

  .user-header {
    display: flex;
    align-items: center;

    .avatar {
      width: 120rpx;
      height: 120rpx;
      border-radius: 60rpx;
      margin-right: 30rpx;
    }

    .user-info {
      flex: 1;

      .nickname {
        font-size: 36rpx;
        font-weight: 600;
        color: #333;
        margin-bottom: 12rpx;
        display: block;
      }

      .role-tag {
        background: linear-gradient(90deg, #409eff 0%, #1890ff 100%);
        color: #fff;
        font-size: 24rpx;
        padding: 4rpx 16rpx;
        border-radius: 20rpx;
        display: inline-block;
      }
    }

    // 扫一扫按钮样式
    .qr-code {
      width: 72rpx;
      height: 72rpx;
      border-radius: 36rpx;
      background: #f5f6fa;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 20rpx;
    }
  }
}

// 菜单卡片容器
.service-card {
  background: #fff;
  border-radius: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.04);
}

// 服务菜单列表（调窄高度）
.service-list {
  padding: 10rpx 30rpx;

  .service-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    // 核心调整：将上下内边距从30rpx改为20rpx，调窄菜单高度
    padding: 20rpx 0;
    border-bottom: 2rpx solid #f5f6fa;

    &:last-child {
      border-bottom: none;
    }

    .left {
      display: flex;
      align-items: center;

      // 无背景色的图标容器
      .service-icon {
        width: 64rpx;
        height: 64rpx;
        margin-right: 24rpx;
        display: flex;
        align-items: center;
        justify-content: center;
      }

      .service-name {
        font-size: 28rpx;
        color: #333;
      }
    }

    .right {
      display: flex;
      align-items: center;

      .desc {
        font-size: 24rpx;
        color: #999;
        margin-right: 16rpx;
      }
    }
  }
}

// 退出登录样式 - 宽度占满屏幕
.logout-section {
  width: 100%;

  .logout-item {
    background-color: #fff;
    border-radius: 16rpx;
    padding: 32rpx 24rpx;
    box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.04);
    text-align: center;

    .logout-text {
      color: #f56c6c;
      font-size: 30rpx;
    }
  }
}
</style>
<template>
  <view class="mine-page">
    <!-- 用户信息卡片 -->
    <view class="profile-card">
      <view class="blob blob-1"></view>
      <view class="blob blob-2"></view>

      <view class="profile-main" @tap="handleToInfo">
        <image @tap.stop="handleToAvatar" class="avatar" :src="avatarSrc" mode="aspectFill"></image>
        <view class="profile-info">
          <text class="nickname">{{ user.nickName || user.username || '未登录' }}</text>
          <view class="profile-meta">
            <view class="role-pill">{{ user.groupRoleName || '普通用户' }}</view>
          </view>
        </view>
        <view class="scan-btn" @tap.stop="scanQRCode">
          <uni-icons type="scan" size="22" color="#ffffff"></uni-icons>
        </view>
      </view>
    </view>

    <!-- 账号与安全 -->
    <view class="section-title">账号与安全</view>
    <view class="menu-card">
      <view class="menu-item" hover-class="menu-item-hover" @tap="handleToEditInfo">
        <view class="menu-left">
          <view class="menu-icon-box icon-blue">
            <uni-icons type="person-filled" size="20" color="#ffffff"></uni-icons>
          </view>
          <text class="menu-name">编辑资料</text>
        </view>
        <uni-icons type="right" size="16" color="#c6ccd8"></uni-icons>
      </view>

      <view class="menu-item" hover-class="menu-item-hover" @tap="handleToPwd">
        <view class="menu-left">
          <view class="menu-icon-box icon-green">
            <uni-icons type="locked-filled" size="20" color="#ffffff"></uni-icons>
          </view>
          <text class="menu-name">修改密码</text>
        </view>
        <uni-icons type="right" size="16" color="#c6ccd8"></uni-icons>
      </view>

      <view class="menu-item" hover-class="menu-item-hover" @tap="handleToThirdAuth">
        <view class="menu-left">
          <view class="menu-icon-box icon-orange">
            <uni-icons type="link" size="20" color="#ffffff"></uni-icons>
          </view>
          <text class="menu-name">三方授权</text>
        </view>
        <uni-icons type="right" size="16" color="#c6ccd8"></uni-icons>
      </view>
    </view>

    <!-- 更多服务 -->
    <view class="section-title">更多服务</view>
    <view class="menu-card">
      <view class="menu-item" hover-class="menu-item-hover" @tap="handleToSetting">
        <view class="menu-left">
          <view class="menu-icon-box icon-slate">
            <uni-icons type="gear-filled" size="20" color="#ffffff"></uni-icons>
          </view>
          <text class="menu-name">应用设置</text>
        </view>
        <uni-icons type="right" size="16" color="#c6ccd8"></uni-icons>
      </view>

      <view class="menu-item" hover-class="menu-item-hover" @tap="shareApp">
        <view class="menu-left">
          <view class="menu-icon-box icon-cyan">
            <uni-icons type="paperplane-filled" size="20" color="#ffffff"></uni-icons>
          </view>
          <text class="menu-name">分享APP</text>
        </view>
        <uni-icons type="right" size="16" color="#c6ccd8"></uni-icons>
      </view>

      <view class="menu-item" hover-class="menu-item-hover" @tap="handleAbout">
        <view class="menu-left">
          <view class="menu-icon-box icon-red">
            <uni-icons type="info-filled" size="20" color="#ffffff"></uni-icons>
          </view>
          <text class="menu-name">关于我们</text>
        </view>
        <uni-icons type="right" size="16" color="#c6ccd8"></uni-icons>
      </view>
    </view>

    <!-- 退出登录 -->
    <view class="logout-btn" hover-class="logout-hover" @tap="handleLogout">
      <text>退出登录</text>
    </view>
  </view>
</template>

<script>
import defaultAvatar from '@/static/images/profile.jpg'
import { getUserProfile } from "@/api/system/user";
import {confirmQrCode, scanQrCode} from "@/api/login";

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
          const scanResult = res.result || '';
          // 判断是否包含登录ticket
          if (scanResult.includes('login?ticket=')) {
            const ticket = this.extractTicket(scanResult);
            this.showLoginConfirm(ticket);
          } else {
            // 原有逻辑：展示扫码结果
            this.$modal.confirm(`码类型：${res.scanType}\n内容：${scanResult}`, '扫码结果', {
              confirmText: '复制内容',
              showCancel: true,
              cancelText: '关闭'
            }).then(() => {
              uni.setClipboardData({
                data: scanResult,
                success: () => {
                  this.$modal.msgSuccess('内容已复制');
                }
              })
            });
          }
        },
        fail: (err) => {
          // 区分取消扫码和扫码失败
          if (err.errMsg !== 'scanCode:fail cancel') {
            this.$modal.msg('扫码失败，请重试');
          }
        }
      })
    },
    // 提取ticket参数的辅助方法
    extractTicket(url) {
      if (!url) return '';
      const regex = /ticket=([^&]*)/;
      const match = url.match(regex);
      return match ? match[1] : '';
    },
    // 展示确认登录弹窗
    showLoginConfirm(ticket) {
      const requestData = {
        "ticket": ticket
      }
      const userName = this.$store.getters.name || '当前用户';
      scanQrCode(requestData).then((res) => {
        this.$modal.confirm(`是否确认[${userName}]在PC端登录?`, "确认登录").then(() => {
          // 点击确认登录后的逻辑
          this.handleQrLogin(requestData);
        })
      });
    },
    // 处理二维码登录逻辑
    async handleQrLogin(requestData) {
      confirmQrCode(requestData).then((res) => {
        if (res.code === 200){
          this.$modal.msgSuccess("登录成功")
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

<style lang="scss">
page {
  background-color: #f4f6fb;
}
</style>

<style lang="scss" scoped>
.mine-page {
  min-height: 100vh;
  background: #f4f6fb;
  padding: 30rpx 30rpx 60rpx;
  box-sizing: border-box;
}

// 用户信息卡片
.profile-card {
  position: relative;
  overflow: hidden;
  border-radius: 32rpx;
  padding: 40rpx 32rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 14rpx 34rpx rgba(102, 126, 234, 0.28);

  .blob {
    position: absolute;
    border-radius: 50%;
    background: #ffffff;
    opacity: 0.12;
    pointer-events: none;
  }

  .blob-1 {
    width: 260rpx;
    height: 260rpx;
    top: -130rpx;
    right: -70rpx;
  }

  .blob-2 {
    width: 180rpx;
    height: 180rpx;
    bottom: -110rpx;
    left: 40rpx;
    opacity: 0.08;
  }

  .profile-main {
    position: relative;
    display: flex;
    align-items: center;
  }

  .avatar {
    width: 124rpx;
    height: 124rpx;
    border-radius: 50%;
    border: 6rpx solid rgba(255, 255, 255, 0.9);
    background: #ffffff;
    margin-right: 26rpx;
    box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.15);
  }

  .profile-info {
    flex: 1;
    min-width: 0;
  }

  .nickname {
    display: block;
    font-size: 38rpx;
    font-weight: 600;
    color: #ffffff;
    margin-bottom: 14rpx;
  }

  .profile-meta {
    display: flex;
    align-items: center;
  }

  .role-pill {
    padding: 4rpx 18rpx;
    border-radius: 20rpx;
    background: rgba(255, 255, 255, 0.2);
    border: 1rpx solid rgba(255, 255, 255, 0.32);
    color: #ffffff;
    font-size: 22rpx;
    line-height: 1.4;
  }

  .scan-btn {
    width: 72rpx;
    height: 72rpx;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.2);
    border: 1rpx solid rgba(255, 255, 255, 0.3);
    display: flex;
    align-items: center;
    justify-content: center;
    margin-left: 16rpx;
  }
}

// 分组标题
.section-title {
  margin: 40rpx 10rpx 18rpx;
  font-size: 26rpx;
  color: #8a93a6;
  letter-spacing: 1rpx;
}

// 菜单卡片
.menu-card {
  background: #ffffff;
  border-radius: 28rpx;
  overflow: hidden;
  box-shadow: 0 8rpx 30rpx rgba(23, 43, 99, 0.05);
}

.menu-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 26rpx 30rpx;
  border-bottom: 1rpx solid #f1f3f8;

  &:last-child {
    border-bottom: none;
  }

  .menu-left {
    display: flex;
    align-items: center;
  }

  .menu-icon-box {
    width: 68rpx;
    height: 68rpx;
    border-radius: 20rpx;
    margin-right: 24rpx;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .menu-name {
    font-size: 27rpx;
    color: #2b3240;
    font-weight: 400;
  }
}

.menu-item-hover {
  background: #f7f9fd;
}

.icon-blue {
  background: linear-gradient(135deg, #5b8cff, #7c5cff);
}

.icon-green {
  background: linear-gradient(135deg, #34c77b, #52d9a0);
}

.icon-orange {
  background: linear-gradient(135deg, #ff9f43, #ffbe76);
}

.icon-slate {
  background: linear-gradient(135deg, #8e9aaf, #b4bfd0);
}

.icon-cyan {
  background: linear-gradient(135deg, #22b8cf, #4dd4e8);
}

.icon-red {
  background: linear-gradient(135deg, #ff6b6b, #ff9a8b);
}

// 退出登录
.logout-btn {
  margin-top: 40rpx;
  height: 104rpx;
  border-radius: 28rpx;
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 30rpx rgba(23, 43, 99, 0.05);

  text {
    font-size: 30rpx;
    font-weight: 500;
    color: #f5522d;
  }
}

.logout-hover {
  background: #fff5f3;
}
</style>

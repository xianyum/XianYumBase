<template>
  <view class="mine-container">
    <!-- 用户信息卡片 -->
    <view class="user-card">
      <view class="user-info" @tap="handleToInfo">
        <image v-if="userLoaded" class="avatar" :src="this.user.avatar || defaultAvatar" mode="aspectFill"></image>
        <view class="info-content">
          <text class="nickname">{{ this.user.nickName || this.user.username}}</text>
          <text class="role">{{this.user.groupRoleName}}</text>
        </view>
        <uni-icons type="right" size="16" color="#fff"></uni-icons>
      </view>
    </view>

    <!-- 功能菜单 -->
    <view class="menu-list">
      <view class="menu-group">
        <view class="menu-item" @tap="handleToInfo">
          <view class="item-left">
            <uni-icons type="person" size="20" color="#409eff"></uni-icons>
            <text class="item-text">个人信息</text>
          </view>
          <uni-icons type="right" size="16" color="#999"></uni-icons>
        </view>
        <view class="menu-item" @tap="handleToPwd">
          <view class="item-left">
            <uni-icons type="locked" size="20" color="#67c23a"></uni-icons>
            <text class="item-text">修改密码</text>
          </view>
          <uni-icons type="right" size="16" color="#999"></uni-icons>
        </view>
      </view>

      <view class="menu-group">
        <view class="menu-item" @tap="handleToSetting">
          <view class="item-left">
            <uni-icons type="gear" size="20" color="#909399"></uni-icons>
            <text class="item-text">应用设置</text>
          </view>
          <uni-icons type="right" size="16" color="#999"></uni-icons>
        </view>
        <view class="menu-item" @tap="handleHelp">
          <view class="item-left">
            <uni-icons type="help" size="20" color="#e6a23c"></uni-icons>
            <text class="item-text">常见问题</text>
          </view>
          <uni-icons type="right" size="16" color="#999"></uni-icons>
        </view>
        <view class="menu-item" @tap="handleAbout">
          <view class="item-left">
            <uni-icons type="info" size="20" color="#f56c6c"></uni-icons>
            <text class="item-text">关于我们</text>
          </view>
          <uni-icons type="right" size="16" color="#999"></uni-icons>
        </view>
      </view>

      <view class="menu-group">
        <view class="menu-item logout" @tap="handleLogout">
          <text class="item-text">退出登录</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
  import defaultAvatar from '@/static/images/profile.jpg'
  import {getUserProfile} from "@/api/system/user";

  export default {
    data() {
      return {
        user: {},
        defaultAvatar,
        userLoaded: false
      }
    },
    onLoad() {
      this.getUser()
    },
    methods: {
      handleToInfo() {
        this.$tab.navigateTo('/pages/mine/info/index')
      },
      handleToPwd(){
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
          }
        }).catch(error => {
          console.error('获取用户信息失败：', error);
        }).finally(() => {
          this.userLoaded = true;
        });
      },
      handleLogout() {
        this.$modal.confirm('确定退出系统吗？').then(() => {
          this.$store.dispatch('LogOut').then(() => {}).finally(()=>{
            this.$tab.reLaunch('/pages/index')
          })
        })
      }
    }
  }
</script>

<style lang="scss" scoped>
.mine-container {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding-bottom: 40rpx;

  .user-card {
    background-color: #409eff;
    padding: 40rpx;
    margin-bottom: 20rpx;

    .user-info {
      display: flex;
      align-items: center;

      .avatar {
        width: 120rpx;
        height: 120rpx;
        border-radius: 60rpx;
        border: 4rpx solid rgba(255,255,255,0.3);
        margin-right: 24rpx;
      }

      .info-content {
        flex: 1;

        .nickname {
          font-size: 36rpx;
          color: #fff;
          font-weight: 500;
          margin-bottom: 8rpx;
          display: block;
        }

        .dept {
          font-size: 26rpx;
          color: rgba(255,255,255,0.9);
        }
      }
    }
  }

  .menu-list {
    padding: 0 20rpx;

    .menu-group {
      background-color: #fff;
      border-radius: 16rpx;
      margin-bottom: 20rpx;
      overflow: hidden;

      .menu-item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 32rpx 24rpx;
        border-bottom: 2rpx solid #f0f2f5;

        &:last-child {
          border-bottom: none;
        }

        .item-left {
          display: flex;
          align-items: center;
          gap: 16rpx;

          .item-text {
            font-size: 28rpx;
            color: #2c3e50;
          }
        }

        &.logout {
          justify-content: center;

          .item-text {
            color: #f56c6c;
            font-size: 30rpx;
          }
        }
      }
    }
  }
}
</style>

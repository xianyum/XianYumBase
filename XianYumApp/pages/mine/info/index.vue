<template>
  <view class="container">
    <view class="avatar-container">
      <image
          class="user-avatar"
          :src="avatarSrc"
          mode="aspectFill"
          @click="previewAvatar"
      ></image>
      <view class="avatar-tips">{{user.username}}</view>
    </view>

    <!-- 原有信息列表 -->
    <uni-list>
      <uni-list-item showExtraIcon="true" :extraIcon="{type: 'person-filled'}" title="用户昵称" :rightText="user.nickName" />
      <uni-list-item showExtraIcon="true" :extraIcon="{type: 'staff-filled'}" title="用户角色" :rightText="user.groupRoleName" />
      <uni-list-item showExtraIcon="true" :extraIcon="{type: 'phone-filled'}" title="手机号码" :rightText="user.mobile" />
      <uni-list-item showExtraIcon="true" :extraIcon="{type: 'email-filled'}" title="用户邮箱" :rightText="user.email" />
      <uni-list-item showExtraIcon="true" :extraIcon="{type: 'calendar-filled'}" title="注册日期" :rightText="formatTime(user.createTime)"/>
    </uni-list>
  </view>
</template>

<script>
import defaultAvatar from '@/static/images/profile.jpg'
import { getUserProfile } from "@/api/system/user"
import { formatTime } from '@/utils/dateFormat.js'

export default {
  data() {
    return {
      avatarSrc: '',
      defaultAvatar,
      user: {}
    }
  },
  onLoad() {
    this.getUser()
  },
  methods: {
    formatTime(time) {
      return formatTime(time)
    },
    // 获取用户信息
    getUser() {
      getUserProfile().then(response => {
        this.user = response.data
        if (this.user.avatar) {
          const img = new Image();
          img.src = this.user.avatar;
          // 加载完成后再赋值，避免闪图
          img.onload = () => {
            this.avatarSrc = this.user.avatar;
          };
          // 加载失败时用默认头像
          img.onerror = () => {
            this.avatarSrc = this.defaultAvatar;
          };
        } else {
          this.avatarSrc = this.defaultAvatar;
        }
      }).catch(error => {
        console.error('获取用户信息失败:', error)
        uni.showToast({ title: '获取信息失败', icon: 'none' })
      })
    },
    // 预览头像
    previewAvatar() {
      // 校验头像是否存在
      if (!this.user.avatar) {
        uni.showToast({ title: '暂无头像可预览', icon: 'none' })
        return
      }
      // 调用uni-app预览图片API
      uni.previewImage({
        urls: [this.user.avatar], // 需要预览的图片链接列表（数组形式）
        current: this.user.avatar, // 当前显示图片的链接
        longPressActions: { // 可选：长按图片的操作菜单
          itemList: ['保存图片'],
          success: (res) => {
            if (res.tapIndex === 0) {
              this.saveAvatar()
            }
          }
        }
      })
    },
    // 可选：保存头像到本地
    saveAvatar() {
      uni.showLoading({ title: '保存中...' })
      uni.downloadFile({
        url: this.user.avatar,
        success: (res) => {
          if (res.statusCode === 200) {
            uni.saveImageToPhotosAlbum({
              filePath: res.tempFilePath,
              success: () => {
                uni.hideLoading()
                uni.showToast({ title: '保存成功', icon: 'success' })
              },
              fail: () => {
                uni.hideLoading()
                uni.showToast({ title: '保存失败，请授权相册权限', icon: 'none' })
              }
            })
          }
        },
        fail: () => {
          uni.hideLoading()
          uni.showToast({ title: '下载图片失败', icon: 'none' })
        }
      })
    }
  }
}
</script>

<style lang="scss">
page {
  background-color: #ffffff;
}

.container {
  padding: 20rpx;
}

// 头像样式
.avatar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 30rpx;
  padding: 20rpx 0;
  border-bottom: 1px solid #f5f5f5;

  .user-avatar {
    width: 160rpx;
    height: 160rpx;
    border-radius: 50%; // 圆形头像
    border: 2px solid #eee;
    margin-bottom: 10rpx;
    // 点击反馈
    &:active {
      opacity: 0.8;
    }
  }

  .avatar-tips {
    font-size: 24rpx;
    color: #999;
  }
}

// 列表样式优化
.uni-list {
  --uni-list-item-padding: 15rpx 0;
}
</style>
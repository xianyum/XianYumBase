<template>
  <view class="auth-manager-page">
    <!-- 授权列表 -->
    <view class="auth-list">
      <!-- QQ 授权项 -->
      <view class="auth-item">
        <view class="auth-info">
          <view class="auth-icon qq-icon">
            <uni-icons custom-prefix="iconfont" type="icon-QQ" size="32" color="#12B7F5"></uni-icons>
          </view>
          <view class="auth-desc">
            <text class="auth-name">QQ</text>
            <text class="auth-status">{{ isQQBound ? `已绑定 (${qqNickname || 'QQ账号'})` : '未绑定' }}</text>
          </view>
        </view>
        <button
            class="auth-btn"
            :class="isQQBound ? 'unbind-btn' : 'bind-btn'"
            @click="handleQQAuth"
            :loading="loading.qq"
        >
          {{ isQQBound ? '解绑' : '绑定' }}
        </button>
      </view>

      <view class="auth-item">
        <view class="auth-info">
          <view class="auth-icon alipay-icon">
            <uni-icons custom-prefix="iconfont" type="icon-zhifubao" size="32" color="#108EE9"></uni-icons>
          </view>
          <view class="auth-desc">
            <text class="auth-name">支付宝</text>
            <text class="auth-status">{{ isAlipayBound ? `已绑定 (${alipayNickname || '支付宝账号'})` : '未绑定' }}</text>
          </view>
        </view>
        <button
            class="auth-btn"
            :class="isAlipayBound ? 'unbind-btn' : 'bind-btn'"
            @click="handleAlipayAuth"
            :loading="loading.alipay"
        >
          {{ isAlipayBound ? '解绑' : '绑定' }}
        </button>
      </view>
    </view>
  </view>
</template>

<script>
import {getCurrentUserThirdRelation} from "@/api/system/user";

export default {
  name: 'AuthManager',
  data() {
    return {
      // 绑定状态
      isQQBound: false,
      isAlipayBound: false,
      // 账号昵称
      qqNickname: '',
      alipayNickname: '',
      // 第三方账号ID（解绑时需要）
      qqOpenId: '',
      alipayOpenId: '',
      // 加载状态
      loading: {
        qq: false,
        alipay: false
      }
    }
  },
  onShow() {
    // 页面显示时获取最新绑定状态
    this.getAuthStatus()
  },
  methods: {
    getAuthStatus() {
      getCurrentUserThirdRelation().then(res => {
        if (res.code === 200) {
          this.isQQBound = false
          this.isAlipayBound = false
          this.qqNickname = ''
          this.alipayNickname = ''
          this.qqOpenId = ''
          this.alipayOpenId = ''
          const authList = res.data || []
          authList.forEach(item => {
            switch (item.thirdType) {
              case 0: // QQ
                this.isQQBound = true
                this.qqNickname = item.openUserName
                this.qqOpenId = item.openUserId
                break
              case 1: // 支付宝
                this.isAlipayBound = true
                this.alipayNickname = item.openUserName
                this.alipayOpenId = item.openUserId
                break
            }
          })
        }
        // 无论接口返回成功与否，都关闭加载提示
        this.$modal.closeLoading()
      }).catch(err => {
        this.$modal.closeLoading()
      })
    },
    /**
     * 处理 QQ 授权
     */
    handleQQAuth() {
      if (this.isQQBound) {
        this.$modal.confirm('确定要解绑QQ账号吗？').then(() => {
          this.unbindQQ()
        })
      } else {
        // 绑定逻辑
        this.bindQQ()
      }
    },

    /**
     * 处理支付宝授权
     */
    handleAlipayAuth() {
      if (this.isAlipayBound) {
        this.$modal.confirm('确定要解绑支付宝账号吗？').then(() => {
          this.unbindAlipay()
        })
      } else {
        // 绑定逻辑
        this.bindAlipay()
      }
    },

    /**
     * 绑定 QQ
     */
    bindQQ() {
      this.$modal.msg("功能正在开发中")
    },

    /**
     * 解绑 QQ
     */
    unbindQQ() {
      this.$modal.msg("功能正在开发中")
    },

    /**
     * 绑定支付宝
     */
    bindAlipay() {
      this.$modal.msg("移动端暂不支持，请去PC端操作")
    },

    /**
     * 解绑支付宝
     */
    unbindAlipay() {
      this.$modal.msg("移动端暂不支持，请去PC端操作")
    }
  }
}
</script>

<style scoped>
.auth-manager-page {
  background-color: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 20rpx;
}

.auth-list {
  margin: 20rpx;
}

.auth-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #fff;
  padding: 30rpx;
  border-radius: 12rpx;
  margin-bottom: 20rpx;
}

.auth-info {
  display: flex;
  align-items: center;
}

/* 调整图标容器样式，适配字体图标 */
.auth-icon {
  width: 80rpx;
  height: 80rpx;
  margin-right: 20rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* QQ图标背景色 */
.qq-icon {
  background-color: #E8F3FF;
}

/* 支付宝图标背景色 */
.alipay-icon {
  background-color: #E5F0FF;
}

.auth-desc {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.auth-name {
  font-size: 32rpx;
  color: #333;
  margin-bottom: 8rpx;
}

.auth-status {
  font-size: 24rpx;
  color: #999;
}

.auth-btn {
  width: 160rpx;
  height: 70rpx;
  line-height: 70rpx;
  border-radius: 35rpx;
  font-size: 28rpx;
}

.bind-btn {
  background-color: #007aff;
  color: #fff;
}

.unbind-btn {
  background-color: #fff;
  color: #ff3b30;
  border: 1rpx solid #ff3b30;
}
</style>
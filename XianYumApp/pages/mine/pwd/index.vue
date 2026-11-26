<template>
  <view class="pwd-page">
    <view class="tip-card">
      <view class="tip-icon">
        <uni-icons type="locked-filled" size="20" color="#ffffff"></uni-icons>
      </view>
      <view class="tip-text">
        <text class="tip-title">保护你的账号安全</text>
        <text class="tip-desc">建议定期更换密码，并避免与其他平台重复</text>
      </view>
    </view>

    <view class="form-card">
      <view class="field" :class="{ 'field-error': errors.oldPassword }">
        <view class="field-label">
          <text>旧密码</text>
          <text class="required">*</text>
        </view>
        <view class="field-input with-eye">
          <input
            class="input-inner"
            v-model="user.oldPassword"
            :password="!show.old"
            placeholder="请输入旧密码"
            placeholder-class="input-placeholder"
            maxlength="20"
            @input="clearError('oldPassword')"
          />
          <view class="eye-btn" @tap="show.old = !show.old">
            <uni-icons :type="show.old ? 'eye-filled' : 'eye-slash'" size="18" color="#9aa4b8"></uni-icons>
          </view>
        </view>
        <text class="field-tip" v-if="errors.oldPassword">{{ errors.oldPassword }}</text>
      </view>

      <view class="field" :class="{ 'field-error': errors.newPassword }">
        <view class="field-label">
          <text>新密码</text>
          <text class="required">*</text>
        </view>
        <view class="field-input with-eye">
          <input
            class="input-inner"
            v-model="user.newPassword"
            :password="!show.newPwd"
            placeholder="请输入新密码（6-20位字符）"
            placeholder-class="input-placeholder"
            maxlength="20"
            @input="clearError('newPassword')"
          />
          <view class="eye-btn" @tap="show.newPwd = !show.newPwd">
            <uni-icons :type="show.newPwd ? 'eye-filled' : 'eye-slash'" size="18" color="#9aa4b8"></uni-icons>
          </view>
        </view>
        <text class="field-tip" v-if="errors.newPassword">{{ errors.newPassword }}</text>
      </view>

      <view class="field" :class="{ 'field-error': errors.confirmPassword }">
        <view class="field-label">
          <text>确认密码</text>
          <text class="required">*</text>
        </view>
        <view class="field-input with-eye">
          <input
            class="input-inner"
            v-model="user.confirmPassword"
            :password="!show.confirm"
            placeholder="请再次输入新密码"
            placeholder-class="input-placeholder"
            maxlength="20"
            @input="clearError('confirmPassword')"
          />
          <view class="eye-btn" @tap="show.confirm = !show.confirm">
            <uni-icons :type="show.confirm ? 'eye-filled' : 'eye-slash'" size="18" color="#9aa4b8"></uni-icons>
          </view>
        </view>
        <text class="field-tip" v-if="errors.confirmPassword">{{ errors.confirmPassword }}</text>
      </view>
    </view>

    <view class="submit-wrap">
      <view class="submit-btn" :class="{ 'is-loading': submitting }" @tap="submit">
        {{ submitting ? '提交中...' : '确认修改' }}
      </view>
    </view>
  </view>
</template>

<script>
  import { updateUserPwd } from "@/api/system/user"

  export default {
    data() {
      return {
        user: {
          oldPassword: undefined,
          newPassword: undefined,
          confirmPassword: undefined
        },
        show: {
          old: false,
          newPwd: false,
          confirm: false
        },
        errors: {},
        submitting: false
      }
    },
    methods: {
      clearError(field) {
        if (this.errors[field]) {
          this.errors[field] = ''
        }
      },
      validate() {
        const errors = {}
        const oldPassword = this.user.oldPassword || ''
        const newPassword = this.user.newPassword || ''
        const confirmPassword = this.user.confirmPassword || ''
        if (!oldPassword) {
          errors.oldPassword = '旧密码不能为空'
        }
        if (!newPassword) {
          errors.newPassword = '新密码不能为空'
        } else if (newPassword.length < 6 || newPassword.length > 20) {
          errors.newPassword = '长度在 6 到 20 个字符'
        }
        if (!confirmPassword) {
          errors.confirmPassword = '确认密码不能为空'
        } else if (newPassword !== confirmPassword) {
          errors.confirmPassword = '两次输入的密码不一致'
        }
        this.errors = errors
        return Object.keys(errors).length === 0
      },
      submit() {
        if (this.submitting) return
        if (!this.validate()) return
        this.submitting = true
        updateUserPwd(this.user.oldPassword, this.user.newPassword).then(response => {
          this.$modal.msgSuccess("修改成功")
        }).catch(() => {}).finally(() => {
          this.submitting = false
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
  .pwd-page {
    min-height: 100vh;
    background: #f4f6fb;
    padding: 30rpx 30rpx 60rpx;
    box-sizing: border-box;
  }

  .tip-card {
    display: flex;
    align-items: center;
    background: #ffffff;
    border-radius: 28rpx;
    padding: 30rpx 32rpx;
    margin-bottom: 28rpx;
    box-shadow: 0 8rpx 30rpx rgba(23, 43, 99, 0.05);

    .tip-icon {
      width: 72rpx;
      height: 72rpx;
      border-radius: 20rpx;
      background: linear-gradient(135deg, #4f7cff, #7c5cff);
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 24rpx;
      flex-shrink: 0;
    }

    .tip-text {
      flex: 1;
      min-width: 0;
    }

    .tip-title {
      display: block;
      font-size: 28rpx;
      font-weight: 600;
      color: #1f2430;
      margin-bottom: 6rpx;
    }

    .tip-desc {
      display: block;
      font-size: 23rpx;
      color: #9aa4b8;
      line-height: 1.5;
    }
  }

  .form-card {
    background: #ffffff;
    border-radius: 28rpx;
    padding: 36rpx 32rpx;
    box-shadow: 0 8rpx 30rpx rgba(23, 43, 99, 0.05);
  }

  .field {
    margin-bottom: 28rpx;

    &:last-child {
      margin-bottom: 0;
    }

    .field-label {
      display: flex;
      align-items: center;
      font-size: 26rpx;
      color: #6b7488;
      margin-bottom: 14rpx;
    }

    .required {
      color: #ff5a5f;
      font-size: 24rpx;
      margin-left: 6rpx;
    }

    .field-input {
      display: flex;
      align-items: center;
      height: 92rpx;
      padding: 0 26rpx;
      background: #f5f7fc;
      border: 2rpx solid transparent;
      border-radius: 20rpx;
      transition: border-color 0.2s, background-color 0.2s;
    }

    .field-input.with-eye {
      padding-right: 8rpx;
    }

    .input-inner {
      flex: 1;
      height: 100%;
      font-size: 30rpx;
      color: #1f2430;
      background: transparent;
    }

    .eye-btn {
      width: 64rpx;
      height: 64rpx;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .field-tip {
      display: block;
      margin-top: 10rpx;
      font-size: 23rpx;
      color: #ff5a5f;
    }
  }

  .field-error {
    .field-input {
      background: #fff6f6;
      border-color: #ffb3b6;
    }
  }

  .input-placeholder {
    color: #b6bdcb;
    font-size: 28rpx;
  }

  .submit-wrap {
    margin-top: 50rpx;
  }

  .submit-btn {
    height: 96rpx;
    border-radius: 48rpx;
    background: linear-gradient(135deg, #4f7cff, #7c5cff);
    color: #ffffff;
    font-size: 31rpx;
    font-weight: 600;
    letter-spacing: 2rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 14rpx 30rpx rgba(79, 124, 255, 0.3);
    transition: opacity 0.2s;

    &:active {
      opacity: 0.85;
    }

    &.is-loading {
      opacity: 0.65;
    }
  }
</style>

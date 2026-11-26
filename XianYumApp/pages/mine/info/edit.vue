<template>
  <view class="edit-page">
    <view class="form-card">
      <view class="card-head">
        <view class="card-title-wrap">
          <view class="title-bar"></view>
          <text class="card-title">基本资料</text>
        </view>
        <text class="card-desc">完善资料，让你的账号更完整</text>
      </view>

      <view class="field" :class="{ 'field-error': errors.nickName }">
        <view class="field-label">
          <text>用户昵称</text>
          <text class="required">*</text>
        </view>
        <view class="field-input">
          <input
            class="input-inner"
            v-model="user.nickName"
            placeholder="请输入昵称"
            placeholder-class="input-placeholder"
            maxlength="30"
            @input="clearError('nickName')"
          />
        </view>
        <text class="field-tip" v-if="errors.nickName">{{ errors.nickName }}</text>
      </view>

      <view class="field" :class="{ 'field-error': errors.mobile }">
        <view class="field-label">
          <text>手机号码</text>
          <text class="required">*</text>
        </view>
        <view class="field-input">
          <input
            class="input-inner"
            v-model="user.mobile"
            type="number"
            placeholder="请输入手机号码"
            placeholder-class="input-placeholder"
            maxlength="11"
            @input="clearError('mobile')"
          />
        </view>
        <text class="field-tip" v-if="errors.mobile">{{ errors.mobile }}</text>
      </view>

      <view class="field" :class="{ 'field-error': errors.email }">
        <view class="field-label">
          <text>邮箱</text>
          <text class="required">*</text>
        </view>
        <view class="field-input">
          <input
            class="input-inner"
            v-model="user.email"
            placeholder="请输入邮箱"
            placeholder-class="input-placeholder"
            maxlength="50"
            @input="clearError('email')"
          />
        </view>
        <text class="field-tip" v-if="errors.email">{{ errors.email }}</text>
      </view>

      <view class="field">
        <view class="field-label">
          <text>性别</text>
        </view>
        <view class="sex-group">
          <view
            class="sex-chip"
            :class="{ active: isSex(0) }"
            @tap="selectSex(0)"
          >
            <uni-icons :type="isSex(0) ? 'checkmarkempty' : 'person'" size="14" :color="isSex(0) ? '#ffffff' : '#7a8499'"></uni-icons>
            <text>男</text>
          </view>
          <view
            class="sex-chip"
            :class="{ active: isSex(1) }"
            @tap="selectSex(1)"
          >
            <uni-icons :type="isSex(1) ? 'checkmarkempty' : 'person'" size="14" :color="isSex(1) ? '#ffffff' : '#7a8499'"></uni-icons>
            <text>女</text>
          </view>
        </view>
      </view>
    </view>

    <view class="submit-wrap">
      <view class="submit-btn" :class="{ 'is-loading': submitting }" @tap="submit">
        {{ submitting ? '保存中...' : '保存修改' }}
      </view>
    </view>
  </view>
</template>

<script>
  import { getUserProfile } from "@/api/system/user"
  import { updateUserProfile } from "@/api/system/user"

  export default {
    data() {
      return {
        user: {
          nickName: "",
          mobile: "",
          email: "",
          sex: null
        },
        errors: {},
        submitting: false
      }
    },
    onLoad() {
      this.getUser()
    },
    methods: {
      getUser() {
        getUserProfile().then(response => {
          this.user = response.data
        })
      },
      isSex(value) {
        return String(this.user.sex) === String(value)
      },
      selectSex(value) {
        this.user.sex = value
      },
      clearError(field) {
        if (this.errors[field]) {
          this.errors[field] = ''
        }
      },
      validate() {
        const errors = {}
        if (!(this.user.nickName || '').trim()) {
          errors.nickName = '用户昵称不能为空'
        }
        const mobile = (this.user.mobile || '').trim()
        if (!mobile) {
          errors.mobile = '手机号码不能为空'
        } else if (!/^1[3-9]\d{9}$/.test(mobile)) {
          errors.mobile = '请输入正确的手机号码'
        }
        const email = (this.user.email || '').trim()
        if (!email) {
          errors.email = '邮箱地址不能为空'
        } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
          errors.email = '请输入正确的邮箱地址'
        }
        this.errors = errors
        return Object.keys(errors).length === 0
      },
      submit() {
        if (this.submitting) return
        if (!this.validate()) return
        const submitData = {
          nickName: (this.user.nickName || '').trim(),
          mobile: (this.user.mobile || '').trim(),
          email: (this.user.email || '').trim(),
          sex: this.user.sex
        }
        this.submitting = true
        updateUserProfile(submitData).then(response => {
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
  .edit-page {
    min-height: 100vh;
    background: #f4f6fb;
    padding: 30rpx 30rpx 60rpx;
    box-sizing: border-box;
  }

  .form-card {
    background: #ffffff;
    border-radius: 28rpx;
    padding: 36rpx 32rpx;
    box-shadow: 0 8rpx 30rpx rgba(23, 43, 99, 0.05);

    & + .form-card {
      margin-top: 28rpx;
    }
  }

  .card-head {
    margin-bottom: 32rpx;

    .card-title-wrap {
      display: flex;
      align-items: center;
    }

    .title-bar {
      width: 8rpx;
      height: 30rpx;
      border-radius: 4rpx;
      background: linear-gradient(180deg, #667eea, #764ba2);
      margin-right: 14rpx;
    }

    .card-title {
      font-size: 34rpx;
      font-weight: 600;
      color: #1f2430;
      letter-spacing: 1rpx;
    }

    .card-desc {
      display: block;
      margin-top: 10rpx;
      padding-left: 22rpx;
      font-size: 24rpx;
      color: #9aa4b8;
    }
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

    .input-inner {
      flex: 1;
      height: 100%;
      font-size: 30rpx;
      color: #1f2430;
      background: transparent;
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

  .sex-group {
    display: flex;
  }

  .sex-chip {
    min-width: 132rpx;
    height: 64rpx;
    padding: 0 28rpx;
    box-sizing: border-box;
    border-radius: 32rpx;
    background: #f5f7fc;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 26rpx;
    color: #5b6478;
    margin-right: 20rpx;
    transition: all 0.2s;

    text {
      margin-left: 6rpx;
    }

    &.active {
      background: linear-gradient(135deg, #667eea, #764ba2);
      color: #ffffff;
      box-shadow: 0 8rpx 18rpx rgba(102, 126, 234, 0.26);
    }
  }

  .submit-wrap {
    margin-top: 50rpx;
  }

  .submit-btn {
    height: 96rpx;
    border-radius: 48rpx;
    background: linear-gradient(135deg, #667eea, #764ba2);
    color: #ffffff;
    font-size: 31rpx;
    font-weight: 600;
    letter-spacing: 2rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 14rpx 30rpx rgba(102, 126, 234, 0.3);
    transition: opacity 0.2s;

    &:active {
      opacity: 0.85;
    }

    &.is-loading {
      opacity: 0.65;
    }
  }
</style>

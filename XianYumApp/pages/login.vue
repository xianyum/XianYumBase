<template>
  <view class="content">
    <view class="login-container">
      <view class="logo-section">
        <view class="logo-avatar">
        </view>
        <text class="logo-text">欢迎回来</text>
        <text class="logo-subtitle">登录您的账号</text>
      </view>

      <view class="login-tabs">
        <view
            class="tab-item"
            :class="{'active': loginType === 'password'}"
            @tap="switchLoginType('password')"
        >
          密码登录
        </view>
        <!--        <view-->
        <!--            class="tab-item"-->
        <!--            :class="{'active': loginType === 'phone'}"-->
        <!--            @tap="switchLoginType('phone')"-->
        <!--        >-->
        <!--          手机登录-->
        <!--        </view>-->
        <view
            class="tab-item"
            :class="{'active': loginType === 'email'}"
            @tap="switchLoginType('email')"
        >
          邮箱登录
        </view>
      </view>

      <view class="login-form">
        <!-- 密码登录 -->
        <view v-if="loginType === 'password'" class="form-group">
          <view class="form-item">
            <view class="input-icon">
              <text class="cuIcon-people"></text>
            </view>
            <input
                class="modern-input"
                type="text"
                placeholder="账号"
                v-model="passwordForm.username"
            />
          </view>
          <view class="form-item">
            <view class="input-icon">
              <text class="cuIcon-lock"></text>
            </view>
            <input
                class="modern-input"
                type="password"
                placeholder="请输入密码"
                v-model="passwordForm.password"
            />
          </view>
        </view>

        <!-- 手机验证码登录 -->
        <view v-if="loginType === 'phone'" class="form-group">
          <view class="form-item">
            <view class="input-icon">
              <text class="cuIcon-mobile"></text>
            </view>
            <input
                class="modern-input"
                type="number"
                placeholder="请输入手机号"
                v-model="phoneForm.phone"
                maxlength="11"
            />
          </view>
          <view class="form-item code-form-item">
            <view class="input-icon">
              <text class="cuIcon-notice"></text>
            </view>
            <input
                class="modern-input code-input"
                type="number"
                placeholder="验证码"
                v-model="phoneForm.code"
                maxlength="6"
            />
            <button
                class="code-btn"
                :class="{'disabled': countdown > 0}"
                :disabled="countdown > 0"
                @tap="sendPhoneCode"
            >
              {{ countdown > 0 ? countdown + 's' : '获取验证码' }}
            </button>
          </view>
        </view>

        <view v-if="loginType === 'email'" class="form-group">
          <view class="form-item">
            <view class="input-icon">
              <text class="cuIcon-mail"></text>
            </view>
            <input
                class="modern-input"
                type="text"
                placeholder="请输入邮箱"
                v-model="emailForm.email"
            />
          </view>
          <view class="form-item code-form-item">
            <view class="input-icon">
              <text class="cuIcon-notice"></text>
            </view>
            <input
                class="modern-input code-input"
                type="text"
                placeholder="验证码"
                v-model="emailForm.code"
            />
            <button
                class="code-btn"
                :class="{'disabled': emailCountdown > 0}"
                :disabled="emailCountdown > 0"
                @tap="sendEmailCode"
            >
              {{ emailCountdown > 0 ? emailCountdown + 's' : '获取验证码' }}
            </button>
          </view>
        </view>
      </view>

      <!-- 操作按钮（精简间距） -->
      <view class="action-section">
        <!-- 协议勾选 -->
        <view class="agreement-section">
          <view class="agreement-check" @tap="agree = !agree">
            <text class="checkbox-icon" :class="{'checked': agree}">
              <text class="cuIcon-roundcheck" v-if="agree"></text>
              <text class="cuIcon-round" v-else></text>
            </text>
            <text class="agreement-text">我已阅读并同意</text>
            <text class="agreement-link" @tap.stop="handleUserAgreement">《用户协议》</text>
            <text class="agreement-text">和</text>
            <text class="agreement-link" @tap.stop="handlePrivacy">《隐私政策》</text>
          </view>
        </view>

        <button class="login-btn" @tap="handleLogin">
          登录
        </button>

        <view class="link-section">
          <text class="link-text" @tap="goToForgotPassword">忘记密码？</text>
        </view>
      </view>

      <!-- 第三方登录（压缩高度和间距） -->
      <view class="third-party-section">
        <view class="divider">
          <text class="divider-text">其他登录方式</text>
        </view>
        <view class="third-party-icons">
          <view class="third-party-item qq" @tap="handleWeixinLogin">
            <text class="cuIcon-weixin"></text>
          </view>
        </view>
      </view>
    </view>

    <verify-code
        ref="verifyCode"
        :type="verifyType"
        :conf="verifyConf"
        @success="onVerifySuccess"
    ></verify-code>

  </view>
</template>

<script>
import {getCaptchaType} from '@/api/login'
import {getToken} from '@/utils/auth'
import verifyCode from "@/uni_modules/tianai-mini-captcha/components/tianai-mini-captcha";

export default {
  components: {verifyCode},
  data() {
    return {
      verifyType: 'SLIDER',
      verifyConf: {
        // 生成验证码接口
        gen: getApp().globalData.config.baseUrl + '/captcha/genCaptcha',
        // 验证接口
        validate: getApp().globalData.config.baseUrl + '/captcha/check'
      },
      codeUrl: "",
      captchaEnabled: true,
      loginType: 'password', // password, phone, email
      showPassword: false,
      countdown: 0,
      // 记住密码开关
      rememberPwd: true,
      emailCountdown: 0,
      timer: null,
      emailTimer: null,
      agree: false,
      passwordForm: {
        username: '',
        password: '',
        verifyCode: ""
      },
      phoneForm: {
        phone: '',
        code: ''
      },
      emailForm: {
        email: '',
        code: ''
      }
    }
  },
  created() {
    // 页面创建时读取缓存，自动填充账号密码
    this.loadRememberedInfo()
  },
  onLoad() {
    if (getToken()) {
      this.$tab.reLaunch('/pages/index')
    }
  },
  onUnload() {
    this.clearTimer();
    this.clearEmailTimer();
  },
  onShow() {
    this.getCaptchaType();
  },
  methods: {
    // 验证成功回调
    onVerifySuccess(verifyCode) {
      if (this.loginType === 'password') {
        this.loginUsernameAndPassword(verifyCode);
      } else if (this.loginType === 'phone') {

      } else if (this.loginType === 'email') {

      }
    },
    loginUsernameAndPassword(verifyCode) {
      this.passwordForm.verifyCode = verifyCode
      this.$store.dispatch('Login', this.passwordForm).then(() => {
        this.saveRememberedInfo()
        this.loginSuccess()
      })
    },
    // 登录成功后，处理函数
    loginSuccess(result) {
      // 设置用户信息
      this.$store.dispatch('GetInfo').then(res => {
        this.$tab.reLaunch('/pages/index')
      })
    },
    switchLoginType(type) {
      this.loginType = type;
    },
    togglePassword() {
      this.showPassword = !this.showPassword;
    },
    // 新增：保存账号密码到缓存
    saveRememberedInfo() {
      try {
        if (this.rememberPwd) {
          uni.setStorageSync('loginInfo', {
            username: this.passwordForm.username,
            password: this.passwordForm.password
          })
        } else {
          // 不记住则清除缓存
          uni.removeStorageSync('loginInfo')
        }
      } catch (e) {
        console.error('保存登录缓存失败：', e)
      }
    },
    loadRememberedInfo() {
      try {
        const loginCache = uni.getStorageSync('loginInfo') || {}
        if (loginCache.username) {
          this.passwordForm.username = loginCache.username
          this.passwordForm.password = loginCache.password ? loginCache.password : ""
          this.rememberPwd = true
        }
      } catch (e) {
        console.error('读取登录缓存失败：', e)
      }
    },
    sendPhoneCode() {
      if (!this.phoneForm.phone) {
        uni.showToast({
          title: '请输入手机号',
          icon: 'none'
        });
        return;
      }
      if (!/^1[3-9]\d{9}$/.test(this.phoneForm.phone)) {
        uni.showToast({
          title: '手机号格式不正确',
          icon: 'none'
        });
        return;
      }
      uni.showToast({
        title: '验证码已发送',
        icon: 'success'
      });
      this.startCountdown();
    },
    sendEmailCode() {
      if (!this.emailForm.email) {
        uni.showToast({
          title: '请输入邮箱',
          icon: 'none'
        });
        return;
      }
      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.emailForm.email)) {
        uni.showToast({
          title: '邮箱格式不正确',
          icon: 'none'
        });
        return;
      }
      uni.showToast({
        title: '验证码已发送',
        icon: 'success'
      });
      this.startEmailCountdown();
    },
    startCountdown() {
      this.countdown = 60;
      this.timer = setInterval(() => {
        this.countdown--;
        if (this.countdown <= 0) {
          this.clearTimer();
        }
      }, 1000);
    },
    clearTimer() {
      if (this.timer) {
        clearInterval(this.timer);
        this.timer = null;
      }
    },
    startEmailCountdown() {
      this.emailCountdown = 60;
      this.emailTimer = setInterval(() => {
        this.emailCountdown--;
        if (this.emailCountdown <= 0) {
          this.clearEmailTimer();
        }
      }, 1000);
    },
    clearEmailTimer() {
      if (this.emailTimer) {
        clearInterval(this.emailTimer);
        this.emailTimer = null;
      }
    },
    handleLogin() {
      // 检查协议勾选
      if (!this.agree) {
        uni.showToast({
          title: '请先阅读并同意用户协议和隐私政策',
          icon: 'none'
        });
        return;
      }
      if (this.loginType === 'password') {
        if (!this.passwordForm.username || !this.passwordForm.password) {
          uni.showToast({
            title: '请填写完整信息',
            icon: 'none'
          });
          return;
        }
        this.showVerify()
      } else if (this.loginType === 'phone') {
        if (!this.phoneForm.phone || !this.phoneForm.code) {
          uni.showToast({
            title: '请填写完整信息',
            icon: 'none'
          });
          return;
        }
      } else if (this.loginType === 'email') {
        if (!this.emailForm.email || !this.emailForm.code) {
          uni.showToast({
            title: '请填写完整信息',
            icon: 'none'
          });
          return;
        }
      }
      this.$modal.closeLoading()
    },
    // 打开验证码
    showVerify() {
      this.$refs.verifyCode.open()
    },
    getCaptchaType() {
      getCaptchaType().then(res => {
        this.verifyType = res.msg
      })
    },
    handleWeixinLogin() {
      uni.showToast({
        title: '微信登录功能开发中',
        icon: 'none'
      });
    },
    goToForgotPassword() {
      this.$modal.msg("请联系管理员找回")
    },
    // 隐私协议
    handlePrivacy() {
      this.$tab.navigateTo(`/pages/common/protocol/index?type=privacy`)
    },
    // 用户协议
    handleUserAgreement() {
      this.$tab.navigateTo(`/pages/common/protocol/index?type=user`)
    }
  }
}
</script>

<style scoped>

.content {
  min-height: 100vh;
  background: linear-gradient(180deg, #f5f7fa 0%, #e8ecf1 100%);
  background-attachment: fixed;
  position: relative;
  overflow: hidden;
  padding-top: 25rpx; /* 顶部少量内边距，避免贴边 */
}

.content::before, .content::after {
  content: '';
  position: absolute;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(102, 126, 234, 0.03) 0%, transparent 70%);
  z-index: 0;
}

.content::before {
  top: -40%;
  right: -20%;
  width: 500rpx;
  height: 500rpx;
}

.content::after {
  bottom: -20%;
  left: -10%;
  width: 400rpx;
  height: 400rpx;
  background: radial-gradient(circle, rgba(118, 75, 162, 0.02) 0%, transparent 70%);
}

/* 主容器：精简内边距，保证内容紧凑 */
.login-container {
  padding: 40rpx 40rpx 30rpx;
  position: relative;
  z-index: 1;
  box-sizing: border-box;
}

/* Logo区域：压缩所有间距，缩小头像外框 */
.logo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30rpx 0 40rpx; /* 大幅压缩上下内边距 */
}

.logo-avatar {
  width: 130rpx;
  height: 130rpx;
  /* 1. 合并背景图+渐变，用逗号分隔（图片在上、渐变在下） */
  /* 2. 修正App端路径：去掉@，直接以/static开头（全端通用正确路径） */
  background: url('/static/logo.png') no-repeat center / 100% 100%,
  linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 35%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20rpx;
  box-shadow: 0 15rpx 40rpx rgba(102, 126, 234, 0.3);
  border: 3rpx solid #ffffff;
}

.logo-avatar text {
  font-size: 65rpx;
  color: #ffffff;
}

.logo-text {
  font-size: 40rpx;
  font-weight: 700;
  color: #333333;
  letter-spacing: 2rpx;
  margin-bottom: 8rpx;
}

.logo-subtitle {
  font-size: 24rpx;
  color: #666666;
  letter-spacing: 1rpx;
}

/* 登录标签：精简内边距和高度 */
.login-tabs {
  display: flex;
  background: #ffffff;
  border-radius: 30rpx;
  padding: 6rpx; /* 压缩内边距 */
  margin-bottom: 30rpx; /* 压缩底部间距 */
  box-shadow: 0 6rpx 20rpx rgba(0, 0, 0, 0.08);
  border: 2rpx solid #e8ecf1;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 18rpx 0; /* 压缩上下内边距 */
  border-radius: 24rpx;
  color: #999999;
  font-size: 26rpx;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  font-weight: 500;
}

.tab-item.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
  font-weight: 600;
  box-shadow: 0 6rpx 18rpx rgba(102, 126, 234, 0.3);
  transform: translateY(-2rpx);
}

/* 表单区域：精简内边距、项间距，优化输入框高度 */
.login-form {
  background: #ffffff;
  border-radius: 40rpx;
  padding: 30rpx 30rpx; /* 压缩内边距 */
  margin-bottom: 30rpx; /* 压缩底部间距 */
  box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.08);
  border: 2rpx solid #e8ecf1;
  box-sizing: border-box;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 20rpx; /* 压缩表单项间距 */
}

.form-item {
  position: relative;
  display: flex;
  align-items: center;
  background: #f8f9fc;
  border-radius: 24rpx;
  padding: 0 25rpx; /* 压缩左右内边距 */
  transition: all 0.3s;
  border: 2rpx solid transparent;
  height: 92rpx; /* 固定输入框高度，压缩高度 */
}

/* 验证码项单独样式，避免按钮挤压 */
.code-form-item {
  height: auto;
  padding: 14rpx 25rpx;
}

.form-item:focus-within {
  background: #ffffff;
  border-color: #667eea;
  box-shadow: 0 0 0 6rpx rgba(102, 126, 234, 0.08);
}

.input-icon {
  width: 40rpx; /* 缩小图标容器 */
  display: flex;
  align-items: center;
  justify-content: center;
}

.input-icon text {
  font-size: 32rpx; /* 缩小图标 */
  color: #667eea;
}

.modern-input {
  flex: 1;
  height: 100%; /* 自适应父容器高度 */
  font-size: 28rpx; /* 微调字体大小 */
  color: #333333;
  border: none;
  outline: none;
}

.modern-input::placeholder {
  color: #a0a0a0;
}

.code-input {
  flex: 1;
  padding-right: 15rpx; /* 压缩右侧间距 */
}

.code-btn {
  padding: 0 25rpx; /* 压缩按钮内边距 */
  height: 56rpx; /* 压缩按钮高度 */
  line-height: 56rpx;
  font-size: 22rpx; /* 微调按钮字体 */
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
  border-radius: 16rpx;
  border: none;
  transition: all 0.3s;
  margin-left: 8rpx; /* 压缩左侧间距 */
  box-shadow: 0 4rpx 15rpx rgba(102, 126, 234, 0.25);
}

.code-btn:active:not(.disabled) {
  transform: scale(0.95);
  opacity: 0.9;
}

.code-btn.disabled {
  background: #e0e0e0;
  color: #a0a0a0;
  box-shadow: none;
}

/* 操作区域：精简所有间距 */
.action-section {
  margin-bottom: 30rpx; /* 压缩底部间距 */
}

.agreement-section {
  margin-bottom: 20rpx; /* 压缩协议底部间距 */
}

.agreement-check {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  font-size: 24rpx; /* 微调协议字体 */
}

.checkbox-icon {
  font-size: 36rpx; /* 缩小复选框 */
  margin-right: 8rpx; /* 压缩右侧间距 */
  color: #d0d0d0;
  transition: all 0.3s;
}

.checkbox-icon.checked {
  color: #667eea;
}

.checkbox-icon text {
  font-size: 36rpx;
}

.agreement-text {
  color: #666666;
}

.agreement-link {
  color: #667eea;
  font-weight: 600;
  margin: 0 4rpx; /* 压缩链接间距 */
}

/* 登录按钮：微调高度，精简阴影 */
.login-btn {
  width: 100%;
  height: 88rpx; /* 压缩按钮高度 */
  line-height: 88rpx;
  font-size: 32rpx; /* 微调字体 */
  font-weight: 600;
  border-radius: 30rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
  border: none;
  box-shadow: 0 10rpx 30rpx rgba(102, 126, 234, 0.4);
  transition: all 0.3s;
}

.login-btn:active {
  transform: translateY(2rpx);
  box-shadow: 0 6rpx 20rpx rgba(102, 126, 234, 0.3);
}

.link-section {
  display: flex;
  justify-content: space-between;
  margin-top: 20rpx; /* 压缩忘记密码顶部间距 */
  padding: 0 10rpx;
}

.link-text {
  color: #667eea;
  font-size: 26rpx; /* 微调字体 */
  font-weight: 600;
}

/* 第三方登录：大幅压缩间距和图标大小 */
.third-party-section {
  padding: 20rpx 0 10rpx; /* 压缩上下内边距 */
}

.divider {
  text-align: center;
  position: relative;
  margin-bottom: 30rpx; /* 压缩分割线底部间距 */
  height: 20rpx; /* 固定分割线高度 */
  line-height: 20rpx;
}

.divider::before, .divider::after {
  content: '';
  position: absolute;
  top: 50%;
  width: 30%; /* 缩短分割线长度 */
  height: 1rpx;
  background: #e8ecf1;
}

.divider::before {
  left: 0;
}

.divider::after {
  right: 0;
}

.divider-text {
  color: #999999;
  font-size: 22rpx; /* 微调字体 */
  padding: 0 15rpx; /* 压缩左右内边距 */
  letter-spacing: 1rpx;
  background: transparent;
}

.third-party-icons {
  display: flex;
  justify-content: center;
}

.third-party-item {
  width: 100rpx; /* 缩小第三方图标 */
  height: 100rpx;
  background: #ffffff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
  box-shadow: 0 6rpx 20rpx rgba(0, 0, 0, 0.1);
  border: 3rpx solid #ffffff;
}

.third-party-item:active {
  transform: scale(0.95);
  box-shadow: 0 3rpx 15rpx rgba(0, 0, 0, 0.15);
}

.third-party-item text {
  font-size: 56rpx; /* 缩小图标字体 */
}

.third-party-item.qq text {
  color: #1dd1a1;
}
</style>
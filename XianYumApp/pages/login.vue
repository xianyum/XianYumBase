<template>
  <view class="page">
    <!-- 背景光晕 -->
    <view class="aurora aurora-1"></view>
    <view class="aurora aurora-2"></view>
    <view class="aurora aurora-3"></view>

    <view class="status-bar"></view>

    <view class="content">
      <!-- 品牌标识 -->
      <view class="brand">
        <view class="brand-logo"></view>
        <text class="brand-name">XianYum</text>
      </view>

      <!-- 主标题 -->
      <view class="hero">
        <text class="hero-title">欢迎回来</text>
        <text class="hero-subtitle">很高兴再次见到你</text>
      </view>

      <!-- 登录方式切换 -->
      <view class="segmented">
        <view
            class="segmented-item"
            :class="{'is-active': loginType === 'password'}"
            @tap="switchLoginType('password')"
        >
          密码登录
        </view>
        <!--        <view-->
        <!--            class="segmented-item"-->
        <!--            :class="{'is-active': loginType === 'phone'}"-->
        <!--            @tap="switchLoginType('phone')"-->
        <!--        >-->
        <!--          手机登录-->
        <!--        </view>-->
        <view
            class="segmented-item"
            :class="{'is-active': loginType === 'email'}"
            @tap="switchLoginType('email')"
        >
          邮箱登录
        </view>
      </view>

      <!-- 表单卡片 -->
      <view class="card">
        <!-- 密码登录 -->
        <view v-if="loginType === 'password'" class="fields">
          <view class="field">
            <text class="cuIcon-people field-icon"></text>
            <input
                class="field-input"
                type="text"
                placeholder="账号 / 手机号 / 邮箱"
                placeholder-class="field-placeholder"
                v-model="passwordForm.username"
            />
          </view>

          <view class="field">
            <text class="cuIcon-lock field-icon"></text>
            <input
                class="field-input"
                type="text"
                :password="!showPassword"
                placeholder="请输入密码"
                placeholder-class="field-placeholder"
                v-model="passwordForm.password"
                confirm-type="done"
            />
            <view class="eye-toggle" @tap="togglePassword">
              <text :class="showPassword ? 'cuIcon-attentionfill' : 'cuIcon-attention'"></text>
            </view>
          </view>
        </view>

        <!-- 手机验证码登录 -->
        <view v-if="loginType === 'phone'" class="fields">
          <view class="field">
            <text class="cuIcon-mobile field-icon"></text>
            <input
                class="field-input"
                type="number"
                placeholder="请输入手机号"
                placeholder-class="field-placeholder"
                v-model="phoneForm.phone"
                maxlength="11"
            />
          </view>
          <view class="field">
            <text class="cuIcon-notice field-icon"></text>
            <input
                class="field-input"
                type="number"
                placeholder="验证码"
                placeholder-class="field-placeholder"
                v-model="phoneForm.code"
                maxlength="6"
            />
            <button
                class="code-btn"
                :class="{'is-disabled': countdown > 0}"
                :disabled="countdown > 0"
                @tap="sendPhoneCode"
            >
              {{ countdown > 0 ? countdown + 's' : '获取验证码' }}
            </button>
          </view>
        </view>

        <!-- 邮箱验证码登录 -->
        <view v-if="loginType === 'email'" class="fields">
          <view class="field">
            <text class="cuIcon-mail field-icon"></text>
            <input
                class="field-input"
                type="text"
                placeholder="请输入邮箱"
                placeholder-class="field-placeholder"
                v-model="emailForm.username"
            />
          </view>
          <view class="field">
            <text class="cuIcon-notice field-icon"></text>
            <input
                class="field-input"
                type="text"
                placeholder="验证码"
                placeholder-class="field-placeholder"
                v-model="emailForm.code"
            />
            <button
                class="code-btn"
                :class="{'is-disabled': emailCountdown > 0}"
                :disabled="emailCountdown > 0"
                @tap="sendEmailCode"
            >
              {{ emailCountdown > 0 ? emailCountdown + 's' : '获取验证码' }}
            </button>
          </view>
        </view>
      </view>

      <!-- 操作区域 -->
      <view class="action-section">
        <view class="agreement-check" @tap="agree = !agree">
          <text class="checkbox-icon" :class="{'is-checked': agree}">
            <text class="cuIcon-roundcheckfill" v-if="agree"></text>
            <text class="cuIcon-round" v-else></text>
          </text>
          <text class="agreement-text">
            我已阅读并同意
            <text class="agreement-link" @tap.stop="handleUserAgreement">《用户协议》</text>
            和
            <text class="agreement-link" @tap.stop="handlePrivacy">《隐私政策》</text>
          </text>
        </view>

        <button class="login-btn" @tap="handleLogin">
          <text class="login-btn-text">登录</text>
        </button>

        <view v-if="loginType === 'password'" class="helper-row">
          <text class="aux-link" @tap="goToForgotPassword">忘记密码？</text>
        </view>
      </view>

      <!-- 第三方登录 -->
      <view class="third-party-section">
        <view class="divider">
          <view class="divider-line"></view>
          <text class="divider-text">其他登录方式</text>
          <view class="divider-line"></view>
        </view>
        <view class="third-party-icons">
          <view class="third-party-item qq" @tap="handleQqLogin">
            <uni-icons custom-prefix="iconfont" type="icon-QQ" size="30" color="#ffffff"></uni-icons>
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
import {getCaptchaType, qqLogin, sendLoginCredentials} from '@/api/login'
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
      captchaEnabled: true,
      loginType: 'password', // password, phone, email
      showPassword: false,
      countdown: 0,
      // 记住密码开关
      rememberPwd: true,
      emailCountdown: 0,
      timer: null,
      emailTimer: null,
      agree: true,
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
        username: '',
        code: '',
        verifyCode: "",
        loginType: "EMAIL"
      }
    }
  },
  created() {
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
        // todo 发送验证码要花钱，以后再说
      } else if (this.loginType === 'email') {
        this.preLoginEmail(verifyCode)
      }
    },
    preLoginEmail(verifyCode) {
      this.emailForm.verifyCode = verifyCode;
      sendLoginCredentials(this.emailForm).then(res => {
        if(res.code === 200) {
          this.startEmailCountdown();
        }
      })
    },
    loginUsernameAndPassword(verifyCode) {
      this.passwordForm.verifyCode = verifyCode
      this.$store.dispatch('Login', this.passwordForm).then(() => {
        this.saveRememberedInfo('password')
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
    saveRememberedInfo(loginType) {
      try {
        if (this.rememberPwd) {
          uni.removeStorageSync('loginInfo')
          if (loginType === 'password') {
            uni.setStorageSync('loginInfo', {
              username: this.passwordForm.username,
              password: this.passwordForm.password
            })
          } else if (loginType === 'phone') {
          } else if (loginType === 'email') {
            uni.setStorageSync('loginInfo', {
              email: this.emailForm.username,
            })
          }
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
        this.passwordForm.username = loginCache.username ? loginCache.username : ""
        this.passwordForm.password = loginCache.password ? loginCache.password : ""
        this.emailForm.username = loginCache.email ? loginCache.email : ""
        this.rememberPwd = true
      } catch (e) {
        console.error('读取登录缓存失败：', e)
      }
    },
    sendPhoneCode() {
      if (!this.phoneForm.phone) {
        this.$modal.msg("请输入手机号")
        return;
      }
      if (!/^1[3-9]\d{9}$/.test(this.phoneForm.phone)) {
        this.$modal.msg("手机号格式不正确")
        return;
      }
      this.$modal.msgSuccess("验证码已发送")
      this.startCountdown();
    },
    sendEmailCode() {
      if (!this.emailForm.username) {
        this.$modal.msg("请输入邮箱")
        return;
      }
      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.emailForm.username)) {
        this.$modal.msg("邮箱格式不正确")
        return;
      }
      this.$modal.msgSuccess("验证码已发送")
      this.showVerify();
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
        this.$modal.msg("请先阅读并同意用户协议和隐私政策")
        return;
      }
      if (this.loginType === 'password') {
        if (!this.passwordForm.username) {
          this.$modal.msg("请输入账号/手机号/邮箱号")
          return;
        }
        if (!this.passwordForm.password) {
          this.$modal.msg("请输入密码")
          return;
        }
        this.showVerify()
      } else if (this.loginType === 'phone') {
        if (!this.phoneForm.phone || !this.phoneForm.code) {
          this.$modal.msg("请输入完整信息")
          return;
        }
      } else if (this.loginType === 'email') {
        if (!this.emailForm.username) {
          this.$modal.msg("请输入邮箱号")
          return;
        }
        if (!this.emailForm.code) {
          this.$modal.msg("请输入验证码")
          return;
        }
        // 邮箱账号登录
        this.$store.dispatch('Login', this.emailForm).then(() => {
          this.saveRememberedInfo(this.loginType)
          this.loginSuccess()
        })
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
    handleQqLogin() {
      const that = this;
      uni.login({
        provider: 'qq',
        success: function (loginRes) {
          //登录成功
          uni.getUserInfo({
            provider: 'qq',
            success: function(info) {
              // 获取用户信息成功, info.authResult保存用户信息
              if(!info.userInfo || info.userInfo.ret != 0){
                that.$modal.msg("获取用户信息异常");
                return;
              }
              let loginParams = {
                accessToken: loginRes.authResult.access_token,
                openId: loginRes.authResult.openid,
                qqUserInfo: info.userInfo
              }
              that.$modal.loading("正在登录...")
              qqLogin(loginParams).then(res => {
                that.$store.dispatch('ThirdLogin', res).then(() => {
                  that.loginSuccess()
                })
              }).finally(() => {
                this.$modal.closeLoading();
              });
            }
          })
        },
        fail: function (err) {
          this.$modal.closeLoading();
          that.$modal.msg("QQ授权异常");
        }
      });
    },
    goToForgotPassword() {
      this.$modal.msg("请联系管理员找回，Wechat：80616059")
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
/* ============ 基础布局 ============ */
.page {
  position: relative;
  min-height: 100vh;
  background: #f7f8fc;
  overflow: hidden;
  box-sizing: border-box;
}

/* 背景光晕：柔和的品牌色弥散 */
.aurora {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
  z-index: 0;
}
.aurora-1 {
  width: 760rpx;
  height: 760rpx;
  top: -320rpx;
  right: -260rpx;
  background: radial-gradient(circle at center, rgba(102, 126, 234, 0.22) 0%, rgba(102, 126, 234, 0) 68%);
}
.aurora-2 {
  width: 620rpx;
  height: 620rpx;
  top: 260rpx;
  left: -300rpx;
  background: radial-gradient(circle at center, rgba(118, 75, 162, 0.16) 0%, rgba(118, 75, 162, 0) 70%);
}
.aurora-3 {
  width: 700rpx;
  height: 700rpx;
  bottom: -360rpx;
  right: -180rpx;
  background: radial-gradient(circle at center, rgba(102, 126, 234, 0.14) 0%, rgba(102, 126, 234, 0) 70%);
}

.status-bar {
  height: var(--status-bar-height);
  width: 100%;
}

.content {
  position: relative;
  z-index: 1;
  padding: 32rpx 48rpx calc(48rpx + env(safe-area-inset-bottom));
  box-sizing: border-box;
}

/* ============ 品牌 ============ */
.brand {
  display: flex;
  align-items: center;
  animation: rise 0.55s cubic-bezier(0.22, 0.68, 0, 1) both;
}
.brand-logo {
  width: 60rpx;
  height: 60rpx;
  border-radius: 20rpx;
  background: url('/static/logo.png') no-repeat center / cover,
  linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 8rpx 20rpx rgba(102, 126, 234, 0.28);
}
.brand-name {
  margin-left: 16rpx;
  font-size: 30rpx;
  font-weight: 600;
  color: #2b2f4a;
  letter-spacing: 1rpx;
}

/* ============ 标题 ============ */
.hero {
  display: flex;
  flex-direction: column;
  padding: 64rpx 0 48rpx;
  animation: rise 0.55s cubic-bezier(0.22, 0.68, 0, 1) 0.06s both;
}
.hero-title {
  font-size: 60rpx;
  line-height: 1.2;
  font-weight: 700;
  color: #1c2039;
  letter-spacing: 1rpx;
}
.hero-subtitle {
  margin-top: 16rpx;
  font-size: 27rpx;
  color: #8f95ad;
  letter-spacing: 0.5rpx;
}

/* ============ 分段切换 ============ */
.segmented {
  display: flex;
  padding: 8rpx;
  background: #eef0f7;
  border-radius: 999rpx;
  animation: rise 0.55s cubic-bezier(0.22, 0.68, 0, 1) 0.12s both;
}
.segmented-item {
  flex: 1;
  height: 76rpx;
  line-height: 76rpx;
  text-align: center;
  font-size: 27rpx;
  font-weight: 500;
  color: #9aa0b6;
  border-radius: 999rpx;
  transition: all 0.3s cubic-bezier(0.22, 0.68, 0, 1);
}
.segmented-item.is-active {
  background: #ffffff;
  color: #3d4470;
  font-weight: 600;
  box-shadow: 0 4rpx 14rpx rgba(38, 48, 92, 0.1);
}

/* ============ 表单卡片 ============ */
.card {
  margin-top: 36rpx;
  padding: 40rpx 32rpx;
  background: #ffffff;
  border-radius: 40rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.9);
  box-shadow: 0 20rpx 60rpx rgba(38, 48, 92, 0.07), 0 2rpx 8rpx rgba(38, 48, 92, 0.03);
  animation: rise 0.55s cubic-bezier(0.22, 0.68, 0, 1) 0.18s both;
  box-sizing: border-box;
}
.fields {
  display: flex;
  flex-direction: column;
}
.fields .field + .field {
  margin-top: 24rpx;
}
.field {
  display: flex;
  align-items: center;
  height: 104rpx;
  padding: 0 26rpx;
  background: #f6f7fb;
  border: 2rpx solid transparent;
  border-radius: 26rpx;
  transition: all 0.25s ease;
  box-sizing: border-box;
}
.field:focus-within {
  background: #ffffff;
  border-color: #667eea;
  box-shadow: 0 0 0 6rpx rgba(102, 126, 234, 0.1);
}
.field-icon {
  width: 40rpx;
  margin-right: 18rpx;
  font-size: 34rpx;
  color: #a3a9c4;
  text-align: center;
}
.field-input {
  flex: 1;
  height: 100%;
  font-size: 29rpx;
  color: #1c2039;
  background: transparent;
  border: none;
  outline: none;
}
.eye-toggle {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56rpx;
  height: 56rpx;
  margin-left: 8rpx;
  font-size: 32rpx;
  color: #b0b5c9;
}
.eye-toggle:active {
  color: #667eea;
}
.helper-row {
  display: flex;
  justify-content: center;
  margin-top: 30rpx;
}
.aux-link {
  font-size: 25rpx;
  color: #8f95ad;
  font-weight: 500;
  padding: 8rpx 16rpx;
}
.aux-link:active {
  color: #667eea;
}

/* 验证码按钮 */
.code-btn {
  margin: 0 0 0 12rpx;
  padding: 0 26rpx;
  height: 62rpx;
  line-height: 62rpx;
  font-size: 24rpx;
  color: #667eea;
  background: rgba(102, 126, 234, 0.1);
  border-radius: 18rpx;
  border: none;
  transition: all 0.25s ease;
}
.code-btn::after {
  border: none;
}
.code-btn:active:not(.is-disabled) {
  background: rgba(102, 126, 234, 0.18);
}
.code-btn.is-disabled {
  color: #b0b5c9;
  background: #eef0f5;
}

/* ============ 协议与主按钮 ============ */
.action-section {
  margin-top: 40rpx;
  animation: rise 0.55s cubic-bezier(0.22, 0.68, 0, 1) 0.24s both;
}
.agreement-check {
  display: flex;
  align-items: flex-start;
  padding: 0 4rpx 8rpx;
}
.checkbox-icon {
  flex: none;
  margin-right: 10rpx;
  font-size: 32rpx;
  line-height: 40rpx;
  color: #c8ccdc;
  transition: color 0.25s ease;
}
.checkbox-icon.is-checked {
  color: #667eea;
}
.agreement-text {
  font-size: 24rpx;
  line-height: 40rpx;
  color: #959bb1;
}
.agreement-link {
  color: #667eea;
  font-weight: 500;
}

.login-btn {
  margin-top: 36rpx;
  width: 100%;
  height: 100rpx;
  line-height: 100rpx;
  padding: 0;
  border: none;
  border-radius: 30rpx;
  background: linear-gradient(135deg, #6b7cf0 0%, #7b58b4 100%);
  box-shadow: 0 16rpx 32rpx rgba(102, 126, 234, 0.3);
  transition: all 0.25s ease;
}
.login-btn::after {
  border: none;
}
.login-btn:active {
  transform: translateY(2rpx) scale(0.995);
  box-shadow: 0 8rpx 20rpx rgba(102, 126, 234, 0.24);
}
.login-btn-text {
  font-size: 33rpx;
  font-weight: 600;
  color: #ffffff;
  letter-spacing: 6rpx;
}

/* ============ 第三方登录 ============ */
.third-party-section {
  margin-top: 60rpx;
  animation: rise 0.55s cubic-bezier(0.22, 0.68, 0, 1) 0.3s both;
}
.divider {
  display: flex;
  align-items: center;
  justify-content: center;
}
.divider-line {
  width: 120rpx;
  height: 1rpx;
  background: #e3e6f0;
}
.divider-text {
  margin: 0 22rpx;
  font-size: 23rpx;
  color: #a8adc0;
  letter-spacing: 1rpx;
}
.third-party-icons {
  display: flex;
  justify-content: center;
  margin-top: 36rpx;
}
.third-party-item {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 92rpx;
  height: 92rpx;
  border-radius: 50%;
  background: #ffffff;
  border: 1rpx solid #eef0f7;
  box-shadow: 0 8rpx 20rpx rgba(38, 48, 92, 0.08);
  transition: all 0.25s ease;
}
.third-party-item:active {
  transform: scale(0.94);
  box-shadow: 0 4rpx 12rpx rgba(38, 48, 92, 0.06);
}
.third-party-item.qq {
  background: linear-gradient(135deg, #5fb8f5 0%, #3d8ee0 100%);
  border: none;
  box-shadow: 0 10rpx 24rpx rgba(61, 142, 224, 0.28);
}

/* ============ 入场动画 ============ */
@keyframes rise {
  from {
    opacity: 0;
    transform: translateY(24rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>

<!-- 占位符样式需为非 scoped 才能作用于 input 的原生占位节点 -->
<style>
.field-placeholder {
  color: #b0b5c9;
  font-size: 29rpx;
}
</style>

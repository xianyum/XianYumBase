<template>
  <div class="login">
    <div class="login-container">
      <!-- 顶部标题 -->
      <h3 class="main-title">XianYum平台</h3>

      <!-- 登录Tab切换 -->
      <div class="login-tabs">
        <div
          class="login-tab-item"
          :class="{ active: activeTab === 'password' }"
          @click="switchTab('password')"
        >
          账号密码登录
        </div>
        <div
          class="login-tab-item"
          :class="{ active: activeTab === 'qrcode' }"
          @click="switchTab('qrcode')"
        >
          扫码登录
        </div>
      </div>

      <!-- 密码登录表单 -->
      <div v-show="activeTab === 'password'">
        <el-form
          ref="loginForm"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              type="text"
              auto-complete="off"
              placeholder="账号"
              @keyup.enter.native="handleLogin"
            >
              <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              auto-complete="off"
              placeholder="密码"
              @keyup.enter.native="handleLogin"
            >
              <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>
          <el-checkbox v-model="loginForm.rememberMe" style="margin:0px 0px 20px 0px;">记住密码</el-checkbox>

          <div id="captcha-mask" v-show="showCaptchaMask">
            <div id="captcha-box"></div>
          </div>

          <el-form-item style="width:100%;">
            <el-button
              :loading="loading"
              size="medium"
              type="primary"
              style="width:100%;"
              @click.native.prevent="handleLogin"
            >
              <span v-if="!loading">登 录</span>
              <span v-else>登 录 中...</span>
            </el-button>
            <div style="float: right;" v-if="register">
              <router-link class="link-type" :to="'/register'">立即注册</router-link>
            </div>
          </el-form-item>
        </el-form>

        <!-- 三方登录区域：移出el-form，独立模块 -->
        <div class="third-login-area">
          <div class="other_info">
            <span class="other_line"></span>
            <span class="other_login">三方登录</span>
            <span class="other_line"></span>
          </div>
          <div class="third-login-wrapper">
            <span class="other_login_info" @click="aliLogin()" title="使用支付宝登录">
              <svg-icon icon-class="zhifubaologin" class-name='icon_style'></svg-icon>
            </span>
            <span class="other_login_info" @click="wechatLogin()" title="使用微信登录">
              <svg-icon icon-class="weixinlogin" class-name='icon_style'></svg-icon>
            </span>
            <span class="other_login_info" @click="qqLogin()" title="使用QQ登录">
              <svg-icon icon-class="qqlogin" class-name='icon_style'></svg-icon>
            </span>
          </div>
        </div>
      </div>

      <!-- 扫码登录面板（仅修改此处结构+样式） -->
      <div v-show="activeTab === 'qrcode'" class="qrcode-login-panel">
        <div class="qrcode-container">
          <!-- 二维码显示区域 -->
          <div v-if="qrcodeSrc" class="qrcode-img">
            <img :src="qrcodeSrc" alt="扫码登录二维码" />
            <div v-if="isLoginLoading" class="login-loading-mask">
              <i class="el-icon-loading"></i>
              <span>登录中...</span>
            </div>
          </div>
          <!-- 加载中状态 -->
          <div v-else class="qrcode-loading">
            <i class="el-icon-loading" style="font-size: 24px; margin-bottom: 8px;"></i>
            <p>正在生成二维码...</p>
          </div>
          <p class="qrcode-tip">请使用XianYum App扫码登录</p>

          <div class="qrcode-actions">
            <el-button
              type="text"
              @click="refreshQrcode"
              class="refresh-qrcode-btn"
              :loading="isRefreshing"
            >
              刷新二维码
            </el-button>
            <!-- 登录状态提示 -->
            <div v-if="qrcodeStatusText" class="qrcode-status" :class="getStatusClass()">
              <i class="status-icon" :class="getStatusIcon()"></i>
              <span class="status-text">{{ qrcodeStatusText }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部 -->
    <div class="el-login-footer">
      <span>Copyright © 2023-2026 xianyum.cn All Rights Reserved.</span>
    </div>
  </div>
</template>

<script>
import Cookies from "js-cookie";
import { encrypt, decrypt } from '@/utils/jsencrypt';
import { Message } from 'element-ui'
import logo from '@/assets/logo/logo.png'
import QRCode from 'qrcode' // 引入qrcode库
import { getQrcodeTicket, checkQrcodeStatus } from '@/api/login'

export default {
  name: "Login",
  data() {
    return {
      // Tab切换状态
      activeTab: 'password',
      // 二维码相关
      qrcodeSrc: '', // 前端生成的二维码base64地址
      qrcodeTicket: '',
      qrcodeStatusText: '',
      qrcodeCheckTimer: null,
      isLoginLoading: false,
      isRefreshing: false,
      loginForm: {
        username: "xianyu",
        password: "123456",
        rememberMe: false,
        verifyCode: ""
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" },
          { min: 3, max: 20, trigger: "blur", message: "账号长度应在3-20个字符之间" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" },
          { min: 6, max: 20, trigger: "blur", message: "密码长度应在6-20个字符之间" }
        ]
      },
      loading: false,
      // 验证码开关
      captchaEnabled: true,
      // 注册开关
      register: false,
      redirect: undefined,
      showCaptchaMask: false,
      qrcodeRawStatus: ''
    };
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect;
      },
      immediate: true
    },
    // 监听Tab切换，切换到扫码登录时生成二维码
    activeTab(newVal) {
      if (newVal === 'qrcode') {
        this.qrcodeStatusText  = ''
        this.qrcodeRawStatus = '';
        this.generateQrcode();
      } else {
        this.clearQrcodeCheckTimer();
      }
    }
  },
  created() {
    this.getCookie();
  },
  beforeDestroy() {
    // 组件销毁时清除定时器
    this.clearQrcodeCheckTimer();
  },
  methods: {
    getStatusClass() {
      const statusMap = {
        'SCANNED': 'status-scanned',
        'CONFIRMED': 'status-success',
        'EXPIRED': 'status-expired'
      };
      return statusMap[this.qrcodeRawStatus] || '';
    },
    getStatusIcon() {
      const iconMap = {
        'SCANNED': 'el-icon-full-screen',
        'CONFIRMED': 'el-icon-success',
        'EXPIRED': 'el-icon-time'
      };
      return iconMap[this.qrcodeRawStatus] || '';
    },
    // 切换登录Tab
    switchTab(tab) {
      this.activeTab = tab;
    },

    // 生成二维码（前端生成）
    async generateQrcode() {
      try {
        // 1. 从后端获取ticket
        const res = await getQrcodeTicket();
        if (res.code === 200) {
          this.qrcodeTicket = res.data.ticket;
          const qrcodeContent = `login?ticket=${this.qrcodeTicket}`;
          // 前端生成二维码（转为base64）
          this.qrcodeSrc = await QRCode.toDataURL(qrcodeContent, {
            width: 120, // 二维码宽度
            margin: 1, // 边距
            color: {
              dark: '#000000', // 深色
              light: '#ffffff' // 浅色
            }
          });

          // 启动定时器检查扫码状态
          this.startQrcodeCheckTimer();
        } else {
          this.$modal.msgError("获取ticket失败：" + res.msg);
        }
      } catch (error) {
        console.error('生成二维码失败：', error);
        this.$modal.msgError("生成二维码失败，请刷新");
      }
    },

    // 刷新二维码
    async refreshQrcode() {
      this.isRefreshing = true;
      try {
        this.qrcodeRawStatus = '';
        this.qrcodeStatusText = '';
        this.qrcodeSrc = '';
        this.clearQrcodeCheckTimer();
        await this.generateQrcode();
      } finally {
        this.isRefreshing = false;
      }
    },

    // 启动检查扫码状态定时器
    startQrcodeCheckTimer() {
      // 先清除已有定时器
      this.clearQrcodeCheckTimer();

      // 每隔2秒检查一次扫码状态
      this.qrcodeCheckTimer = setInterval(async () => {
        try {
          const requestData = {"ticket": this.qrcodeTicket}
          const res = await checkQrcodeStatus(requestData);
          if (res.code === 200) {
            const status = res.data.loginStatus;
            this.qrcodeRawStatus = status;
            // 状态说明：
            // UNSCANNED: 未扫码 SCANNED: 已扫码待确认 CONFIRMED: 已确认登录 EXPIRED: 二维码过期
            switch (status) {
              case 'UNSCANNED':
                this.qrcodeStatusText  = '';
                break;
              case 'SCANNED':
                this.qrcodeStatusText  = '已扫码，请在手机上确认登录';
                break;
              case 'CONFIRMED':
                this.isLoginLoading = true;
                this.qrcodeStatusText = '登录成功，正在跳转...';
                this.clearQrcodeCheckTimer();
                // 登录成功，获取token并跳转
                const loginData = {
                  "authCode": this.qrcodeTicket,
                  "loginType": "QR"
                };
                try {
                  await this.$store.dispatch("Login", loginData);
                  this.$router.push({ path: this.redirect || "/" }).catch(()=>{});
                } finally {
                  this.isLoginLoading = false;
                }
                break;
              case 'EXPIRED':
                this.qrcodeStatusText  = '二维码已过期，请刷新';
                this.clearQrcodeCheckTimer();
                this.qrcodeSrc = ''; // 清空二维码
                // this.generateQrcode()
                break;
            }
          }
        } catch (error) {
          console.error('检查扫码状态出错：', error);
        }
      }, 2000);
    },

    // 清除扫码状态检查定时器
    clearQrcodeCheckTimer() {
      if (this.qrcodeCheckTimer) {
        clearInterval(this.qrcodeCheckTimer);
        this.qrcodeCheckTimer = null;
      }
    },

    qqLogin(){
      let qqUrl = 'https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id=101831000&redirect_uri=https%3A%2F%2Fbase.xianyum.cn%2FthirdLogin%3FloginType%3Dqq';
      window.location.replace(qqUrl)
    },
    wechatLogin(){
      Message.error('微信登录功能待完善')
    },
    aliLogin(){
      let aliUrl = 'https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?app_id=2019110868997443&scope=auth_user&redirect_uri=https%3A%2F%2Fbase.xianyum.cn%2FthirdLogin%3FloginType%3Dalipay';
      window.location.replace(aliUrl)
    },
    getCookie() {
      const username = Cookies.get("username");
      const password = Cookies.get("password");
      const rememberMe = Cookies.get('rememberMe');
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      };
    },
    async checkSuccess(){
      this.loading = true;
      try {
        if (this.loginForm.rememberMe) {
          Cookies.set("username", this.loginForm.username, { expires: 30 });
          Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 });
          Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 });
        } else {
          Cookies.remove("username");
          Cookies.remove("password");
          Cookies.remove('rememberMe');
        }
        await this.$store.dispatch("Login", this.loginForm);
        this.$router.push({ path: this.redirect || "/" }).catch(()=>{});
      } finally {
        this.loading = false;
      }
    },
    handleLogin() {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          this.showCaptchaMask = true;
          // config 对象为TAC验证码的一些配置和验证的回调
          const config = {
            // 生成接口 (必选项,必须配置)
            requestCaptchaDataUrl: process.env.VUE_APP_BASE_API + "/captcha/genCaptcha",
            // 验证接口 (必选项,必须配置)
            validCaptchaUrl: process.env.VUE_APP_BASE_API + "/captcha/check",
            // 验证码绑定的div块 (必选项,必须配置)
            bindEl: "#captcha-box",
            // 验证成功回调函数(必选项,必须配置)
            validSuccess: (res, c, tac) => {
              // 销毁验证码服务
              tac.destroyWindow();
              this.showCaptchaMask = false;
              this.loginForm.verifyCode = res.data.id;
              this.checkSuccess();
            },
            // 关闭按钮回调事件
            btnCloseFun: (el, tac) => {
              tac.destroyWindow();
              this.showCaptchaMask = false;
              this.loginForm.verifyCode = null;
            }
          };
          let style = {
            logoUrl: logo,
          };
          window.initTAC("./tac", config, style).then(tac => {
            tac.init(); // 调用init则显示验证码
          }).catch(e => {
            console.log("初始化tac失败", e);
          });
        }
      });
    }
  }
};
</script>

<style rel="stylesheet/scss" lang="scss">
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-image: url("https://api.kdcc.cn/img/");
  background-size: cover;
  background-position: center;
  background-attachment: fixed;

  .login-container {
    width: 400px;
    background: #ffffff;
    border-radius: 12px;
    padding: 0;
    overflow: hidden;
    box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.08);
    transition: all 0.3s ease;
    animation: fadeIn 0.5s ease-in-out;

    &:hover {
      box-shadow: 0 6px 25px 0 rgba(0, 0, 0, 0.12);
      transform: translateY(-2px);
    }
  }

  // 顶部标题样式
  .main-title {
    text-align: center;
    color: #333;
    margin: 24px 0 16px 0;
    font-size: 18px;
    font-weight: 600;
    letter-spacing: 0.5px;
  }

  // 登录Tab样式
  .login-tabs {
    display: flex;
    border-bottom: 1px solid #f0f0f0;

    .login-tab-item {
      flex: 1;
      text-align: center;
      padding: 12px 0;
      cursor: pointer;
      position: relative;
      font-size: 15px;
      color: #666;
      transition: all 0.2s ease;

      &:hover {
        color: #1890ff;
        background-color: #f8f9fa;
      }

      &.active {
        color: #1890ff;
        font-weight: 500;

        &::after {
          content: '';
          position: absolute;
          bottom: -1px;
          left: 0;
          width: 100%;
          height: 2px;
          background-color: #1890ff;
          border-radius: 1px;
          animation: slideIn 0.3s ease;
        }
      }
    }
  }

  // 密码登录表单容器
  .login-form {
    padding: 20px 24px 0 24px;

    .el-input {
      height: 36px;
      input {
        height: 36px;
        font-size: 14px;
      }

      &:focus-within {
        box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
        border-radius: 4px;
      }
    }

    .input-icon {
      height: 37px;
      width: 14px;
      margin-left: 2px;
      color: #999;
    }
  }

  // 扫码登录面板样式
  .qrcode-login-panel {
    text-align: center;
    padding: 20px 24px 10px 24px;

    .qrcode-container {
      padding: 16px 0;

      .qrcode-img {
        margin: 0 auto;
        width: 160px;
        height: 160px;
        padding: 8px;
        background: #fff;
        border-radius: 8px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
        transition: all 0.3s ease;

        &:hover {
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }

        img {
          width: 100%;
          height: 100%;
        }

        position: relative;

        .login-loading-mask {
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          background: rgba(255, 255, 255, 0.8);
          display: flex;
          flex-direction: column;
          justify-content: center;
          align-items: center;
          border-radius: 8px;
          z-index: 10;

          .el-icon-loading {
            font-size: 24px;
            margin-bottom: 8px;
            animation: rotate 1s linear infinite;
            color: #1890ff;
          }

          span {
            font-size: 12px;
            color: #666;
          }
        }
      }

      .qrcode-loading {
        height: 160px;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        color: #666;
        font-size: 13px;
      }

      .qrcode-tip {
        margin: 8px 0;
        color: #666;
        font-size: 13px;
      }

      .qrcode-actions {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 12px;
        margin-top: 8px;
      }

      .refresh-qrcode-btn {
        color: #1890ff;
        font-size: 13px;
        margin: 0; // 清空原有margin
        display: inline-block;

        &:hover {
          color: #096dd9;
        }
      }
    }

    .qrcode-status {
      margin: 0;
      padding: 8px 12px;
      border-radius: 6px;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      background-color: #f5f7fa;
      transition: all 0.2s ease;

      .status-icon {
        margin-right: 6px;
        font-size: 14px;
      }

      .status-text {
        font-size: 14px;
        line-height: 1;
      }

      &.status-scanned {
        background-color: #e6f7ff;
        color: #1890ff;

        .status-icon {
          color: #1890ff;
        }
      }

      &.status-success {
        background-color: #f0f9ff;
        color: #52c41a;

        .status-icon {
          color: #52c41a;
        }
      }

      &.status-expired {
        background-color: #fff1f0;
        color: #ff4d4f;

        .status-icon {
          color: #ff4d4f;
        }
      }
    }
  }

  .title {
    display: none;
  }
}

.third-login-area {
  padding: 0 24px 16px 24px;
}

.other_info {
  height: 24px;
  width: 100%;
  line-height: 24px;
  text-align: center;
  margin: 8px 0;
}

.other_info .other_line {
  color: #eee;
  display: inline-block;
  width: 50px; /* 短分割线 */
  border-top: 1px solid #eee;
  margin: 0 4px; /* 最小间距 */
  vertical-align: middle;
}

.other_info .other_login {
  color: #999;
  vertical-align: middle;
  font-size: 12px; /* 小字体 */
}

.third-login-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  margin-bottom: 8px;
}

// 图标样式（调大尺寸）
.icon_style {
  font-size: 30px;
  transition: all 0.2s ease;

  &:hover {
    transform: scale(1.08);
    color: #1890ff;
  }
}

.other_login_info {
  display: inline-flex;
  justify-content: center;
  align-items: center;
  width: 48px;
  height: 48px;
  margin: 0 12px;
  color: #666;
  cursor: pointer;
  border-radius: 50%;
  background-color: #f5f7fa;
  transition: all 0.2s ease;

  &:hover {
    background-color: #e6f7ff;
    transform: translateY(-2px);
  }
}

.login-tip {
  font-size: 12px;
  text-align: center;
  color: #999;
}

.login-code {
  width: 33%;
  height: 36px;
  float: right;

  img {
    cursor: pointer;
    vertical-align: middle;
  }
}

.el-login-footer {
  height: 36px;
  line-height: 36px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: rgba(255, 255, 255, 0.8);
  font-family: Arial;
  font-size: 11px;
  letter-spacing: 0.5px;
  background: rgba(0, 0, 0, 0.1);
}

.login-code-img {
  height: 36px;
}

#captcha-mask {
  display: flex;
  z-index: 99;
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  justify-content: center;
  align-items: center;
  border-radius: 12px;
}

@keyframes rotate {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes slideIn {
  from { width: 0; left: 50%; }
  to { width: 100%; left: 0; }
}
</style>

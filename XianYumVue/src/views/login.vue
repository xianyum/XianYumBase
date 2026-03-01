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
          <el-checkbox v-model="loginForm.rememberMe" style="margin:0px 0px 25px 0px;">记住密码</el-checkbox>

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

            <div class="other_info">
              <span class="other_line"></span>
              <span class="other_login">其他方式登录</span>
              <span class="other_line"></span>
            </div>

            <span class="other_login_info" @click="aliLogin()" title="使用支付宝登录"><svg-icon icon-class="zhifubaologin" class-name='icon_style'></svg-icon></span>
            <span class="other_login_info" @click="wechatLogin()" title="使用微信登录"><svg-icon icon-class="weixinlogin" class-name='icon_style'></svg-icon></span>
            <span class="other_login_info" @click="qqLogin()" title="使用QQ登录"><svg-icon icon-class="qqlogin" class-name='icon_style'></svg-icon></span>
          </el-form-item>
        </el-form>
      </div>

      <!-- 扫码登录面板 -->
      <div v-show="activeTab === 'qrcode'" class="qrcode-login-panel">
        <div class="qrcode-container">
          <!-- 二维码显示区域 -->
          <div v-if="qrcodeSrc" class="qrcode-img">
            <img :src="qrcodeSrc" alt="扫码登录二维码" />
          </div>
          <!-- 加载中状态 -->
          <div v-else class="qrcode-loading">
            <i class="el-icon-loading" style="font-size: 30px; margin-bottom: 10px;"></i>
            <p>正在生成二维码...</p>
          </div>
          <p class="qrcode-tip">请使用XianYum App扫码登录</p>
          <!-- 刷新二维码按钮 -->
          <el-button
            type="text"
            @click="refreshQrcode"
            class="refresh-qrcode-btn"
          >
            刷新二维码
          </el-button>
        </div>
        <!-- 登录状态提示 -->
        <div v-if="qrcodeStatus" class="qrcode-status">
          {{ qrcodeStatus }}
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
      qrcodeStatus: '',
      qrcodeCheckTimer: null,

      loginForm: {
        username: "xianyu",
        password: "123456",
        rememberMe: false,
        verifyCode: ""
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" }
        ]
      },
      loading: false,
      // 验证码开关
      captchaEnabled: true,
      // 注册开关
      register: false,
      redirect: undefined,
      showCaptchaMask: false
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
    refreshQrcode() {
      this.qrcodeSrc = '';
      this.clearQrcodeCheckTimer();
      this.generateQrcode();
    },

    // 启动检查扫码状态定时器
    startQrcodeCheckTimer() {
      // 先清除已有定时器
      this.clearQrcodeCheckTimer();

      // 每隔3秒检查一次扫码状态
      this.qrcodeCheckTimer = setInterval(async () => {
        try {
          const requestData = {"ticket": this.qrcodeTicket}
          const res = await checkQrcodeStatus(requestData);
          if (res.code === 200) {
            const status = res.data.loginStatus;
            // 状态说明：
            // 0: 未扫码 1: 已扫码待确认 2: 已确认登录 3: 二维码过期
            switch (status) {
              case 'UNSCANNED':
                this.qrcodeStatus = '';
                break;
              case 'SCANNED':
                this.qrcodeStatus = '已扫码，请在手机上确认登录';
                break;
              case 'CONFIRMED':
                this.qrcodeStatus = '登录成功，正在跳转...';
                this.clearQrcodeCheckTimer();
                // 登录成功，获取token并跳转
                const loginData = {
                  "authCode": this.qrcodeTicket,
                  "loginType": "QR"
                }
                this.$store.dispatch("Login", loginData).then(() => {
                  this.$router.push({ path: this.redirect || "/" }).catch(()=>{});
                }).finally(() => {
                });
                break;
              case 'EXPIRED':
                this.qrcodeStatus = '二维码已过期，请刷新重试';
                this.clearQrcodeCheckTimer();
                this.qrcodeSrc = ''; // 清空二维码
                this.generateQrcode()
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
    checkSuccess(){
      this.loading = true;
      if (this.loginForm.rememberMe) {
        Cookies.set("username", this.loginForm.username, { expires: 30 });
        Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 });
        Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 });
      } else {
        Cookies.remove("username");
        Cookies.remove("password");
        Cookies.remove('rememberMe');
      }
      this.$store.dispatch("Login", this.loginForm).then(() => {
        this.$router.push({ path: this.redirect || "/" }).catch(()=>{});
      }).finally(() => {
        this.loading = false;
      });
    },
    handleLogin() {
      this.showCaptchaMask = true;
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          // config 对象为TAC验证码的一些配置和验证的回调
          const config = {
            // 生成接口 (必选项,必须配置, 要符合tianai-captcha默认验证码生成接口规范)
            requestCaptchaDataUrl: process.env.VUE_APP_BASE_API + "/captcha/genCaptcha",
            // 验证接口 (必选项,必须配置, 要符合tianai-captcha默认验证码校验接口规范)
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
  height: 100%;
  background-image: url("https://api.kdcc.cn/img/");
  background-size: cover;

  .login-container {
    width: 400px;
    background: #ffffff;
    border-radius: 6px;
    padding: 0;
    overflow: hidden;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  }

  // 顶部标题样式
  .main-title {
    text-align: center;
    color: #707070;
    margin: 30px 0;
    font-size: 20px;
    font-weight: 600;
  }

  // 登录Tab样式
  .login-tabs {
    display: flex;
    border-bottom: 1px solid #e6e6e6;

    .login-tab-item {
      flex: 1;
      text-align: center;
      padding: 15px 0;
      cursor: pointer;
      position: relative;
      font-size: 16px;
      color: #606266;
      transition: color 0.2s;

      &:hover {
        color: #1890ff;
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
        }
      }
    }
  }

  // 密码登录表单容器
  .login-form {
    padding: 25px 25px 5px 25px;

    .el-input {
      height: 38px;
      input {
        height: 38px;
      }
    }

    .input-icon {
      height: 39px;
      width: 14px;
      margin-left: 2px;
    }
  }

  // 扫码登录面板样式
  .qrcode-login-panel {
    text-align: center;
    padding: 25px 25px 5px 25px;

    .qrcode-container {
      padding: 20px 0;

      .qrcode-img {
        margin: 0 auto;
        width: 200px;
        height: 200px;

        img {
          width: 100%;
          height: 100%;
        }
      }

      .qrcode-loading {
        height: 200px;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        color: #666;
      }

      .qrcode-tip {
        margin: 10px 0;
        color: #666;
        font-size: 14px;
      }

      .refresh-qrcode-btn {
        color: #1890ff;
        font-size: 13px;
      }
    }

    .qrcode-status {
      margin-top: 10px;
      color: #666;
      font-size: 14px;
    }
  }

  // 隐藏原来的title样式
  .title {
    display: none;
  }
}

.login-tip {
  font-size: 13px;
  text-align: center;
  color: #bfbfbf;
}

.login-code {
  width: 33%;
  height: 38px;
  float: right;

  img {
    cursor: pointer;
    vertical-align: middle;
  }
}

.el-login-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #fff;
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;
}

.login-code-img {
  height: 38px;
}

.other_info {
  height: 60px;
  width: 100%;
  line-height: 60px;
  text-align: center;
}

.other_info .other_line {
  color: #8c92a4;
  display: inline-block;
  width: 100px;
  border-top: 1px solid #ccc;
}

.other_info .other_login {
  color: #686868;
  vertical-align: -4px;
}

.icon_style {
  font-size: 40px;
}

.other_login_info {
  display: inline-block;
  width: 90px;
  height: 2px;
  line-height: 40px;
  text-align: center;
  padding-top: 1px;
  border-radius: 4px;
  margin-bottom: 20px;
  margin-left: 18px;
}

#captcha-mask {
  display: flex;
  z-index: 99;
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.53);
  justify-content: center;
  align-items: center;
}
</style>

<template>
  <div class="login">
    <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
      <h3 class="title">XianYum平台</h3>
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
<!--      <el-form-item prop="code" v-if="captchaEnabled">-->
<!--        <el-input-->
<!--          v-model="loginForm.code"-->
<!--          auto-complete="off"-->
<!--          placeholder="验证码"-->
<!--          style="width: 63%"-->
<!--          @keyup.enter.native="handleLogin"-->
<!--        >-->
<!--          <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />-->
<!--        </el-input>-->
<!--        <div class="login-code">-->
<!--          <img :src="codeUrl" @click="getCode" class="login-code-img"/>-->
<!--        </div>-->
<!--      </el-form-item>-->

      <Verify
        @success="success"
        :mode="'pop'"
        :captchaType="captchaType"
        :imgSize="{ width: '330px', height: '155px' }"
        ref="verify"
      ></Verify>
      <el-checkbox v-model="loginForm.rememberMe" style="margin:0px 0px 25px 0px;">记住密码</el-checkbox>
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


    <!--  底部  -->
    <div class="el-login-footer">
      <span>Copyright © 2023-2025 xianyum.cn All Rights Reserved.</span>
    </div>
  </div>
</template>

<script>
import Cookies from "js-cookie";
import Verify from './../components/Verifition/Verify';
import { encrypt, decrypt } from '@/utils/jsencrypt';
import { Message } from 'element-ui'


export default {
  name: "Login",
  data() {
    return {
      captchaType: 'blockPuzzle',
      codeUrl: "",
      loginForm: {
        username: "xianyu",
        password: "123456",
        rememberMe: false,
        code: "",
        uuid: ""
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
      // 注册开关
      register: false,
      redirect: undefined
    };
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect;
      },
      immediate: true
    }
  },
  components: {
    Verify
  },
  created() {
    this.getCookie();
  },
  methods: {
    qqLogin(){
      let qqUrl = 'https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id=101831000&redirect_uri=https%3a%2f%2fbase.xianyum.cn%2f%23%2fcheckQQLogin';
      window.location.replace(qqUrl)
    },
    wechatLogin(){
      Message.error('微信登录功能待完善')
    },
    aliLogin(){
      let aliUrl = 'https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?app_id=2019110868997443&scope=auth_user&redirect_uri=https%3a%2f%2fbase.xianyum.cn%2f%23%2fcheckAliLogin'
      window.location.replace(aliUrl)
    },
    success(params){
      this.loginForm.captchaVerification = params.captchaVerification
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true;
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.userName, { expires: 30 });
            Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 });
            Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 });
          } else {
            Cookies.remove("username");
            Cookies.remove("password");
            Cookies.remove('rememberMe');
          }
          this.$store.dispatch("Login", this.loginForm).then(() => {
            this.$router.push({ path: this.redirect || "/" }).catch(()=>{});
          }).catch(() => {
            this.loading = false;
          });
        }
      });
    },
    getCookie() {
      const username = Cookies.get("username");
      const password = Cookies.get("password");
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      };
    },
    handleLogin() {
      this.$refs.verify.show()
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
  //background-image: url("../assets/images/login-background.jpg");
  background-image: url("https://api.kdcc.cn/img/");
  background-size: cover;
}
.title {
  margin: 0px auto 30px auto;
  text-align: center;
  color: #707070;
}

.login-form {
  border-radius: 6px;
  background: #ffffff;
  width: 400px;
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

.other_info{
  height: 60px;
  width: 100%;
  line-height: 60px;
  text-align: center;
}
.other_info .other_line {
  color: #8c92a4;
  display: inline-block;
  width: 100px;
  border-top: 1px solid #ccc ;
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
</style>

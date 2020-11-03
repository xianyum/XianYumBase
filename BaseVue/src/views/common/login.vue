<template>
  <div class="site-page--login">
    <div
      v-if="isShow"
      v-loading="socialLoading"
      class="login-container"
      :element-loading-text="'现在进行'+currentPath+'第三方登录,请稍等'"
    >
      <div class="login-center">
        <div class="title-container">
          <h3 class="title" style="margin-right: 77px">
            BASE DEMO WEB
          </h3>
        </div>
        <el-tabs v-model="activeName" @tab-click="handleClick">
          <el-tab-pane name="loginForm">
            <span slot="label"><i slot="prefix" class=""></i> 帐号登录</span>
            <el-form
              :model="dataForm"
              :rules="dataRule"
              ref="dataForm"
              @keyup.enter.native="handleLogin()"
              class="login-form"
              label-position="left"
            >
              <el-form-item prop="userName">
                <el-input
                  v-model="dataForm.userName"
                  placeholder="账号"
                  name="userName"
                  type="text"
                  autocomplete="off"
                  style="width: 80%"
                >
                </el-input>
              </el-form-item>

              <el-form-item prop="password">
                <el-input
                  v-model="dataForm.password"
                  placeholder="密码"
                  name="password"
                  type="password"
                  style="width: 80%"
                >
                </el-input>
              </el-form-item>
<!--              <el-form-item>-->
<!--                <el-col :span="12" style="overflow:hidden">-->
<!--                  <el-form-item prop="captcha">-->
<!--                    <el-input-->
<!--                      v-model="dataForm.captcha"-->
<!--                      type="test"-->
<!--                      auto-complete="off"-->
<!--                      placeholder="验证码, 单击图片刷新"-->
<!--                      style="width: 100%;"-->
<!--                    />-->
<!--                  </el-form-item>-->
<!--                </el-col>-->
<!--                <el-col class="line" :span="1">&nbsp;</el-col>-->
<!--                <el-col :span="11">-->
<!--                  <el-form-item>-->
<!--                    <img-->
<!--                      style="width: 57%;height: 35px;float: left;"-->
<!--                      class="pointer"-->
<!--                      :src="src"-->
<!--                      alt=""-->
<!--                      @click="refreshCaptcha"-->
<!--                    >-->
<!--                  </el-form-item>-->
<!--                </el-col>-->
<!--              </el-form-item>-->


              <el-form-item>
                <Verify
                  @success="success"
                  :mode="'pop'"
                  :captchaType="'blockPuzzle'"
                  :imgSize="{ width: '330px', height: '155px' }"
                  ref="verify"
                ></Verify>
              </el-form-item>

              <el-button
                :loading="loading"
                type="primary"
                style="width:80%;"
                @click.native.prevent="handleLogin"
              >
                登录
              </el-button>
            </el-form>
          </el-tab-pane>
          <el-tab-pane name="phoneForm" label="手机号登录">
            <span slot="label"><i slot="prefix" class=""></i> 手机号登录</span>
            <el-form
              ref="phoneForm"
              :model="phoneForm"
              class="login-form"
              label-position="left"
            >
              <el-form-item prop="phone">
                <el-input
                  v-model="phoneForm.phone"
                  placeholder="请输入手机号"
                  name="phone"
                  type="text"
                  autocomplete="off"
                  style="width:80%;"
                >
                </el-input>
              </el-form-item>

              <el-form-item prop="code">
                <el-input
                  v-model="phoneForm.code"
                  placeholder="请输入验证码"
                  name="code"
                  style="width: 49%;"
                  autocomplete="off"
                >
                </el-input>
                <el-button :loading="codeLoading" :disabled="isDisabled" @click="sendCode">{{ buttonName }}</el-button>
              </el-form-item>

              <el-button
                :loading="loading"
                type="primary"
                style="width:80%;"
                @click.native.prevent="phoneLogin"
              >登录
              </el-button>
            </el-form>
          </el-tab-pane>
        </el-tabs>
        <div class="other_info">
          <span class="other_line"></span>
          <span class="other_login">其他方式登录</span>
          <span class="other_line"></span>
        </div>
        <span class="other_login_info" @click="aliLogin()" title="使用支付宝登录"><icon-svg name="zhifubaologin"
                                                                                     class="icon_style"></icon-svg></span>
        <span class="other_login_info" @click="otherLogin()" title="使用微信登录"><icon-svg name="weixinlogin"
                                                                                      class="icon_style"></icon-svg></span>
        <span class="other_login_info" @click="qqLogin()" title="使用QQ登录"><icon-svg name="qqlogin"
                                                                                      class="icon_style"></icon-svg></span>
        <!--<div class="register">-->
        <!--<span class="" @click="gotoRegister()">注册账户</span>-->
        <!--</div>-->
      </div>
    </div>
  </div>
</template>

<script>
  import {getUUID} from '@/utils'
  import {isvalidPhone} from '@/utils/validate'
  import Verify from "./../../components/verifition/Verify";

  export default {
    name: 'Login',
    data () {
      return {
        tenantList: [],
        dataForm: {
          userName: 'admin',
          password: 'admin',
          uuid: '',
          captcha: ''
        },
        src: '',
        phoneForm: {
          phone: '',
          code: ''
        },
        passwordType: 'password',
        loading: false,
        showDialog: false,
        redirect: undefined,
        token: '',
        isShow: true,
        activeName: 'loginForm',
        buttonName: '发送验证码',
        isDisabled: false,
        codeLoading: false,
        time: 60,
        socialLoading: false,
        currentPath: '',
        active: '',
        dataRule: {
          userName: [
            {required: true, message: '帐号不能为空', trigger: 'blur'}
          ],
          password: [
            {required: true, message: '密码不能为空', trigger: 'blur'}
          ],
          captcha: [
            {required: true, message: '验证码不能为空', trigger: 'blur'}
          ]
        }
      }
    },
    created () {
      // this.refreshCaptcha()
    },
    components: {
      Verify
    },
    mounted () {
      // 自动加载indexs方法

    },
    methods: {
      success(params){
        let captchaVerification = params.captchaVerification
        let _this = this
        _this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            _this.$http({
              url: _this.$http.adornUrl('/login'),
              method: 'post',
              data: _this.$http.adornData({
                'username': _this.dataForm.userName,
                'password': _this.dataForm.password,
                'uuid': _this.dataForm.uuid,
                'captcha': _this.dataForm.captcha,
                'captchaVerification': captchaVerification
              })
            }).then(({data}) => {
              if (data && data.code === 200) {
                _this.$cookie.set('token', data.token)
                _this.$router.replace({name: 'home'})
              } else {
                _this.refreshCaptcha()
                _this.$message.error(data.msg)
              }
            })
          }
        })
      },
      showVerify(){
        this.$refs.verify.show();
      },
      otherLogin () {
        this.$message.error('微信登录功能待完善')
      },
      aliLogin () {
        let aliUrl = 'https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?app_id=2019110868997443&scope=auth_user&redirect_uri=http%3a%2f%2fxianyum.cn%2fbase%2f%23%2fcheckAliLogin'
        window.location.replace(aliUrl)
      },
      qqLogin () {
        window.location.replace('https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id=101831000&redirect_uri=http%3a%2f%2fxianyum.cn%2fbase%2f%23%2fcheckQQLogin')
      },
      // 用户名 密码登录
      handleLogin () {
        this.$refs.verify.show()
        // let _this = this
        // _this.$refs['dataForm'].validate((valid) => {
        //   if (valid) {
        //     _this.$http({
        //       url: _this.$http.adornUrl('/login'),
        //       method: 'post',
        //       data: _this.$http.adornData({
        //         'username': _this.dataForm.userName,
        //         'password': _this.dataForm.password,
        //         'uuid': _this.dataForm.uuid,
        //         'captcha': _this.dataForm.captcha
        //       })
        //     }).then(({data}) => {
        //       if (data && data.code === 200) {
        //         _this.$cookie.set('token', data.token)
        //         _this.$router.replace({name: 'home'})
        //       } else {
        //         _this.refreshCaptcha()
        //         _this.$message.error(data.msg)
        //       }
        //     })
        //   }
        // })
      },

      // 手机号短信登录
      phoneLogin () {
        this.$message.error('短信登录暂时无法使用')
      },
      refreshCaptcha: function () {
        this.dataForm.uuid = getUUID()
        this.src = this.$http.adornUrl(`/captcha.jpg?uuid=${this.dataForm.uuid}`)
      },
      // 发送短信验证码
      sendCode () {
        if (this.phoneForm.phone !== '' && isvalidPhone(this.phoneForm.phone)) {
          this.codeLoading = true
          this.buttonName = '正在发送'
          const _this = this
          this.dataForm.uuid = getUUID()
          this.$http({
            url: this.$http.adornUrl('/getPhoneCode'),
            method: 'post',
            data: this.$http.adornData({
              'mobile': this.phoneForm.phone,
              'uuid': this.dataForm.uuid
            })
          }).then(({data}) => {
            if (data && data.code === 200) {
              this.$message({
                message: '发送验证码成功',
                type: 'success'
              })
              this.codeLoading = false
              this.isDisabled = true
              this.buttonName = this.time-- + '秒后重发'
              this.timer = window.setInterval(function () {
                _this.buttonName = _this.time + '秒后重发'
                --_this.time
                if (_this.time < 0) {
                  _this.buttonName = '重新发送'
                  _this.time = 60
                  _this.isDisabled = false
                  window.clearInterval(_this.timer)
                }
              }, 1000)
            } else {
              this.$message.error(data.msg)
              this.codeLoading = false
            }
          })
        }
      },
      handleClick (tab, event) {
        this.$refs[tab.paneName].resetFields()
      },
      gotoRegister () {
        this.$router.push({
          path: '/register'
        })
      },
      handleTenant (tenantId) {
      },
      getTenantList () {
      }
    }
  }
</script>

<style rel="stylesheet/scss" lang="scss">
  .site-page--login {
    position: fixed;
    top: 0;
    left: 0;
    z-index: -1;
    width: 100%;
    height: 100%;
    content: "";
    background-image: url(https://xiaoyaxiaokeai.gitee.io/base/20201018/457a981a-b7fe-4777-a7b8-ed1a5e8abf2e.jpg);
    /*background-image: url(~@/assets/img/pg5.jpg);*/
    background-size: cover;
  }

  .login-container {
    width: 500px;
    display: flex;
    border-radius: 5px;
    overflow: hidden;
    background: #fff;
    box-shadow: 0 0 25px #cac6c6;
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    padding: 40px 0;

    .login-left {
      width: 50%;
      /*background: #1F79D6;*/
      text-align: center;
      padding-top: 90px;
      border-right: 1px solid #00000055;

      img {
        width: 140px;
      }
    }

    .login-center {
      width: 100%;
      padding: 35px;
      margin-left: 60px;
      height: 450px;

      .title {
        margin: 0 auto 30px auto;
        text-align: center;
        color: #505458;
      }

      .remember {
        margin: 0 0 35px 0;
      }

      .el-form-item {
        border: 1px solid rgba(255, 255, 255, 0.1);
        border-radius: 5px;
        color: #454545;
      }

      .other-login {
        margin-top: 3vh;
        display: flex;
        flex-direction: row;
        flex-wrap: wrap;
      }

      .other-icon {
        cursor: pointer;
        margin-left: 5px;
        fill: rgba(0, 0, 0, .2);
      }

      .other-icon:hover {
        fill: rebeccapurple;
      }

      .other-login .other-way {
        font-size: 14px;
        color: #515a6e;
      }

      .register {
        float: right;
        color: #1ab2ff;
        font-size: 14px;
        cursor: pointer;
        width: calc(100% - 160px);
        text-align: right;
      }

      .login-select {
        margin-left: 100px;
        margin-bottom: 13px;

        input {
          color: #333;
          font-size: 14px;
          font-weight: 400;
          border: none;
          text-align: center;
        }
      }

      .el-tabs__nav-wrap::after {
        content: "";
        position: absolute;
        left: 0;
        bottom: 0;
        width: 80%;
        height: 2px;
        background-color: #e4e7ed;
        z-index: 1;
      }

      /*.other_login {*/
        /*color: #8c92a4;*/
        /*margin-top: 3vh;*/
        /*display: flex;*/
        /*flex-direction: row;*/
        /*flex-wrap: wrap;*/
        /*margin-left: 100px;*/

      /*}*/

      .other_login_info {
        display: inline-block;
        width: 90px;
        height: 50px;
        line-height: 40px;
        text-align: center;
        padding-top: 1px;
        border-radius: 4px;
        margin-bottom: 20px;
        margin-right: 5px;
      }

      .icon_style {
        font-size: 40px;
        margin-top: 8px;
      }
      .other_info{
        height: 60px;
        width: 80%;
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
    }

  }
</style>

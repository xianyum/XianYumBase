<template>
  <div class="site-wrapper site-page--login">
    <div class="site-content__wrapper">
      <div class="site-content">
        <div class="login-main1">
          <h3 class="login-title" align="center">注册账号</h3>
          <el-form :model="dataForm" :rules="dataRule" size="small" ref="dataForm" @keyup.enter.native="dataFormSubmit()" status-icon>
            <el-form-item prop="userName">
              <el-input v-model="dataForm.userName" placeholder="帐号"></el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="dataForm.password" type="password" placeholder="密码"></el-input>
            </el-form-item>
            <el-form-item prop="mobile">
              <el-input v-model="dataForm.mobile" placeholder="手机号"></el-input>
            </el-form-item>
            <el-form-item prop="email">
              <el-input v-model="dataForm.email" placeholder="邮箱（用于接受验证码）"></el-input>
            </el-form-item>
            <el-row :gutter="2">
                <el-col :span="14"><el-form-item prop="code"><el-input v-model="dataForm.code" placeholder="验证码" style="width: 95%"></el-input></el-form-item></el-col>
                <el-col :span="4">
                  <el-button  @click="getAuthCode" style="float: left;left: -50px;">{{content}}</el-button>
                </el-col>
            </el-row>
            <el-form-item>
              <el-button class="login-btn-submit" type="primary" @click="dataFormSubmit()">注册</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import { getUUID } from '@/utils'
  import { isEmail, isMobile } from '@/utils/validate'
  export default {
    data () {
      var validateEmail = (rule, value, callback) => {
        if (!isEmail(value)) {
          callback(new Error('邮箱格式错误'))
        } else {
          callback()
        }
      }
      var validateMobile = (rule, value, callback) => {
        if (!isMobile(value)) {
          callback(new Error('手机号格式错误'))
        } else {
          callback()
        }
      }
      return {
        totaltime: 60,
        clickCancle: true,
        content: '获取验证码',
        dataForm: {
          userName: '',
          password: '',
          uuid: '',
          captcha: ''
        },
        dataRule: {
          userName: [
            { required: true, message: '帐号不能为空', trigger: 'blur' }
          ],
          password: [
            { required: true, message: '密码不能为空', trigger: 'blur' }
          ],
          email: [
            { required: true, message: '邮箱不能为空', trigger: 'blur' },
            { validator: validateEmail, trigger: 'blur' }
          ],
          mobile: [
            { required: true, message: '手机号不能为空', trigger: 'blur' },
            { validator: validateMobile, trigger: 'blur' }
          ]
        },
        captchaPath: ''
      }
    },
    created () {
    },
    methods: {
      flushCode: function () {
        this.content = this.totaltime + 's后重新发送'
        let m = setInterval(() => {
          this.totaltime--
          this.content = this.totaltime + 's后重新发送'
          if (this.totaltime === 0) {
            clearInterval(m)
            this.content = '获取验证码'
            this.totaltime = 60
            this.clickCancle = true
          }
        }, 1000)
      },
      getAuthCode () {
        if (!this.clickCancle) return
        this.clickCancle = false
        this.dataForm.uuid = getUUID()
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl('/getRegisterCode'),
              method: 'post',
              data: this.$http.adornData({
                'email': this.dataForm.email,
                'uuid': this.dataForm.uuid
              })
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.flushCode()
                this.$message({
                  message: '发送验证码成功，请到邮箱查看',
                  type: 'success'
                })
              } else {
                this.$message.error(data.msg)
                this.clickCancle = true
              }
            })
          }
        })
      },
      // 注册
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl('/register'),
              method: 'post',
              data: this.$http.adornData({
                'username': this.dataForm.userName,
                'password': this.dataForm.password,
                'email': this.dataForm.email,
                'mobile': this.dataForm.mobile,
                'code': this.dataForm.code,
                'uuid': this.dataForm.uuid
              })
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.$message({
                  message: '注册成功,即将跳转登录界面',
                  type: 'success'
                })
                this.$refs['dataForm'].resetFields()
                this.$router.replace({ name: 'login' })
              } else {
                this.$message.error(data.msg)
              }
            })
          }
        })
      }
    }
  }
</script>

<style lang="scss">
  .site-wrapper.site-page--login {
    position: absolute;
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
    /*background-color: rgba(38, 50, 56, .6);*/
    overflow: hidden;
    &:before {
      position: fixed;
      top: 0;
      left: 0;
      z-index: -1;
      width: 100%;
      height: 100%;
      content: "";
      background-image: url(~@/assets/img/pg1.jpg);
      background-size: cover;
    }
    .site-content__wrapper {
      position: absolute;
      top: 0;
      right: 0;
      bottom: 0;
      left: 0;
      padding: 0;
      margin: 0;
      overflow-x: hidden;
      overflow-y: auto;
      background-color: transparent;
    }
    .site-content {
      min-height: 100%;
      padding: 30px 500px 30px 30px;
    }
    .brand-info {
      margin: 220px 100px 0 90px;
      color: #fff;
    }
    .brand-info__text {
      margin:  0 0 22px 0;
      font-size: 48px;
      font-weight: 400;
      text-transform : uppercase;
    }
    .brand-info__intro {
      margin: 10px 0;
      font-size: 16px;
      line-height: 1.58;
      opacity: .6;
    }
    .login-main1 {
      position: absolute;
      width: 400px;
      height: 500px;
      left: 50%;
      top: 50%;
      transform: translate(-50%,-50%);
      padding: 50px 60px 180px;
      /*min-height: 100%;*/
      /*background-color: #fff;*/
      background: rgba(255,255,255,.2)
    }
    .login-title {
      font-size: 16px;
    }
    .login-captcha {
      overflow: hidden;
      > img {
        width: 100%;
        cursor: pointer;
      }
    }
    .login-btn-submit {
      width: 100%;
      margin-top: 5px;
    }
  }
</style>

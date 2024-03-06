<template>

</template>


<script>

import { aliLogin , qqLogin} from '@/api/login'
import { qqBindUserRequest,aliBindUserRequest } from "@/api/system/user";

  export default {
    name: 'thirdLogin',
    data () {
      return {
        form:{
          authCode: ''
        }
      }
    },
    created () {
      let loginType = this.$route.query.loginType
      // 判断类型，主要区分是登录还是绑定用户操作
      let type = this.$route.query.type
      if(type){
        if(loginType == 'alipay'){
          // 支付宝授权后返回auth_code
          this.form.authCode = this.$route.query.auth_code
          this.aliBindUser()
        }else if (loginType == 'qq'){
          // qq授权后返回code
          this.form.authCode = this.$route.query.code
          this.qqBindUser()
        }else{
          location.href = '/index';
        }
      }else{
        if(loginType == 'alipay'){
          // 支付宝授权后返回auth_code
          this.form.authCode = this.$route.query.auth_code
          this.aliLogin()
        }else if (loginType == 'qq'){
          // qq授权后返回code
          this.form.authCode = this.$route.query.code
          this.qqLogin()
        }else{
          location.href = '/index';
        }
      }
    },
    methods: {
      aliBindUser(){
        aliBindUserRequest(this.form).then(res => {
          this.$router.push({ name: 'Profile', params: {'activeTab': 'thirdUser'} });
        }).catch(error => {
          location.href = '/index';
        })
      },
      qqBindUser(){
        qqBindUserRequest(this.form).then(res => {
          this.$router.push({ name: 'Profile', params: {'activeTab': 'thirdUser'} });
        }).catch(error => {
          location.href = '/index';
        })
      },
      // 支付宝登录
      aliLogin () {
        aliLogin(this.form).then(res => {
          this.$store.dispatch("ThirdLogin", res).then(() => {
            this.$router.push({ path: this.redirect || "/" }).catch(()=>{});
          }).catch(() => {
          });
        }).catch(error => {
          location.href = '/index';
        })
      },
      // QQ登录
      qqLogin () {
        qqLogin(this.form).then(res => {
          this.$store.dispatch("ThirdLogin", res).then(() => {
            this.$router.push({ path: this.redirect || "/" }).catch(()=>{});
          }).catch(() => {
          });
        }).catch(error => {
          location.href = '/index';
        })
      }
    }
  }
</script>

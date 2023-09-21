<template>

</template>


<script>

import { aliLogin , qqLogin} from '@/api/login'

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
    },
    methods: {
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

<style scoped>

</style>

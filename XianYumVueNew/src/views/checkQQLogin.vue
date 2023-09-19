<template>

</template>

<script>
import { qqLogin } from '@/api/login'
import { Message } from 'element-ui'
  export default {
    name: 'checkQQLogin',
    data () {
      return {
        form:{
          authCode: ''
        }
      }
    },
    created () {
      this.form.authCode = this.$route.query.code
      this.qqLogin();
    },
    methods: {
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

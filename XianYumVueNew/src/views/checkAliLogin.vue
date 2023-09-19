<template>

</template>


<script>

import { aliLogin} from '@/api/login'

  export default {
    name: 'checkAliLogin',
    data () {
      return {
        form:{
          authCode: ''
        }
      }
    },
    created () {
      this.form.authCode = this.$route.query.auth_code
      this.aliLogin()
    },
    methods: {
      aliLogin () {
        aliLogin(this.form).then(res => {
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

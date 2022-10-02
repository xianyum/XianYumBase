<template>

</template>

<script>
  export default {
    name: 'checkAliLogin',
    data () {
      return {
        authCode: ''
      }
    },
    created () {
      this.authCode = this.$route.query.auth_code
      this.aliLogin()
    },
    methods: {
      aliLogin () {
        this.$http({
          url: this.$http.adornUrl('/ali/login'),
          method: 'post',
          data: this.$http.adornData({
            'authCode': this.authCode
          })
        }).then(({data}) => {
          if (data && data.code === 200) {
            this.$cookie.set('token', data.token)
            this.$router.replace({ name: 'home' })
          } else {
            this.$router.replace({ name: 'login' })
            this.$message.error('没有登录权限')
          }
        })
      }
    }
  }
</script>

<style scoped>

</style>

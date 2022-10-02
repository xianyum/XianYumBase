<template>

</template>

<script>
  export default {
    name: 'checkQQLogin',
    data () {
      return {
        authCode: ''
      }
    },
    created () {
      this.authCode = this.$route.query.code
      this.qqLogin()
    },
    methods: {
      qqLogin () {
        this.$http({
          url: this.$http.adornUrl('/qq/login'),
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

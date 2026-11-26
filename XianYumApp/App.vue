<script>
  import config from './config'
  import { getToken } from '@/utils/auth'

  export default {
    // Vue3 需显式声明 globalData，供 getApp().globalData 读取
    globalData: {
      config: null
    },
    onLaunch: function() {
      this.initApp()
    },
    methods: {
      // 初始化应用
      initApp() {
        // 初始化应用配置
        this.initConfig()
        // 检查用户登录状态
        this.checkLogin()
      },
      initConfig() {
        this.globalData.config = config
      },
      checkLogin() {
        if (!getToken()) {
          this.$tab.reLaunch('/pages/login') 
        }
      }
    }
  }
</script>

<style lang="scss">
  @import '@/static/scss/index.scss'
</style>

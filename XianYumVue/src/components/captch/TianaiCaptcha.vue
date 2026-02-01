<template>
  <div class="tianai-captcha-wrap" ref="captchaContainer"></div>
</template>

<script>
export default {
  name: 'TianaiCaptcha',
  props: {
    // 天御验证码appId（必传，从控制台获取）
    appId: {
      type: String,
      required: true
    },
    // 服务端获取验证码token的接口地址（必传，需后端配合实现）
    tokenApi: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      // 验证码实例对象
      captchaInstance: null,
      // 防止重复初始化
      isIniting: false
    }
  },
  beforeDestroy() {
    // 组件销毁时销毁验证码实例，防止内存泄漏
    if (this.captchaInstance) {
      this.captchaInstance.destroy()
      this.captchaInstance = null
    }
  },
  methods: {
    // 1. 调用服务端接口获取验证码token（天御要求必须服务端生成）
    async getCaptchaToken() {
      try {
        const res = await this.$axios({
          url: this.tokenApi,
          method: 'GET',
          timeout: 5000
        })
        // 后端返回格式约定：{ code: 200, data: { token: 'xxx' }, msg: '成功' }
        if (res.code === 200 && res.data.token) {
          return res.data.token
        } else {
          this.$message.error('获取验证参数失败：' + (res.msg || '服务端异常'))
          return null
        }
      } catch (err) {
        this.$message.error('请求验证接口失败：' + err.message)
        return null
      }
    },

    // 2. 初始化天御验证码（供父组件调用）
    async initCaptcha() {
      // 防止重复初始化
      if (this.isIniting || this.captchaInstance) return
      this.isIniting = true

      // 校验SDK是否加载成功（挂载在window上）
      if (!window.TianaiCaptcha) {
        this.$message.error('验证码SDK加载失败，请刷新页面重试')
        this.isIniting = false
        return
      }

      // 获取服务端token
      const token = await this.getCaptchaToken()
      if (!token) {
        this.isIniting = false
        return
      }

      try {
        // 创建天御验证码实例
        this.captchaInstance = new window.TianaiCaptcha({
          appId: this.appId, // 应用ID
          token: token, // 服务端生成的token
          el: this.$refs.captchaContainer, // 渲染容器
          // 验证成功回调（核心）
          onSuccess: (res) => {
            this.isIniting = false
            // 向父组件传递验证结果（ticket+randStr是服务端校验的核心）
            this.$emit('verifySuccess', res)
            // 验证成功后重置验证码，支持再次验证
            this.resetCaptcha()
          },
          // 验证失败回调
          onFail: (err) => {
            this.isIniting = false
            this.$message.error('验证失败：' + (err.msg || '系统异常'))
          },
          // 验证码关闭回调
          onClose: () => {
            this.isIniting = false
            this.$emit('verifyClose')
            this.resetCaptcha()
          }
        })

        // 执行实例初始化
        await this.captchaInstance.init()
      } catch (err) {
        this.isIniting = false
        this.$message.error('验证码初始化失败：' + err.message)
        console.error('天御验证码初始化异常：', err)
      }
    },

    // 3. 重置验证码（供父组件调用，或内部自动调用）
    resetCaptcha() {
      if (this.captchaInstance) {
        this.captchaInstance.reset()
      }
    }
  }
}
</script>

<style scoped>
.tianai-captcha-wrap {
  width: 100%;
  height: auto;
  min-height: 80px;
  margin-bottom: 15px;
}
</style>

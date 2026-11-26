<template>
  <view class="page-box">
    <zero-markdown-view
        :markdown="content"
        :aiMode="true"
        themeColor="#cb28f8"
    />
  </view>
</template>

<script>
export default {
  data() {
    return {
      title: "",
      content: ""
    }
  },
  onLoad(options) {
    this.title = options.markdownTitle || "Markdown浏览";
    // 来源页面用 encodeURIComponent 传参，App 端部分基座拿到的仍是编码后的原文，这里兜底解码
    this.content = this.decodeQuery(options.markdownContent);

    // 设置导航标题
    this.$setPageTitle(this.title);
  },
  methods: {
    decodeQuery(value) {
      if (!value) return "";
      try {
        return decodeURIComponent(value);
      } catch (e) {
        return value;
      }
    }
  },
};
</script>

<style scoped lang="scss">
.page-box {
  padding: 20rpx;
  min-height: 100vh;
  background: #f5f7fa;
}
</style>
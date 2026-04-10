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
    this.content = options.markdownContent || "";

    // 设置导航标题
    uni.setNavigationBarTitle({
      title: this.title
    });
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
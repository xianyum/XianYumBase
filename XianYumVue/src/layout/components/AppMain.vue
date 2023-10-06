<template>
  <section class="app-main">
    <transition name="fade-transform" mode="out-in">
      <keep-alive :include="cachedViews">
        <router-view v-if="!$route.meta.link" :key="key" />
      </keep-alive>
    </transition>
    <iframe-toggle />
  </section>
</template>

<script>
import iframeToggle from "./IframeToggle/index"

export default {
  name: 'AppMain',
  components: { iframeToggle },
  computed: {
    cachedViews() {
      return this.$store.state.tagsView.cachedViews
    },
    key() {
      return this.$route.path
    }
  }
}
</script>

<style lang="scss" scoped>
.app-main {
  /* 50= navbar  50  */
  min-height: calc(100vh - 50px);
  width: 100%;
  position: relative;
  overflow: hidden;
}

.fixed-header + .app-main {
  padding-top: 50px;
}

.hasTagsView {
  .app-main {
    /* 84 = navbar + tags-view = 50 + 34 */
    min-height: calc(100vh - 84px);
  }

  .fixed-header + .app-main {
    padding-top: 84px;
  }
}
</style>

<style lang="scss">
// fix css style bug in open el-dialog
.el-popup-parent--hidden {
  .fixed-header {
    padding-right: 6px;
  }
}

::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background-color: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background-color: #c0c0c0;
  border-radius: 3px;
}

//宽度可以使用max-width显示,占比为屏幕占比，高度用max-height是行不通的,于是就用的超出行省略，当
//在限制的宽度之内多少行限制(不限制当内容几千几万字时就挂了),padding为了调整我这个正好15行省略看
//起来样式不会出问题,有问题的话可以调padding,line-height,font-size保证展示框样式完整
.el-tooltip__popper{
  max-width:30%;
  padding-bottom: 5px!important;
  display: -webkit-box;
  overflow: hidden;
  text-overflow: ellipsis;
  -webkit-line-clamp: 15;
  -webkit-box-orient: vertical;

}
.el-tooltip__popper,.el-tooltip__popper.is-dark{
  background:rgb(48, 65, 86) !important;
  color: #fff !important;
  line-height: 24px;
}
</style>

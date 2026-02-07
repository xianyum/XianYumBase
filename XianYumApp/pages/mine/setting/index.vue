<template>
  <view class="setting-container" :style="{height: `${windowHeight}px`}">
    <view class="menu-list">
      <view class="list-cell list-cell-arrow" @click="handleToUpgrade">
        <view class="menu-item-box">
          <view class="iconfont icon-refresh menu-icon"></view>
          <view>检查更新</view>
        </view>
      </view>
      <view class="list-cell list-cell-arrow" @click="handleCleanTmp">
        <view class="menu-item-box">
          <view class="iconfont icon-clean menu-icon"></view>
          <view>清理缓存</view>
        </view>
      </view>
      <view class="list-cell list-cell-arrow" @click="shareApp">
        <view class="menu-item-box">
          <view class="iconfont icon-clean menu-icon"></view>
          <view>分享APP</view>
        </view>
      </view>
    </view>
    <view class="cu-list menu">
      <view class="cu-item item-box">
        <view class="content text-center" @click="handleLogout">
          <text class="text-black">退出登录</text>
        </view>
      </view>
    </view>

    <!-- 引入更新弹窗组件 -->
    <UpdatePopup
        :updateInfo="updateInfo"
        :showPopup="showUpdatePopup"
        @close="showUpdatePopup = false"
    />
  </view>
</template>

<script>
  import UpdatePopup from '@/components/update-popup/update-popup.vue';
  import {getLastAppVersion} from "@/api/app/appVersionControl";

  export default {
    components: {
      UpdatePopup
    },
    data() {
      return {
        showUpdatePopup: false,
        windowHeight: uni.getSystemInfoSync().windowHeight,
        updateInfo: {}
      }
    },
    methods: {

      shareApp(){
        this.$tab.navigateTo('/pages/mine/share/index')
      },
      handleToUpgrade() {
        // #ifdef APP-PLUS
        this.$modal.loading('检查更新中...');
        const systemInfo = uni.getSystemInfoSync();
        const requestObject = {
          appId: systemInfo.appId,
          version: systemInfo.appWgtVersion
        };
        try{
          getLastAppVersion(requestObject).then(response => {
            this.$modal.closeLoading();
            if (response && response.data) {
              this.updateInfo = response.data;
              this.showUpdatePopup = true
            }else{
              this.$modal.msg('当前是最新版本，无需更新')
            }
          })
        }catch(error){
          this.$modal.closeLoading();
        }

        // #endif

        // #ifndef APP-PLUS
          this.$modal.msg('当前是最新版本，无需更新');
        // #endif
      },

      /**
       * 格式化文件大小（字节转 KB/MB/GB）
       * @param {Number} size 字节数
       * @returns {String} 格式化后的大小
       */
      formatSize(size) {
        if (size < 1024) {
          return size + "B";
        } else if (size < 1024 * 1024) {
          return (size / 1024).toFixed(2) + "KB";
        } else if (size < 1024 * 1024 * 1024) {
          return (size / (1024 * 1024)).toFixed(2) + "MB";
        } else {
          return (size / (1024 * 1024 * 1024)).toFixed(2) + "GB";
        }
      },
      /**
       * 处理清理缓存逻辑
       */
      handleCleanTmp() {
        // 先判断是否为App环境
        if (uni.getSystemInfoSync().platform !== 'android') {
          this.$modal.msg('仅支持App端清理缓存', 'none');
          return;
        }

        // #ifdef APP-PLUS
        // 显示加载提示
        this.$modal.loading('计算缓存大小中...');
        
        // 计算缓存大小
        plus.cache.calculate((size) => {
          this.$modal.closeLoading();
          const cacheSize = this.formatSize(size);

          // 调用你原来的confirm方法（未优化版）
          this.$modal.confirm(`当前缓存大小：${cacheSize}\n确定要清除所有缓存吗？`, '清理缓存')
              .then((confirm) => {
                if (confirm) {
                  // 用户确认清除
                  this.$modal.loading('清除缓存中...');
                  plus.cache.clear(() => {
                    this.$modal.closeLoading();
                    this.$modal.msg('缓存清除成功');
                  }, (err) => {
                    this.$modal.closeLoading();
                    this.$modal.msg(`清除失败：${err.message}`, 'none');
                  });
                }
              });
        }, (err) => {
          this.$modal.closeLoading();
          this.$modal.msg(`计算缓存失败：${err.message}`, 'none');
        });
        // #endif
      },

      handleLogout() {
        this.$modal.confirm('确定注销并退出系统吗？').then(() => {
          this.$store.dispatch('LogOut').then(() => {}).finally(()=>{
            this.$tab.reLaunch('/pages/index')
          })
        })
      }
    }
  }
</script>

<style lang="scss" scoped>
  .page {
    background-color: #f8f8f8;
  }

  .item-box {
    background-color: #FFFFFF;
    margin: 30rpx;
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    padding: 10rpx;
    border-radius: 8rpx;
    color: #303133;
    font-size: 32rpx;
  }
</style>
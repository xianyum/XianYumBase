<template>
  <view class="page">
    <view class="menu-list">
      <view class="list-cell list-cell-arrow" @click="handleToUpgrade">
        <view class="menu-item-box">
          <view class="iconfont icon-refresh menu-icon"></view>
          <view>检查更新</view>
        </view>
      </view>
      <view class="list-cell list-cell-arrow" @click="handleForceUpgrade">
        <view class="menu-item-box">
          <view class="iconfont icon-refresh menu-icon"></view>
          <view>强制更新</view>
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

    <view class="logout-card" @click="handleLogout">
      <text class="logout-text">退出登录</text>
    </view>

    <UpdatePopup
        :updateInfo="updateInfo"
        :showPopup="showUpdatePopup"
        :forceInstall="forceInstall"
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
        updateInfo: {},
        forceInstall: false
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
              this.forceInstall = false;
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

      handleForceUpgrade() {
        // #ifdef APP-PLUS
        this._doForceUpgrade();
        // #endif

        // #ifndef APP-PLUS
        this.$modal.msg('仅支持App端强制更新');
        // #endif
      },

      _doForceUpgrade() {
        this.$modal.loading('获取最新版本信息...');
        const systemInfo = uni.getSystemInfoSync();
        getLastAppVersion({
          appId: systemInfo.appId,
          version: '0'
        }).then(response => {
          this.$modal.closeLoading();
          if (!response || !response.data) {
            this.$modal.msg('未获取到更新包');
            return;
          }
          this.updateInfo = response.data;
          this.forceInstall = true;
          this.showUpdatePopup = true;
        }).catch(() => {
          this.$modal.closeLoading();
          this.$modal.msg('获取更新失败');
        });
      },

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

      handleCleanTmp() {
        if (uni.getSystemInfoSync().platform !== 'android') {
          this.$modal.msg('仅支持App端清理缓存', 'none');
          return;
        }

        // #ifdef APP-PLUS
        this.$modal.loading('计算缓存大小中...');
        
        plus.cache.calculate((size) => {
          this.$modal.closeLoading();
          const cacheSize = this.formatSize(size);

          this.$modal.confirm(`当前缓存大小：${cacheSize}\n确定要清除所有缓存吗？`, '清理缓存')
              .then((confirm) => {
                if (confirm) {
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
    min-height: 100vh;
    background-color: #f8f8f8;
  }

  .logout-card {
    margin: 15px;
    background-color: #fff;
    border-radius: 8rpx;
    padding: 26rpx 30rpx;
    text-align: center;

    &:active {
      background-color: #f5f5f5;
    }
  }

  .logout-text {
    font-size: 30rpx;
    color: #e54d42;
  }
</style>
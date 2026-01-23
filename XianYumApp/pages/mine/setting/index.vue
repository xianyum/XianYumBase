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
      handleCleanTmp() {
        this.$modal.showToast('开发中...')
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

<template>
  <view class="work-container">
<!--    &lt;!&ndash; 基础模块 &ndash;&gt;-->
<!--    <view class="module-section">-->
<!--      <view class="module-header">-->
<!--        <text class="module-title">基础模块</text>-->
<!--      </view>-->
<!--      <view class="grid-section">-->
<!--        <view class="grid-item" v-for="(item, index) in systemMenus" :key="index" @tap="handleMenu(item)">-->
<!--          <view class="icon-box" :style="{ backgroundColor: item.bgColor }">-->
<!--            <uni-icons :type="item.icon" size="24" color="#fff"></uni-icons>-->
<!--          </view>-->
<!--          <text class="menu-name">{{ item.name }}</text>-->
<!--        </view>-->
<!--      </view>-->
<!--    </view>-->

    <!-- 新能源车辆 -->
    <view class="module-section">
      <view class="module-header">
        <text class="module-title">新能源车辆</text>
      </view>
      <view class="grid-section">
        <view class="grid-item" v-for="(item, index) in newEnergyMenus" :key="index" @tap="handleMenu(item)">
          <view class="icon-box" :style="{ backgroundColor: item.bgColor }">
            <uni-icons custom-prefix="iconfont" :type="item.icon" size="24" color="#fff"></uni-icons>
          </view>
          <text class="menu-name">{{ item.name }}</text>
        </view>
      </view>
    </view>

    <!-- 物联网模块 -->
    <view class="module-section">
      <view class="module-header">
        <text class="module-title">物联网</text>
      </view>
      <view class="grid-section">
        <view class="grid-item" v-for="(item, index) in toolMenus" :key="index" @tap="handleMenu(item)">
          <view class="icon-box" :style="{ backgroundColor: item.bgColor }">
            <uni-icons custom-prefix="iconfont" :type="item.icon" size="24" color="#fff"></uni-icons>
          </view>
          <text class="menu-name">{{ item.name }}</text>
        </view>
      </view>
    </view>

    <!-- 系统监控 -->
    <view class="module-section">
      <view class="module-header">
        <text class="module-title">系统监控</text>
      </view>
      <view class="grid-section">
        <view class="grid-item" v-for="(item, index) in monitorMenus" :key="index" @tap="handleMenu(item)">
          <view class="icon-box" :style="{ backgroundColor: item.bgColor }">
            <uni-icons custom-prefix="iconfont" :type="item.icon" size="24" color="#fff"></uni-icons>
          </view>
          <text class="menu-name">{{ item.name }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      // 系统管理菜单
      systemMenus: [
        { name: '用户管理', icon: 'person', bgColor: '#409eff', path: '/pages/system/user/index' },
        { name: '角色管理', icon: 'staff', bgColor: '#67c23a', path: '/pages/system/role/index' },
        { name: '菜单管理', icon: 'list', bgColor: '#e6a23c', path: '/pages/system/menu/index' },
        { name: '部门管理', icon: 'home', bgColor: '#f56c6c', path: '/pages/system/dept/index' },
        { name: '字典管理', icon: 'paperplane', bgColor: '#909399', path: '/pages/system/dict/index' }
      ],
      newEnergyMenus: [
        { name: '行驶记录', icon: 'icon-hangshijilu', bgColor: '#9c27b0', path: '/pages/monitor/online/index' },
        { name: '行驶报表', icon: 'icon-BIshujuzhongxin', bgColor: '#27b0a5', path: '/pages/monitor/online/index' }
      ],
      // 系统监控菜单
      monitorMenus: [
        { name: '在线用户', icon: 'icon-zaixianyonghu', bgColor: '#b08927', path: '/pages/monitor/user-online/index' },
        { name: '服务监控', icon: 'icon-zaixianyonghujiankong', bgColor: '#795548', path: '/pages/monitor/server/index' }
      ],
      // 物联网菜单
      toolMenus: [
        { name: '智能鱼缸', icon: 'icon-iot', bgColor: '#607d8b', path: '/pages/tool/test/index' }
      ]
    }
  },
  methods: {
    handleMenu(item) {
      uni.navigateTo({
        url: item.path
      })
    }
  }
}
</script>

<style lang="scss">
.work-container {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 20rpx;

  .module-section {
    margin-bottom: 30rpx;
    background-color: #fff;
    border-radius: 16rpx;
    padding: 30rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);

    .module-header {
      margin-bottom: 24rpx;

      .module-title {
        font-size: 32rpx;
        font-weight: 600;
        color: #2c3e50;
        display: block;
        margin-bottom: 8rpx;
      }

      .module-desc {
        font-size: 24rpx;
        color: #909399;
      }
    }

    .grid-section {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 20rpx;

      .grid-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 20rpx;

        .icon-box {
          width: 88rpx;
          height: 88rpx;
          border-radius: 22rpx;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-bottom: 12rpx;
          transition: all 0.3s;
          box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);

          &:active {
            transform: scale(0.92);
            box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.1);
          }
        }

        .menu-name {
          font-size: 26rpx;
          color: #606266;
        }
      }
    }
  }
}
</style>

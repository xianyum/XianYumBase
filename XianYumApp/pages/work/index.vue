<template>
  <view class="work-container">
    <!-- 动态渲染菜单模块 -->
    <view class="module-section" v-for="(module, moduleIndex) in dynamicMenuModules" :key="moduleIndex">
      <view class="module-header">
        <text class="module-title">{{ module.moduleTitle }}</text>
      </view>
      <view class="grid-section">
        <view
            class="grid-item"
            v-for="(item, index) in module.children"
            :key="index"
            @tap="handleMenu(item)"
        >
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
import { getRouters,reportMenuClick} from "@/api/system/menu/menu";

export default {
  data() {
    return {
      // 存储从后端获取的动态菜单模块
      dynamicMenuModules: [],
      // 默认背景色（可根据需要修改）
      defaultBgColor: '#8227b0'
    }
  },
  onLoad() {
    this.getMenus();
  },
  methods: {
    // 从后端获取菜单数据并格式化
    async getMenus() {
      const queryParams = { "platformType": "APP" };
      const res = await getRouters(queryParams);
      if (res.code === 200 && res.data ) {
        // 格式化菜单数据
        this.formatMenuData(res.data);
      }
    },

    // 格式化后端返回的菜单数据
    formatMenuData(rawData) {
      const formattedModules = [];

      rawData.forEach((module) => {
        // 过滤掉隐藏的模块
        if (module.hidden) return;
        // 格式化子菜单
        const formattedChildren = module.children?.map((child) => {
          // 过滤掉隐藏的子菜单
          if (child.hidden) return null;
          return {
            menuId: child.menuId,
            name: child.meta?.title || '',        // 菜单名称
            icon: child.meta?.icon || '',         // 图标类名
            path: child.component || '',          // 跳转路径
            // 使用后端返回的背景色，没有则使用默认色
            bgColor: child.iconBgColor || this.defaultBgColor
          };
        }).filter(Boolean);
        if (formattedChildren.length > 0) {
          formattedModules.push({
            moduleTitle: module.meta?.title || '未命名模块',
            children: formattedChildren
          });
        }
      });

      this.dynamicMenuModules = formattedModules;
    },
    // 菜单点击跳转
    async handleMenu(item) {
      if (!item.path) {
        return;
      }
      try {
        await reportMenuClick(item);
      } catch (error) {
        console.error('菜单埋点上报失败:', error);
      }
      uni.navigateTo({
        url: item.path
      });
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
---
name: "new-page"
description: "Enforces XianYumApp uni-app Vue3 page conventions. MUST be invoked before creating or modifying any page: onLoad(options), $setPageTitle, pages.json entry, Vue3-only APIs, no VUE3 conditionals."
---

# 新建页面规范（强制）

XianYumApp 是 uni-app + Vue3（HBuilderX 5.x / Vite）项目。**任何新建页面、改造页面、新增路由的操作，必须先读取并遵守本规范**，不得凭 Vue2 经验直接写代码。

## 一、固定步骤

1. **注册路由**：在 `pages.json` 的 `pages` 数组中追加页面配置，`style.navigationBarTitleText` 填默认标题；列表类页面按需开启 `enablePullDownRefresh`。
2. **创建页面**：`pages/<模块>/<页面>/index.vue`（详情页可用 `detail.vue` / `edit.vue`），风格为 Options API + `<style lang="scss" scoped>`。
3. **接口模块**：接口调用写到 `api/` 下对应模块，页面内通过 `@/api/...` 引入，不要在页面里直接写 `uni.request`。
4. **自检**：按文末清单逐项检查后再交付。

## 二、页面骨架（照抄此结构）

```vue
<template>
  <view class="page-container">
    <!-- 内容 -->
  </view>
</template>

<script>
import { listXxx } from '@/api/xxx/xxx'
import { checkPermi } from '@/utils/permission'

export default {
  data() {
    return {
      loading: false,
      list: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10
      }
    }
  },
  onLoad(options) {
    // 标题：优先取路由参数 pageTitle，页面固定标题则直接传字符串
    this.$setPageTitle(options.pageTitle || '默认标题')
    this.getList()
  },
  onShow() {},
  onPullDownRefresh() {
    this.queryParams.pageNum = 1
    this.getList().finally(() => uni.stopPullDownRefresh())
  },
  onReachBottom() {
    this.queryParams.pageNum++
    this.getList()
  },
  methods: {
    async getList() {
      this.loading = true
      try {
        const res = await listXxx(this.queryParams)
        // 处理数据
      } catch (e) {
        this.$modal.msg('加载失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.page-container {
  min-height: 100vh;
  background: #f5f5f5;
}
</style>
```

## 三、标题规范（强制）

- `onLoad` **必须显式接收 `options` 参数**，即使暂时不用；禁止写 `onLoad()` 后再引用 `options`（Vue3 不再注入该变量，会抛 `ReferenceError`）。
- 所有导航栏标题统一调用全局方法 `this.$setPageTitle(...)`，**禁止直接调用** `uni.setNavigationBarTitle`。
- 通过菜单跳转的页面，标题来自 `options.pageTitle`（URL 编码），`$setPageTitle` 内部已做解码，直接传原值即可。
- 页面有固定标题时传字符串，例如 `this.$setPageTitle('行程详情')`。

```js
onLoad(options) {
  this.$setPageTitle(options.pageTitle)   // 菜单跳转页面
  this.$setPageTitle('行程详情')           // 固定标题页面
}
```

## 四、Vue3 语法强制要求

禁止出现以下 Vue2 写法，发现即视为不合格：

| 禁止 | 替代写法 |
|---|---|
| `this.$set(obj, k, v)` / `this.$delete` | 直接赋值 `obj[k] = v` / `delete obj[k]` |
| `Vue.prototype.$xx` | `app.config.globalProperties.$xx`（在 `plugins/index.js` 注册） |
| `import Vue from 'vue'` / `new Vue()` | 不引入 Vue 构造器，使用 `createSSRApp`（入口已在 `main.js`） |
| `filters: {}` / `{{ a \| b }}` | 用 computed 或 method |
| `.native` 修饰符 | 组件需支持 `emits`，或改用 `@tap` |
| `slot-scope` / `scope="..."` | `v-slot` / `#default="..."` |
| `$children` | `ref` + `$refs` |
| `$listeners` | `$attrs` |
| `$scopedSlots` | `$slots` |
| `beforeDestroy` / `destroyed` | `beforeUnmount` / `unmounted` |
| `module.exports` / `require()` | `export default` / `import` |

另外：

- **不得再写 `#ifdef VUE3` / `#ifndef VUE3` / `#ifdef VUE2` 这类版本条件编译**；项目已固定 Vue3。
- 平台条件编译（`#ifdef H5`、`#ifdef APP-PLUS`、`#ifdef MP-WEIXIN` 等）可以正常使用。
- 修改 `uni_modules` 内第三方组件时，只能改业务必要逻辑，并保持纯 Vue3 写法；禁止引入 Vue2 分支。

## 五、全局方法（由 plugins/index.js 注册，直接 this 调用）

| 方法 | 用途 |
|---|---|
| `this.$tab.reLaunch / switchTab / navigateTo / redirectTo` | 页面跳转 |
| `this.$auth.hasPermi / hasRole` | 权限判断 |
| `this.$modal.msg / msgSuccess / msgError / confirm` | 提示与确认框 |
| `this.$setPageTitle(title)` | 设置导航栏标题（自动解码） |

## 六、列表页约定

- 分页参数统一 `pageNum` / `pageSize`，下拉刷新重置为第一页。
- 下拉刷新结束必须调用 `uni.stopPullDownRefresh()`，放在 `finally` 中。
- 权限按钮使用 `checkPermi(['system:xxx:save'])`（从 `@/utils/permission` 引入）。
- 错误提示使用 `this.$modal.msg(...)` 或 `showErrorToast`（`@/utils/common`），不要静默失败。
- 弹层/抽屉不要用 `v-if` 包裹包含 `movable-area` 的结构，改用 `v-show`（规避 App 端运行时崩溃）。

## 七、交付前自检清单

在项目根目录执行以下检查，结果必须为空（`vue3-upgrade-record.html` 等文档除外）：

1. 禁止 API：搜索 `$set(`、`$delete(`、`Vue.prototype`、`filters:`、`$scopedSlots`、`$children`、`beforeDestroy`、`destroyed(`
2. 禁止条件编译：搜索 `#ifdef VUE3`、`#ifndef VUE3`、`#ifdef VUE2`
3. 标题调用：搜索 `setNavigationBarTitle`，业务页面中除 `plugins/index.js` 外不应出现
4. `onLoad` 签名：确认全部为 `onLoad(options)`，且未引用未声明的 `options`
5. `pages.json` 已注册页面路径，标题与 `$setPageTitle` 一致
6. H5 与手机基座各跑一次目标页面，控制台无红色报错

## 八、参考范例

- 列表 + 下拉刷新 + 权限：`pages/system/apiKey/index.vue`
- 表单新增/编辑：`pages/system/apiKey/edit.vue`
- 图表页：`pages/ev-drive/report/index.vue`
- 详情页固定标题：`pages/ev-drive/trip/detail.vue`

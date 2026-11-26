import tab from './tab'
import auth from './auth'
import modal from './modal'

/**
 * 设置导航栏标题
 * @param {String} title 标题，为空时不设置；支持 URL 编码的标题
 */
const setPageTitle = (title) => {
  if (!title) return
  let text = title
  try {
    text = decodeURIComponent(title)
  } catch (e) {
    // 标题中不含合法编码时直接使用原值
  }
  uni.setNavigationBarTitle({ title: text })
}

export default {
  install(app) {
    // 页签操作
    app.config.globalProperties.$tab = tab
    // 认证对象
    app.config.globalProperties.$auth = auth
    // 模态框对象
    app.config.globalProperties.$modal = modal
    // 页面标题
    app.config.globalProperties.$setPageTitle = setPageTitle
  }
}

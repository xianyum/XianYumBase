import App from './App'
import store from './store' // store
import plugins from './plugins' // plugins
import './permission' // permission
import { getDicts } from "@/api/system/dict/data"
import { createSSRApp } from 'vue'

export function createApp() {
  const app = createSSRApp(App)
  app.use(store)
  app.use(plugins)
  // $store 由 Vuex 4 自动注入，无需手动挂载
  app.config.globalProperties.getDicts = getDicts
  return {
    app
  }
}

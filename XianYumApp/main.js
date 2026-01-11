import Vue from 'vue'
import App from './App'
import store from './store' // store
import plugins from './plugins' // plugins
import './permission' // permission
import { getDicts } from "@/api/system/dict/data"
import { showConfirm, showSuccessToast,showErrorToast } from '@/utils/common.js'

Vue.use(plugins)

Vue.config.productionTip = false
Vue.prototype.$store = store
Vue.prototype.getDicts = getDicts
Vue.prototype.$showConfirm = showConfirm
Vue.prototype.$showSuccessToast = showSuccessToast
Vue.prototype.$showErrorToast = showErrorToast

App.mpType = 'app'

const app = new Vue({
  ...App
})

app.$mount()

import Vue from 'vue'
import App from './App.vue'
import router from './router'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import './global.css';
import request from "@/utils/request";
import store from "@/store";
import Viewer from 'v-viewer'
import 'viewerjs/dist/viewer.css'
Vue.use(Viewer)
Viewer.setDefaults({
  // 需要配置的属性 注意属性并没有引号
  title: false,
  toolbar: false
})

Vue.use(ElementUI,{size:"small"});

Vue.config.productionTip = false

//引入config中的request对象
Vue.prototype.request=request;

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')

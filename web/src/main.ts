// import './assets/main.css'
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/reset.css';
// Surely Vue Table
import App from './App.vue'
import router from './router'

import "@/access"

const app = createApp(App)

app.use(createPinia())
app.use(router)
// 全局注册ant组件
app.use(Antd)


app.mount('#app')





<template>
  <div id="global-header">

    <a-row>
      <a-col flex="280px">
       <router-link to="/">
         <div class="title-bar">
           <img src="../assets/logo.png" alt="logo" class="logo" />
           <div class="title">弧图图 —— 智能图床</div>

         </div>
       </router-link>
      </a-col>
      <a-col flex="auto">
        <a-menu v-model:selectedKeys="current" mode="horizontal" :items="items"
        @click="doMenuClick"

        />

      </a-col>
      <a-col flex="200px">
        <div class="user-login-status">

          <div v-if="loginUserStore.loginUser.id">
            {{loginUserStore.loginUser.userName?? "无名"}}
          </div>
          <div v-else>

            <a-button type="primary"  href="/user/login">登录</a-button>

          </div>

        </div>
      </a-col>
    </a-row>




  </div>
</template>
<script lang="ts" setup>
import { h, ref } from 'vue'
import { MenuProps } from 'ant-design-vue'
import {  TagOutlined  } from '@ant-design/icons-vue';


const loginUserStore = useLoginUserStore()
loginUserStore.getLoginUser()



const current = ref<string[]>(['mail'])
const items = ref<MenuProps['items']>([
  {
    key: '/',
    title: '首页',
    label: '首页',
  },
  {
    key: '/about',
    title: '关于',
    label: '关于',
  },
  {
    key: 'others',
    title: 'BLOG',
    icon: ()=>h(TagOutlined),
    label: h('a', { href: 'https://varin.blog.csdn.net', target: '_blank' }, 'blog'),
  },
])

import {useRouter} from 'vue-router';
import { useLoginUserStore } from '@/store/userStore'
const router = useRouter();
// 路由跳转事件

const doMenuClick = ({key}:{key:string}) => {
router.push({
  path:key
})
}

// 解决刷新后菜单高亮失效
router.afterEach((to) => {
  current.value = [to.path]
})


</script>
<style scoped>
#global-header {
  margin:0 30px;
}

.title-bar {
  display: flex;
  align-items: center;

  .logo{
    height: 48px;
  }
  .title{
    color: #000;
    font-size: 18px;
    margin-left: 20px;


  }
}

</style>

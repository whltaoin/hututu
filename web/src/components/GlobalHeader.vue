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
        <a-menu
          v-model:selectedKeys="current"
          mode="horizontal"
          :items="items"
          @click="doMenuClick"
        />
      </a-col>
      <a-col flex="200px">
        <a-dropdown v-if="loginUserStore.loginUser.id">
          <a class="ant-dropdown-link" @click.prevent>
            <div class="user-login-status">
              <div>
                <a-avatar shape="square" size="large" :src="loginUserStore.loginUser.userAvatar" />

                {{ loginUserStore.loginUser.userName ?? '无名' }}
              </div>
              <div></div>
            </div>
          </a>
          <template #overlay>
            <a-menu style="max-width: 120px">
              <a-menu-item @click="doLoginOut">
                <LoginOutlined />
                <span style="margin-left: 10px">退出登录</span>
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
        <a-button type="primary" href="/user/login" v-else>登录</a-button>
      </a-col>
    </a-row>
  </div>
</template>
<script lang="ts" setup>
import { computed, h, onMounted, ref } from 'vue'
import { MenuProps, message } from 'ant-design-vue'
import { TagOutlined } from '@ant-design/icons-vue'
import { LoginOutlined } from '@ant-design/icons-vue'

// 获取用户信息
const loginUserStore = useLoginUserStore()
loginUserStore.getLoginUser()

const current = ref<string[]>(['mail'])
const originItems = ([
  {
    key: '/',
    title: '首页',
    label: '首页',
  },
  {
    key: '/admin/manage',
    title: '用户管理',
    label: '用户管理',
  },
  {
    key: '/add_picture',
    title: '创建图片',
    label: '创建图片',
  },
  {
    key: '/admin/pictureManage',
    title: '图片管理',
    label: '图片管理',
  },
  {
    key: 'others',
    title: 'BLOG',
    icon: () => h(TagOutlined),
    label: h('a', { href: 'https://varin.blog.csdn.net', target: '_blank' }, 'blog'),
  },
])

import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/store/userStore'
import { userLogoutUsingPost } from '@/api/UserController'
const router = useRouter()
// 路由跳转事件

const doMenuClick = ({ key }: { key: string }) => {
  router.push({
    path: key,
  })
}

// 解决刷新后菜单高亮失效
router.afterEach((to) => {
  current.value = [to.path]
})
const userLoginUser = useLoginUserStore()

// 退出登录
const doLoginOut = async () => {
  const res = await userLogoutUsingPost()
  if (res.status === 200 && res.data.data == true) {
    userLoginUser.setLoginUser({
      userName: '未登录',
    })
    message.success('退出登录成功')
    router.push({
      path: '/user/login',
      replace: true,
    })
  } else {
    message.error('操作失败：' + res.data.message)
  }
}




const items = computed<MenuProps['items']>(() =>{
  return originItems?.filter((item) => {
    if (item.key.startsWith('/admin')) {
      const loginUser = userLoginUser.loginUser
      if (!loginUser || loginUser.userRole !== 'admin') {
        return false
      }
    }
    return true
  })
})

console.log(items)




</script>
<style scoped>
#global-header {
  margin: 0 30px;
}

.title-bar {
  display: flex;
  align-items: center;

  .logo {
    height: 48px;
  }
  .title {
    color: #000;
    font-size: 18px;
    margin-left: 20px;
  }
}
</style>

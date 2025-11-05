import { createRouter, createWebHistory } from 'vue-router'
import PictureDetailPage from '@/page/picture/PictureDetailPage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [

    {
      path: '/',
      name: '首页',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/IndexView.vue'),
    },
    {
      path: '/about',
      name: '关于',

      component: () => import('../views/AboutView.vue'),
    },
    {
      path: '/user/login',
      name: '用户登录',

      component: () => import('../page/user/UserLoginPage.vue'),
    }
    ,
    {
      path: '/user/register',
      name: '用户注册',

      component: () => import('../page/user/UserRegisterPage.vue'),
    }
    , {
      path: '/admin/manage',
      name: '用户管理',

      component: () => import('../page/admin/UserManagePage.vue'),
    }

    , {
      path: '/add_picture',
      name: '创建图片',

      component: () => import('@/page/picture/AddPicturePage.vue'),
    } , {
      path: '/admin/pictureManage',
      name: '图片管理',

      component: () => import('@/page/admin/PictureManagePage.vue'),
    },
    {
      path: '/picture/:id',
      name: '图片详情',
      props: true,
      component: PictureDetailPage

    }
  ],
})

export default router

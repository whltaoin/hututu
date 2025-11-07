// 用户权限控制

// 首次获取到登录用户
import router from '@/router'
import { useLoginUserStore } from '@/store/userStore'
import { message } from 'ant-design-vue'

let firstgetLoginUser = true;


router.beforeEach(async (to, from, next) => {
   const userLoginUser = useLoginUserStore()
   let loginUser = userLoginUser.loginUser;
   //首次加载
   if(firstgetLoginUser){
     await userLoginUser.getLoginUser()
     loginUser = userLoginUser.loginUser
     firstgetLoginUser = false;
   }
   // 获取到路径
  const toUrl = to.fullPath
  if(toUrl.startsWith('/admin')){
    // 判断如果用户为空或者用户权限不是admin弹出
    if(!loginUser || loginUser.userRole !== 'admin'){
      message.error("您没有管理员权限")
      next(`/user/login?redirect=${toUrl}`)
      return
    }
  }
  next();

})


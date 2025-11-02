import {defineStore} from 'pinia'
import {ref} from 'vue'
import { getLoginUserUsingPost } from '@/api/UserController'
import LoginUserVo = API.LoginUserVo

export const useLoginUserStore = defineStore("loginUser",()=>{
  // 创建登录用户信息
  const  loginUser = ref<LoginUserVo>({
    userName :"未登录"
  })
  // 获取登录用户
  async  function getLoginUser(){
    // 后端接口没有开发，暂时用定时器模拟
    const  res = await getLoginUserUsingPost();
    if(res.data.code==200 && res.data.data){
      loginUser.value = res.data.data

    }
  }

  // 设置登录用户
  function setLoginUser(newLoginUser: any){
    loginUser.value = newLoginUser

  }

  return { loginUser ,setLoginUser  ,getLoginUser}
});


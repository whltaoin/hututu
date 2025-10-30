import {defineStore} from 'pinia'
import {ref} from 'vue'

export const useLoginUserStore = defineStore("loginUser",()=>{
  // 创建登录用户信息
  const  loginUser = ref<any>({
    userName :"未登录"
  })
  // 获取登录用户
  async  function getLoginUser(){
    // 后端接口没有开发，暂时用定时器模拟
    setTimeout(()=>{
      loginUser.value = {
        id:526,
        userName:"varin"
      }
    },10000)
  }

  // 设置登录用户
  function setLoginUser(newLoginUser: any){
    loginUser.value = newLoginUser

  }

  return { loginUser ,setLoginUser  ,getLoginUser}
});


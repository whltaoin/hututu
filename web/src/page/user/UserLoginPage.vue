<template>
  <div id="user-login">

    <h3 class="title">弧图图（Hututu）——用户登录</h3>
    <div class="description">企业级AI智能协同云图床</div>
    <a-form :model="formState" @finish="handleSubmit">
      <a-form-item
        name="userAccount"
        :rules="[{ required: true, message: '请输入用户名', trigger: 'blur' }]"
      >
        <a-input v-model:value="formState.userAccount"  placeholder="用户名"  />
      </a-form-item>

      <a-form-item name="userPassword" :rules="[{ required: true, message: '请输入用户密码' },{min:8,message: '密码不能小于8位'}]">
        <a-input-password v-model:value="formState.userPassword" placeholder="密码" />
      </a-form-item>
      <div style="text-align:right;margin-bottom: 20px">
        没有账号?  <router-link to="/user/register">
        去注册
      </router-link>

      </div>

      <a-form-item>
        <a-button type="primary" html-type="submit" style="min-width: 100%">登录</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>
<script lang="ts" setup>
import { reactive } from 'vue'
import UserLoginRequest = API.UserLoginRequest
import { userLoginUsingPost } from '@/api/UserController'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/store/userStore'



const formState = reactive<UserLoginRequest>({
  userAccount: '',
  userPassword: '',
})
const router = useRouter()
const loginUserStore = useLoginUserStore();
// 提交表单
const handleSubmit = async (values: UserLoginRequest) => {
  const res = await userLoginUsingPost(values)
    if(res.data.code == 200 && res.data.data){
      // 重新存储登录用户
     await loginUserStore.getLoginUser();
      message.success("登录成功")
      router.push({ path: '/',replace: true })

    }else{
      message.error('登录失败: '+ res.data.message)
    }
}


</script>

<style scoped>

#user-login {
  max-width: 360px;
  margin: 0 auto;
  .title{
    text-align: center;
    margin-bottom: 16px;

  }
  .description{
    font-size: 13px;
    color: #555;
    text-align: center;
    margin-bottom: 10px;
  }
}

</style>

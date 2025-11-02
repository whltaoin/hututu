<template>
  <div id="user-login">
    <h3 class="title">弧图图（Hututu）——用户注册</h3>
    <div class="description">企业级AI智能协同云图床</div>
    <a-form :model="formState" @finish="handleSubmit">
      <a-form-item
        name="userAccount"
        :rules="[{ required: true, message: '请输入用户名', trigger: 'blur' }]"
      >
        <a-input v-model:value="formState.userAccount" placeholder="用户名" />
      </a-form-item>

      <a-form-item
        name="userPassword"
        :rules="[
          { required: true, message: '请输入用户密码' },
          { min: 8, message: '密码不能小于8位' },
        ]"
      >
        <a-input-password v-model:value="formState.userPassword" placeholder="密码" />
      </a-form-item>
      <a-form-item
        name="checkPassword"
        :rules="[
          { required: true, message: '请输入用户密码' },
          { min: 8, message: '密码不能小于8位' },
        ]"
      >
        <a-input-password v-model:value="formState.checkPassword" placeholder="确认密码" />
      </a-form-item>
      <div style="text-align: right; margin-bottom: 20px">
       已有账号? <router-link to="/user/register"> 去登录 </router-link>
      </div>

      <a-form-item>
        <a-button type="primary" html-type="submit" style="min-width: 100%">注册</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>
<script lang="ts" setup>
import { reactive } from 'vue'
import { userLoginUsingPost, userRegisterUsingPost } from '@/api/UserController'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/store/userStore'
import UserRegisterRequest = API.UserRegisterRequest

const formState = reactive<UserRegisterRequest>({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
})
const router = useRouter()
// 提交表单
const handleSubmit = async (values: UserRegisterRequest) => {
  if (values.userPassword != values.checkPassword){
    message.error("密码不一致，请重新输入")
    return;
  }
  const res = await userRegisterUsingPost(values)
  if (res.data.code == 200 && res.data.data) {
    // 重新存储注册用户
    message.success('注册成功')
    router.push({ path: '/user/login', replace: true })
  } else {
    message.error('注册失败: ' + res.data.message)
  }
}
</script>

<style scoped>
#user-login {
  max-width: 360px;
  margin: 0 auto;
  .title {
    text-align: center;
    margin-bottom: 16px;
  }
  .description {
    font-size: 13px;
    color: #555;
    text-align: center;
    margin-bottom: 10px;
  }
}
</style>

<!--默认前端模版-->

<template>
  <div id="add-picture-batch">
    <div>
      <h2>批量创建图片</h2>
      <a-form layout="vertical" :model="formData" @finish="handleSubmit">
        <a-form-item label="关键词" name="searchText">
          <a-input v-model:value="formData.searchText" placeholder="请输入关键词" />
        </a-form-item>
        <a-form-item label="抓取数量" name="count">
          <a-input-number
            v-model:value="formData.count"
            placeholder="请输入数量"
            :min="1"
            :max="30"
            allow-clear
          />
        </a-form-item>
        <a-form-item label="名称前缀" name="namePrefix">
          <a-input
            v-model:value="formData.nameFrefix"
            placeholder="请输入名称前缀，会自动补充序号"
          />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit" :loading="loading"> 执行任务 </a-button>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { message } from 'ant-design-vue'
import { pictureUploadByBatchUsingPost } from '@/api/pictureController'
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const formData = reactive<API.PictureUploadByBatchRequest>({
  count: 10,
})
const loading = ref(false)
const router = useRouter()
const handleSubmit = async (values: any) => {
  loading.value = true
  const res = await pictureUploadByBatchUsingPost({
    ...formData, })

  if (res.data.code === 200 && res.data.data) {
    message.success(`创建成功，共 ${res.data.data} 条`)
    router.push({
      path: '/',
    })
  } else {
    message.error('创建失败，' + res.data.message)
  }
  loading.value = false
}
</script>

<style scoped>
#add-picture-batch {
}
</style>

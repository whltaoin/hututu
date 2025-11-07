<!--图片上传组件-->
<template>
  <div id="url-picture-upload">

    <div>
      <a-input-group compact style="margin-left:10%;">
        <a-input v-model:value="fileUrl" placeholder="请输入图片 URL"  style="max-width: 80%"  />
        <a-button type="primary" :loading="loading" @click="handleUpload">提交</a-button>
      </a-input-group>
      <img v-if="picture?.url" :src="picture?.url" alt="avatar" />
    </div>
  </div>

</template>
<script lang="ts" setup>
import { ref } from 'vue';
import {uploadByUrlUsingPost} from '@/api/PictureController'
import { message } from 'ant-design-vue';

const fileUrl = ref<string>()



// 参数校验



// 定义为父组件需要传参数和实现的方法
interface Props{
  picture:API.Picture;
  onSuccess?: (newPicure: API.PictureVo) => void;
}
const props = defineProps<Props>()

const loading = ref<boolean>(false);
// 上传图片
const handleUpload = async () => {
  loading.value = true
  try {
    const params: uploadByUrlUsingPOSTParams = { fileUrl: fileUrl.value }
    if (props.picture) {
      params.id = props.picture.id
    }
    console.log(params)


    const res = await uploadByUrlUsingPost(params)
    if (res.data.code === 200 && res.data.data) {
      message.success('图片上传成功')

      props.onSuccess?.(res.data.data)
    } else {
      message.error('图片上传失败，' + res.data.message)
    }
  } catch (error) {
    message.error('图片上传失败')
  } finally {
    loading.value = false
  }
}

</script>
<style scoped>


#url-picture-upload :deep(.ant-upload) {
  width: 100% !important;
  height: 100% !important;
  min-height: 152px;
  min-width: 152px;
}

#picture-upload img {
  max-width: 100%;
  max-height: 480px;
}

.ant-upload-select-picture-card i {
  font-size: 32px;
  color: #999;
}

.ant-upload-select-picture-card .ant-upload-text {
  margin-top: 8px;
  color: #666;
}


</style>

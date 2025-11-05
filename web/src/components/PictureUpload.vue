<!--图片上传组件-->
<template>
  <div id="picture-upload">
    <a-upload
      list-type="picture-card"
      :show-upload-list="false"
      :before-upload="beforeUpload"
      :custom-request="handleUpload"
    >
      <img v-if="picture?.url" :src="picture?.url" alt="avatar"  />
      <div v-else>
        <loading-outlined v-if="loading"></loading-outlined>
        <plus-outlined v-else></plus-outlined>
        <div class="ant-upload-text">点击或拖拽上传图片</div>
      </div>
    </a-upload>
  </div>
</template>
<script lang="ts" setup>
import { ref } from 'vue';
import { PlusOutlined, LoadingOutlined } from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import type {  UploadProps } from 'ant-design-vue';
import { uploadUsingPost1 } from '@/api/PictureController'





// 参数校验

const beforeUpload = (file: UploadProps['fileList'][number]) => {
  const isJpgOrPng = file.type === 'image/jpg' || file.type === 'image/png';
  if (!isJpgOrPng) {
    message.error('不支持上传该格式的图片，推荐 jpg 或 png');
  }
  const isLt2M = file.size / 1024 / 1024 < 2;
  if (!isLt2M) {
    message.error('图片大小不能超过2MB');
  }
  return isJpgOrPng && isLt2M;
};


// 定义为父组件需要传参数和实现的方法
interface Props{
  picture:API.Picture;
  onSuccess?: (newPicure: API.PictureVo) => void;
}
const props = defineProps<Props>()

const loading = ref<boolean>(false);
// 上传图片
const handleUpload = async ({file}:any) => {
  loading.value = true;
  try{
    const param = props.picture?{id:props.picture.id}:{}
    const res = await uploadUsingPost1(param,{},file);
    if(res.data.code == 200 && res.data.data){
      message.success("图片上传成功");
      props.onSuccess?.(res.data.data);
    }

  }catch( e){
    message.error("图片上传失败");
  }finally{
    loading.value = false;
  }
}
</script>
<style scoped>


#picture-upload :deep(.ant-upload) {
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

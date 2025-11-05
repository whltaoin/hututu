<!--默认前端模版-->

<template>
  <div id="picture-detail-page"></div>
</template>

<script setup lang="ts">

import { getPictureVoByIdUsingGet } from '@/api/PictureController'
import { onMounted, ref } from 'vue'
import { message } from 'ant-design-vue'

const props =defineProps<{id:String | number}>()
const picture = ref<API.PictureVO>({})


const fetchPictureDetail = async () => {
  try {
    const res = await getPictureVoByIdUsingGet({
      id: props.id,
    })
    if (res.data.code === 200 && res.data.data) {
      picture.value = res.data.data
      console.log(picture.value)
    } else {
      message.error('获取图片详情失败，' + res.data.message)
    }
  } catch (e: any) {
    message.error('获取图片详情失败：' + e.message)
  }
}

onMounted(() => {
  fetchPictureDetail()
})
</script>

<style scoped>
#picture-detail-page {
}
</style>

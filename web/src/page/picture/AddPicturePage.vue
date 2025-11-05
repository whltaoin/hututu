<!--默认前端模版-->

<template>
  <div id="add-picture-page">
    <div>
      <h2>
        {{ route.query?.id ? '修改图片' : '创建图片' }}
      </h2>      <PictureUpload :picture="picture" :on-success="onSuccess"></PictureUpload>
    </div>

    <div>
      <a-form v-if="picture" :model="pictureForm" @finish="handleSubmit" layout="vertical">
        <a-form-item name="name" label="名称">
          <a-input v-model:value="pictureForm.name" placeholder="请输入名称"></a-input>
        </a-form-item>

        <a-form-item label="简介" name="introduction">
          <a-textarea
            v-model:value="pictureForm.introduction"
            placeholder="请输入简介"
            :rows="2"
            :auto-size="{ minRows: 2, maxRows: 5 }"
            allowClear
          />
        </a-form-item>

        <a-form-item label="分类" name="category">
          <a-auto-complete
            v-model:value="pictureForm.category"
            :options="categoryOptions"
            placeholder="请输入分类"
            allowClear
          />
        </a-form-item>

        <a-form-item label="标签" name="tags">
          <a-select
            v-model:value="pictureForm.tags"
            :options="tagOptions"
            mode="tags"
            placeholder="请输入标签"
            allowClear
          />
        </a-form-item>

        <a-form-item>
          <a-button type="primary" html-type="submit" style="min-width: 100%">
            {{ route.query?.id ? '修改' : '创建   ' }}
          </a-button>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import PictureUpload from '@/components/PictureUpload.vue'
import { onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { editPictureUsingPost, getPictureByIdUsingGet, listPictureTagCategoryUsingGet } from '@/api/PictureController'
import { useRouter,useRoute } from 'vue-router'

const picture = ref<API.Picture>()
const onSuccess = (newPicture: API.PictureVo): void => {
  picture.value = newPicture
  pictureForm.name = newPicture.name
  console.log(pictureForm.name)
}
const pictureForm = reactive<API.PictureQueryRequest>({})

const router = useRouter()
const handleSubmit = async (values: any) => {
  const pictureId = picture.value?.id
  if (pictureId == undefined || pictureId == null) {
    message.error('请上传图片')
  }
  const res = await editPictureUsingPost({
    id: pictureId,
    ...values,
  })
  if (res.data.code == 200 && res.data.data) {
    message.success('创建图片成功！')
    router.push({
      path: `/picture/${pictureId}`,
    })
  } else {
    message.error('创建图片失败，请重试！')
  }
}
// 分类列表
const categoryOptions = ref<string[]>([])
// 标签列表
const tagOptions = ref<string[]>([])
// 获取分类列表和标签列表
const getTagCategoryOptions = async () => {
  const res = await listPictureTagCategoryUsingGet()
  if (res.data.code === 200 && res.data.data) {
    // 转格式，转为{value,label}
    tagOptions.value = (res.data.data.tagList ?? []).map((data: string) => {
      return {
        value: data,
        label: data,
      }
    })
    categoryOptions.value = (res.data.data.categoryList ?? []).map((data: string) => {
      return {
        value: data,
        label: data,
      }
    })
  } else {
    message.error('加载选项失败，' + res.data.message)
  }
}

onMounted(() => {

  getTagCategoryOptions()
  getOldPicture()
})
// 通过判断url是否存在id，存在id则是修改
const route = useRoute()
const getOldPicture = async () => {
  const id  = route.query?.id
  if(!id){
   return
  }
  const res = await getPictureByIdUsingGet({id})
  console.log(res)
  if (res.data.code == 200 && res.data.data) {
    const data = res.data.data
    picture.value = data
    pictureForm.name = data.name
    pictureForm.introduction = data.introduction
    pictureForm.category = data.category
    pictureForm.tags = data.tags
  }


}

</script>

<style scoped>
#add-picture-page {
  max-width: 720px;
  margin: 0 auto;
}
</style>

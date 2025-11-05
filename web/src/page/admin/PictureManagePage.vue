<template>
  <div id="picture-manage">
    <a-flex justify="space-between">
      <h2>图片管理</h2>
      <a-button type="primary" href="/add_picture" target="_blank">+ 创建图片</a-button>
    </a-flex>


    <!--   搜索-->
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="关键词" name="searchText">
        <a-input
          v-model:value="searchParams.searchText"
          placeholder="从名称和简介搜索"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="类型" name="category">
        <a-input v-model:value="searchParams.category" placeholder="请输入类型" allow-clear />
      </a-form-item>
      <a-form-item label="标签" name="tags">
        <a-select
          v-model:value="searchParams.tags"
          mode="tags"
          placeholder="请输入标签"
 style="width: 100px"
          allow-clear
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">搜索</a-button>

      </a-form-item>
      <a-form-item>
        <a-button
          @click="doReSearch"
          type="dashed" block
        >
          重置
        </a-button>
      </a-form-item>
    </a-form>
    <!--    表格-->
    <a-table :columns="columns" :data-source="dataList"
             :pagination="pagination"
             @change="doTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'url'" >
          <a-image :src="record.url" :width="120" />
        </template>
        <!-- 标签 -->
        <template v-if="column.dataIndex === 'tags'">
          <a-space wrap>
            <a-tag v-for="tag in JSON.parse(record.tags || '[]')" :key="tag">{{ tag }}</a-tag>
          </a-space>
        </template>
        <!-- 图片信息 -->
        <template v-if="column.dataIndex === 'picInfo'">
          <div>格式：{{ record.picFormat }}</div>
          <div>宽度：{{ record.picWidth }}</div>
          <div>高度：{{ record.picHeight }}</div>
          <div>宽高比：{{ record.picScale }}</div>
          <div>大小：{{ (record.picSize / 1024).toFixed(2) }}KB</div>
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.dataIndex === 'editTime'">
          {{ dayjs(record.editTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space>
            <a-button type="link" :href="`/add_picture?id=${record.id}`" target="_blank">编辑</a-button>
            <a-button type="link" danger @click="doDelete(record.id)">删除</a-button>
          </a-space>
        </template>
      </template>

    </a-table>
  </div>
</template>
<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { deleteUserByIdUsingDelete } from '@/api/UserController'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import { listPictureByPageUsingPost } from '@/api/PictureController'
import PictureQueryRequest = API.PictureQueryRequest
const total = ref(0)

const searchParams = reactive<PictureQueryRequest>({
  current: 1,
  pageSize: 10,
  sortField: 'createTime',
  sortOrder: 'descend',
  searchText:"",
  category:"",
  tags:[]

})
const dataList = ref([])

const fetchData = async () => {
  const res = await listPictureByPageUsingPost({
    ...searchParams,
  })
  if (res.data.data) {
    dataList.value = res.data.data.records ?? []
    total.value = res.data.data.total ?? 0
    console.log(dataList.value)
    console.log( total.value )
  } else {
    message.error('获取数据失败，' + res.data.message)
  }
}


// 页面加载时执行一次
onMounted(() => {
  fetchData()
})

const columns = [
  {
    title: 'id',
    dataIndex: 'id',
    width: 80,
  },
  {
    title: '图片',
    dataIndex: 'url',
  },
  {
    title: '名称',
    dataIndex: 'name',
  },
  {
    title: '简介',
    dataIndex: 'introduction',
    ellipsis: true,
  },
  {
    title: '类型',
    dataIndex: 'category',
  },
  {
    title: '标签',
    dataIndex: 'tags',
  },
  {
    title: '图片信息',
    dataIndex: 'picInfo',
  },
  {
    title: '用户 id',
    dataIndex: 'userId',
    width: 80,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '编辑时间',
    dataIndex: 'editTime',
  },
  {
    title: '操作',
    key: 'action',
  },
]

// 分页参数
const pagination = computed(() => {
  return {
    current: searchParams.current ?? 1,
    pageSize: searchParams.pageSize ?? 10,
    total:total.value,
    showSizeChanger: true,
    showTotal: (total) => `共 ${total} 条`,
  }
})
// 监听表格页数变化
const doTableChange = (page: any) => {
  searchParams.current = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}


// 搜索框搜索

const doSearch = () => {
  searchParams.current=1
  fetchData()

}

// 重置按钮
const doReSearch = () => {
  searchParams.current=1;
  searchParams.category=""

  searchParams.pageSize=10
  searchParams.searchText=""
  searchParams.tags=[]
  fetchData()
}

// 删除按钮
const doDelete = async (id:string) => {

  const res = await    deleteUserByIdUsingDelete({id})
  if (res.data.code === 200 || res.data.data) {
    message.success('删除成功')
    fetchData()
  }else{
    message.error("数据删除失败："+res.data.message)
  }
}
</script>

<style scoped>
#picture-manage {
  margin: 0 auto;
  background: #fff;
  padding: 10px;
}
</style>

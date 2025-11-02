<template>
  <div id="user-manage">

<!--    搜索框-->

    <div class="search-box" style="margin:10px;">

      <a-form
        layout="inline"
        :model="searchParams"
        @finish="doSearch"
      >
        <a-form-item label="用户账号">

          <a-input v-model:value="searchParams.userAccount">
          </a-input>
        </a-form-item>
        <a-form-item label="用户名">

          <a-input v-model:value="searchParams.userName">
          </a-input>
        </a-form-item>

        <a-form-item>
          <a-button
            type="primary"
            html-type="submit"
          >
            搜索
          </a-button>
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
    </div>

<!--    表格-->
    <a-table :columns="columns" :data-source="userVOList"
    :pagination="pagination"
             @change="doTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'userAccount'">
          <span>
            {{ record.userAccount }}
          </span>
        </template>

        <template v-else-if="column.dataIndex === 'userName'">
          <span>
            {{ record.userName }}
          </span>
        </template>

        <template v-else-if="column.dataIndex === 'userAvatar'">
          <a-image :width="50" :src="record.userAvatar" />
        </template>

        <template v-else-if="column.dataIndex === 'userRole'">
          <span v-if="record.userRole == 'admin'">
            <a-tag color="pink">管理员</a-tag>
          </span>
          <span v-if="record.userRole == 'user'">
            <a-tag color="green">普通用户</a-tag>
          </span>
        </template>

        <template v-else-if="column.dataIndex === 'createTime'">
          <span>
            {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
          </span>
        </template>

        <template v-else-if="column.key === 'action'">
          <a-button danger  @click="doDelete(record.id)">删除</a-button>
        </template>
      </template>
    </a-table>
  </div>
</template>
<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { deleteUserByIdUsingDelete, getUserByIdUsingPost } from '@/api/UserController'
import UserQueryRequest = API.UserQueryRequest
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
const userVOList = ref([])
const total = ref(0)

const searchParams = reactive<UserQueryRequest>({
  current: 1,
  pageSize: 5,
  userAccount:'',
  userName:''

})

const getUserVoList = async () => {
  const res = await getUserByIdUsingPost({ ...searchParams })
  if (res.data.code === 200 || res.data.data) {
    userVOList.value = res.data.data.records ?? []
    total.value = res.data.data.total ?? 0
  } else {
    message.error('获取数据失败：' + res.data.message)
  }
}
// 页面加载时执行一次
onMounted(() => {
  getUserVoList()
})
const columns = [
  {
    title: ' 用户账号',
    dataIndex: 'userAccount',
  },

  {
    title: ' 用户名',
    dataIndex: 'userName',
  },

  {
    title: '头像',
    dataIndex: 'userAvatar',
  },
  {
    title: '角色',
    dataIndex: 'userRole',
  },

  {
    title: '创建时间',
    dataIndex: 'createTime',
  },

  {
    title: '操作项',
    key: 'action',
  },
]
// 分页参数
const pagination = computed(() => {
  return {
    current:searchParams.current,
    pageSize : searchParams.pageSize,
    total: total.value,
    showSizeChanger: true,
    showTotal: (total)=> `共${total}条`,

  }

})
// 监听表格页数变化
const doTableChange = (page:any)=>{
  searchParams.pageSize = page.pageSize
  searchParams.current = page.current
  getUserVoList()
}

// 搜索框搜索

const doSearch = () => {
  searchParams.current=1
  getUserVoList()

}

// 重置按钮
const doReSearch = () => {
  searchParams.current=1;
  searchParams.userName=""
  searchParams.userAccount=""
  searchParams.pageSize=5
  getUserVoList()
}

// 删除按钮
  const doDelete = async (id:string) => {

    const res = await    deleteUserByIdUsingDelete({id})
    if (res.data.code === 200 || res.data.data) {
      message.success('删除成功')
      getUserVoList()
    }else{
      message.error("数据删除失败："+res.data.message)
    }
  }
</script>

<style scoped>
#user-manage {
  margin: 0 auto;
  background: #fff;
  padding: 10px;
}
</style>

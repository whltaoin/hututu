<template>
  <div id="app">
        <basic-layout></basic-layout>

  </div>

</template>
<script setup lang="ts">

import BasicLayout from '@/layouts/BasicLayout.vue'
// 测试后端接口
import {healthUsingGet} from "@/api/healthController"
import { useLoginUserStore } from '@/store/userStore'
healthUsingGet().then((res) => {
  console.log(res)
})

// 测试pinia



</script>
<style scoped>


#app{

}
</style>

<template>
  <div id="app">
        <basic-layout></basic-layout>

  </div>

</template>
<script setup lang="ts">

import BasicLayout from '@/layouts/BasicLayout.vue'

import {healthUsingGet} from "@/api/healthController"
healthUsingGet().then((res) => {
  console.log(res)
})
</script>
<style scoped>


#app{

}
</style>



<template>
  <div id="index-view">
    <h1>
      {{msg}}
    </h1>
<!--    测试组件中英文切换-->
        <a-date-picker />
        <a-time-picker />

  </div>
</template>
<script setup lang="ts">

const msg = "弧图图 -- AI智能打造的智能图床"
</script>
<style>

#index-view {

}
</style>

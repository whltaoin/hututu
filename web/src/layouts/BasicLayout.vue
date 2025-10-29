<!--默认前端模版-->

<template>
  <div id="basic-page">

    <a-layout style="min-width: 100vh">
      <a-layout-header class="headerStyle">

        <global-header></global-header>
      </a-layout-header>
      <a-layout-content class="contentStyle">
        <router-view></router-view>
      </a-layout-content>
      <a-layout-footer class="footerStyle">
        <a href="http:www.varin.cn" target="_blank">
          varin.cn By Varin
        </a>
      </a-layout-footer>
    </a-layout>

  </div>
</template>

<script setup lang="ts">


import GlobalHeader from '@/components/GlobalHeader.vue'
</script>

<style scoped>
#basic-page {
  width: 100%;
}

#basic-page .headerStyle  {
  padding-inline: 0px;
  margin-bottom: 16px;
  color:unset;
  background-color: white;
}
#basic-page .contentStyle{
  margin-bottom: 40px;
  padding:20px;
}

#basic-page .footerStyle{
  padding:16px;
  background-color: #efefef;
  position: fixed;
  bottom: 0;
  left:0;
  right:0;
  text-align: center;

}
</style>

<!--默认前端模版-->

<template>


  <div id="basic-page">

    <a-config-provider :locale="locale === 'en' ? enUS : zhCN">

    <a-layout style="min-width: 100vh">
      <a-layout-header class="headerStyle">

        <global-header></global-header>
      </a-layout-header>
      <a-layout-content class="contentStyle">
        <router-view></router-view>
      </a-layout-content>
      <a-layout-footer class="footerStyle">
        <div style="margin-bottom: 16px;text-align: right">
          <a-radio-group v-model:value="locale">
            <a-radio-button key="en" :value="enUS.locale">English</a-radio-button>
            <a-radio-button key="cn" :value="zhCN.locale">中文</a-radio-button>
          </a-radio-group>
        </div>
        <a href="http:www.varin.cn" target="_blank">
          varin.cn By Varin
        </a>
      </a-layout-footer>
    </a-layout>
      </a-config-provider>

  </div>
</template>

<script setup lang="ts">


import GlobalHeader from '@/components/GlobalHeader.vue'
import { ref, watch } from 'vue';
import enUS from 'ant-design-vue/es/locale/en_US';
import zhCN from 'ant-design-vue/es/locale/zh_CN';
import dayjs from 'dayjs';
import 'dayjs/locale/zh-cn';


dayjs.locale('en');
const locale = ref(enUS.locale);
watch(locale, val => {
  dayjs.locale(val);
});

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

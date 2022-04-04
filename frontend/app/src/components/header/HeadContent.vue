<script setup lang="ts">
import HeaderLeft from './HeaderLeft.vue';
import HeaderRight from "@/components/header/HeaderRight.vue";

const emit = defineEmits(['home'])

function home() {
  emit('home', null)
}
</script>

<template>
  <header>
    <HeaderLeft @home="home"/>
    <HeaderRight />
  </header>
</template>

<style scoped>
header {
  height: 100px;
  display: flex;
  flex-direction: row;
  flex-wrap: nowrap;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 0 10px black;
}
</style>

<script setup lang="ts">
import HeaderLeft from './HeaderLeft.vue';
import HeaderRight from "@/components/header/HeaderRight.vue";
</script>

<template>
  <header>
    <HeaderLeft />
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

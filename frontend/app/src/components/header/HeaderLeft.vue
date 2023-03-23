<template>
  <div id="headerLeft" @click="home">
    <font-awesome-icon :icon="['fas', 'flag']" class="fa-2x"></font-awesome-icon>
    <h1>{{ $t("title") }}</h1>
  </div>
</template>

<script setup>
import {router} from '@/main'

function home() {
  router.replace({path: "/"})
}
</script>

<style scoped>
#headerLeft {
  padding-left: 10px;
  display: flex;
  flex-direction: row;
  align-items: center;
  cursor: pointer;
}

#headerLeft > * {
  margin-left: 20px;
}
</style>
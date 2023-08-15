<template>
  <header>
    <HeaderLeft/>
    <HeaderRight/>
  </header>
  <LoadingBar/>
</template>


<script lang="ts" setup>
import HeaderLeft from "./HeaderLeft.vue"
import HeaderRight from "./HeaderRight.vue"
import LoadingBar from "./LoadingBar.vue"
</script>

<style scoped>
header {
  height: 70px;
  display: flex;
  flex-direction: row;
  flex-wrap: nowrap;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 0 10px black;
  position: sticky;
  left: 0;
  right: 0;
  top: 0;
  z-index: 10;
  background-color: white;
}
</style>

<script setup lang="ts">

</script>

<template>
  <div id="headerLeft">
    <font-awesome-icon :icon="['fas', 'flag']" class="fa-2x" ></font-awesome-icon>
    <h1>Turnierplaner</h1>
  </div>
</template>

<style scoped>
  #headerLeft {
    padding-left: 10px;
    display: flex;
    flex-direction: row;
    align-items: center;
  }

  #headerLeft > * {
    margin-left: 20px;
  }
</style>
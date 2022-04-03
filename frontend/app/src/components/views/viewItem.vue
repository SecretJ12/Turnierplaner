<script setup lang="ts">

const info = defineProps({
  title: String,
  description: String
})
</script>

<template>
  <div id="item">
    <h2>{{ title }}</h2>
    <p>{{ description }}</p>
  </div>
</template>

<style scoped>
  #item {
    border-radius: 10px;
    padding: 0 10px 0 10px;
    width: 400px;
    height: 300px;
    background-color: #D0D0D0;
  }

  h2 {
    text-align: center;
  }

  p {
    text-align: center;
  }

  #item:hover {
    box-shadow: 0 0 10px black;
  }
</style>
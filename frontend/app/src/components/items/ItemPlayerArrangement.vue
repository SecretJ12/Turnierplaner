<template>
  <div id="item" @click="selected">
    <h3>Player arrangement</h3>
  </div>
</template>

<script lang="ts" setup>
const emit = defineEmits(["selected"]);

function selected() {
  emit("selected");
}
</script>

<style scoped>
#item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  padding: 0 10px 0 10px;
  width: 400px;
  height: 300px;
  background-color: #e0e0e0;
}

#item:hover {
  box-shadow: 0 0 10px black;
  cursor: pointer;
}

#item:active {
  background-color: #d0d0d0;
}
</style>

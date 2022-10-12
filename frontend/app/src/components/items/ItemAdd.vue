<script setup>

const props = defineProps();

const emit = defineEmits(['selected']);

function selected() {
  emit('selected');
}
</script>

<template>
  <div id="item" @click="selected">
    <font-awesome-icon id="icon" :icon="['fas', 'plus']" class="fa-10x " ></font-awesome-icon>
  </div>
</template>

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
  background-color: #E0E0E0;
}

#icon {
  color: #C0C0C0;
}

#item:hover {
  box-shadow: 0 0 10px black;
  cursor: pointer;
}

#item:active {
  background-color: #C0C0C0;
}
</style>
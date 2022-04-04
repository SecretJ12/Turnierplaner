<script setup lang="ts">

const props = defineProps({
  idTour: String,
  title: String,
  description: String
})

const emit = defineEmits(['selected']);

function selected() {
  emit('selected', props.idTour);
}
</script>

<template>
  <div id="item" @click="selected">
    <h2>{{ props.title }}</h2>
    <p>{{ props.description }}</p>
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
    cursor: pointer;
  }

  #item:active {
    background-color: #C0C0C0;
  }
</style>
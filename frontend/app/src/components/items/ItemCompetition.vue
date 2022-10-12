<script setup>

const props = defineProps({
  name: String,
  description: String,
  type: String
});

const emit = defineEmits(['selected']);

function selected() {
  emit('selected', props.idComp);
}
</script>

<template>
  <div id="item" @click="selected">
    <h2>{{ name }}</h2>
    <p>{{ description }}</p>
    <div id="type">
      <font-awesome-icon v-if="type==='GROUPS'" :icon="['fas', 'table-cells-large']" class="fa-1x" ></font-awesome-icon>
      <font-awesome-icon v-else-if="type==='KNOCKOUT'" :icon="['fas', 'user-group']" class="fa-1x" ></font-awesome-icon>
    </div>
  </div>
</template>

<style scoped>
  #item {
    position: relative;
    border-radius: 10px;
    padding: 0 10px 0 10px;
    width: 400px;
    height: 300px;
    background-color: #D0D0D0;
  }

  #type {
    position: absolute;
    right: 0;
    margin-right: 15px;
    bottom: 0;
    margin-bottom: 15px;
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
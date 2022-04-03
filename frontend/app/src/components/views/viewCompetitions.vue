<script setup lang="ts">
import { ref } from 'vue';
import item from '@/components/views/viewItem.vue';

const tournaments = ref([
  {title: "Herren", description: "ganz viel Text"},
  {title: "Damen", description: "noch mehr Text"},
  {title: "U18", description: "richtig, noch mehr text"}
])

const emit = defineEmits(['clicked']);

function clicked() {
  emit("clicked", "title");
}
</script>

<template>
  <div id="tournaments">
    <item v-for="tournament in tournaments" :key="tournament.title" :title="tournament.title" :description="tournament.description" @clicked="clicked"/>
  </div>
</template>

<style scoped>
#tournaments {
  margin: 10px;
  display: flex;
  flex-wrap: wrap;
  flex-direction: row;
  justify-content: center;
}

#tournaments > * {
  margin: 0 10px 10px 10px;
}
</style>
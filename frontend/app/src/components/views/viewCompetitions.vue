<script setup lang="ts">
import { ref } from 'vue';
import item from '@/components/views/itemTournament.vue';

const tournaments = ref([
  {title: "Herren", description: "ganz viel Text"},
  {title: "Damen", description: "noch mehr Text"},
  {title: "U18", description: "richtig, noch mehr text"}
])

const props = defineProps({
  id: String
})

const emit = defineEmits(['clicked']);

function clicked() {
  emit("clicked", "title");
  console.log(props.id)
}
</script>

<template>
  <h2>{{props.id}}</h2>
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

h2 {
  text-align: center;
}
</style>
<script setup>
import {ref} from 'vue';
import Item from '../items/ItemTournament.vue';
import { router } from '/src/main'

const tournaments = ref([
  {id: "1", title: "2022", description: "ganz viel Text"},
  {id: "2", title: "2021", description: "noch mehr Text"},
  {id: "3", title: "2020", description: "richtig, noch mehr text"}
])

function selected(tournament) {
  router.push({path: '/tournaments/'+tournament})
}

</script>

<template>
  <div id="tournaments">
    <item v-for="tournament in tournaments" :key="tournament.title" :idTour="tournament.id" :title="tournament.title"
          :description="tournament.description" @selected="selected"/>
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

<script setup>
import {ref} from 'vue';
import Item from '../items/ItemTournament.vue';
import { router } from '/src/main'
import axios from "axios";

const tournaments = ref([])

axios.get('/tournaments')
    .then((response) => {
      console.log(response)
      tournaments.value = response.data
    })
    .catch((error) => {
      console.log(error)
    })

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

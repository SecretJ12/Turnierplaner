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
      tournaments.value.forEach(t => console.log(t.visible))
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
    <item v-for="tournament in tournaments" :key="tournament.name"
          :name="tournament.name" :description="tournament.description"
          :beginRegistration="new Date(tournament.beginRegistration)" :endRegistration="new Date(tournament.endRegistration)"
          :beginGamePhase="new Date(tournament.beginGamePhase)" :endGamePhase="new Date(tournament.endGamePhase)"
          :visible="tournament.visible"
          @selected="selected"/>
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

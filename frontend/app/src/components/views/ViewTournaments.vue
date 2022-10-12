<script setup>
import { ref } from 'vue';
import Item from '../items/ItemTournament.vue';
import AddItem from '../items/ItemAdd.vue';
import { router } from '/src/main'
import axios from "axios";
import { auth } from '/src/security/AuthService';

const tournaments = ref([])
const canCreate = ref(false)

axios.get('/tournament/list')
    .then((response) => {
      tournaments.value = response.data
    })
    .catch((error) => {
      console.log(error)
    })

auth.addUserUpdateListener(() => {
  canCreate.value = false;
  auth.getUser().then((user) => {
    if (user !== null) {
      axios.get('/tournament/canCreate')
          .then((response) => {
            canCreate.value = response.status === 200
          })
          .catch((error) => {
            console.log(error)
          })
    }
  });
});

function selected(tournament) {
  router.push({path: '/tournament/' + tournament})
}
</script>

<template>
  <div id="tournaments">
    <item v-for="tournament in tournaments" :key="tournament.name"
          :name="tournament.name" :description="tournament.description"
          :beginRegistration="new Date(tournament.beginRegistration)"
          :endRegistration="new Date(tournament.endRegistration)"
          :beginGamePhase="new Date(tournament.beginGamePhase)" :endGamePhase="new Date(tournament.endGamePhase)"
          :visible="tournament.visible"
          @selected="selected"/>
    <AddItem v-if="canCreate" />
  </div>
</template>

<style scoped>
#tournaments {
  width: 100%;
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

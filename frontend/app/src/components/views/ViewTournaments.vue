<script setup>
import {inject, ref, watch} from 'vue';
import Item from '../items/ItemTournament.vue';
import AddItem from '../items/ItemAdd.vue';
import { router } from '@/main'
import axios from "axios";
import { auth } from "@/security/AuthService"

const tournaments = ref([])

axios.get('/tournament/list')
    .then((response) => {
      tournaments.value = response.data
    })
    .catch((error) => {
      console.log(error)
    })

const isLoggedIn = inject('loggedIn', ref(false))
const canCreate = ref(false)

watch (isLoggedIn, async () => {
  checkCanCreate()
})
checkCanCreate()
function checkCanCreate() {
  canCreate.value = false
  auth.getUser().then((user) => {
    if (user !== null) {
      axios.get('/tournament/canCreate')
          .then((response) => {
            canCreate.value = response.status === 200
          })
          .catch((error) => {
          })
    }
  });
}
function selected(tournament) {
  router.push({path: '/tournament/' + tournament})
}
function addTournament() {
  router.push({path: '/createTournament'})
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
    <AddItem v-if="canCreate" @selected="addTournament"/>
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

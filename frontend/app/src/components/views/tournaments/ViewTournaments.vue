<template>
  <div id="tournaments">
    <item v-for="tournament in tournaments" :key="tournament.name"
          :beginGamePhase="new Date(tournament.beginGamePhase)" :beginRegistration="new Date(tournament.beginRegistration)"
          :canCreate=canCreate
          :description="tournament.description"
          :endGamePhase="new Date(tournament.endGamePhase)" :endRegistration="new Date(tournament.endRegistration)"
          :name="tournament.name"
          :visible="tournament.visible"
          @selected="selected"
          @settings="settingsItem"/>
    <AddItem v-if="canCreate" @selected="addTournament"/>
  </div>
</template>

<script setup>
import {inject, ref, watch} from 'vue';
import Item from '../../items/ItemTournament.vue';
import AddItem from '../../items/ItemAdd.vue';
import {i18n, router} from '@/main'
import axios from "axios";
import {auth} from "@/security/AuthService"
import {ElMessage} from "element-plus";

const tournaments = ref([])

const isLoggedIn = inject('loggedIn', ref(false))
const canCreate = ref(false)

watch(isLoggedIn, async () => {
  update()
})
update()

function update() {
  canCreate.value = false
  axios.get('/tournament/list')
      .then((response) => {
        tournaments.value = response.data
      })
      .catch((error) => {
        ElMessage.error(i18n.global.t("ViewTournaments.loadingFailed"))
        console.log(error)
      })
  auth.getUser().then((user) => {
    if (user !== null) {
      axios.get('/tournament/canCreate')
          .then((response) => {
            canCreate.value = response.status === 200
          })
          .catch((_) => {
            canCreate.value = false
          })
    }
  })
}

function selected(tournament) {
  router.push({path: '/tournament/' + tournament})
}

function settingsItem(tournament) {
  router.push({path: '/tournament/' + tournament + '/edit'})
}

function addTournament() {
  router.push({path: '/createTournament'})
}
</script>

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
  margin: 0 10px 20px 10px;
}
</style>

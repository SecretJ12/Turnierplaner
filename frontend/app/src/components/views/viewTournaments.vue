<script setup lang="ts">
import { ref } from 'vue';
import item from '@/components/views/itemTournament.vue';
import competition from "@/components/views/viewCompetitions.vue";
import Registration from "@/components/header/Registration.vue"

interface TournamentDetails {
  id: String,
  title: String,
  description: String
}

const tournaments = ref<Array<TournamentDetails>>([
  {id: "1", title: "2022", description: "ganz viel Text"},
  {id: "2", title: "2021", description: "noch mehr Text"},
  {id: "3", title: "2020", description: "richtig, noch mehr text"}
])
const curTournament = ref("");

function selected(tournament: string) {
  curTournament.value = tournament
}
</script>

<template>
  <div id="tournaments" v-if="curTournament === ''">
    <item v-for="tournament in tournaments" :key="tournament.title" :id="tournament.id" :title="tournament.title" :description="tournament.description" @selected="selected"/>
  </div>
  <div id="showCompetition" v-else>
    <competition :id="curTournament"/>
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

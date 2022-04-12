<script setup>
import { ref } from 'vue';
import Item from '../items/ItemCompetition.vue';
import { router } from '/src/main'
import { useRoute } from 'vue-router'

const competitions = ref([
  {idComp: "1", title: "Herren", description: "ganz viel Text"},
  {idComp: "2", title: "Damen", description: "noch mehr Text"},
  {idComp: "3", title: "U18", description: "richtig, noch mehr text"}
])

const route = useRoute()

function selected(competition) {
  router.push({path: "/tournaments/" + route.params.tourId + "/competitions/" + competition})
}
</script>

<template>
  <div>
    <h2>{{$route.params.tourId}}</h2>
    <div id="competitions">
      <item v-for="competition in competitions" :key="competition.title" :idComp="competition.idComp" :title="competition.title" :description="competition.description" @selected="selected"/>
    </div>
  </div>
</template>

<style scoped>
#competitions {
  margin: 10px;
  display: flex;
  flex-wrap: wrap;
  flex-direction: row;
  justify-content: center;
}

#competitions > * {
  margin: 0 10px 10px 10px;
}

h2 {
  text-align: center;
}
</style>
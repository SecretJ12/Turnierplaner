<script setup>
import { ref } from 'vue';
import Item from '../items/ItemCompetition.vue';
import { router } from '/src/main'
import { useRoute } from 'vue-router'
import axios from "axios";

const route = useRoute()

function selected(competition) {
  router.push({path: "/tournament/" + route.params.tourId + "/competition/" + competition})
}

const competitions = ref([])

axios.get(`/competition/list?tourName=${route.params.tourId}`)
    .then((response) => {
      competitions.value = response.data
    })
    .catch((error) => {
      console.log(error)
    })
</script>

<template>
  <div id="container">
    <h2>{{route.params.tourId}}</h2>
    <div id="competitions">
      <item v-for="competition in competitions"
            :name="competition.name"
            :description="competition.description"
            :type="competition.type"
            @selected="selected"/>
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

#container {
  width: 100%;
}

#competitions > * {
  margin: 0 10px 10px 10px;
}

h2 {
  text-align: center;
}
</style>
<template>
<div class="container text-center">
  <table class="table">
    <thead>
    <tr>
<!--      TOOD internalization-->
      <th scope="col">Begin Time </th>
      <th scope="col">Court</th>
      <th scope="col">Player 1</th>
      <th scope="col">Player 2</th>
      <th scope="col">Result</th>
    </tr>
    </thead>
    <tbody class="table-group-divider">
    <tr v-for="match in currentGroup.matches">
      <td>{{new Date(match.begin).toLocaleTimeString()}}</td>
      <td>{{match.court}}</td>
      <td>{{match.teamA}}</td>
      <td>{{match.teamB}}</td>
    </tr>
    </tbody>
  </table>
</div>
</template>

<script setup>
import {useRoute} from 'vue-router'
import axios from "axios"
import {reactive} from "vue"
import {useI18n} from "vue-i18n"
const { t } = useI18n({inheritLocale: true})

const route = useRoute()
const groupDetails = reactive({
  groups: []
})

const currentGroup = reactive({
  matches: []
})

const group = Number(route.params.groupId)

await axios.get(`tournament/${route.params.tourId}/competition/${route.params.compId}/groupMatches`)
    .then((response) => {
      groupDetails.groups = response.data.groups
      currentGroup.matches = groupDetails.groups[group].matches
    })
    .catch((_) => {
    })
</script>

<style scoped>

</style>
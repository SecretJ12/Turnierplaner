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
<!--  <div class="container text-center">-->
<!--    <ul class="list-group">-->
<!--      <li class="list-group-item active" aria-current="true">{{ $t("ViewGroupSystem.group") }} {{group}}</li>-->
<!--      <li class="list-group-item" v-for="match in currentGroup.matches">-->
<!--        {{match}}-->
<!--      </li>-->
<!--    </ul>-->
<!--  </div>-->
</template>

<script setup>

import {useRoute} from 'vue-router'
import axios from "axios";
import {i18n, router} from "@/main";
import {inject, reactive, ref, watch} from "vue";

const route = useRoute()
const groupDetails = reactive({
  groups: []
})

const currentGroup = reactive({
  matches: []
})

const group = Number(route.params.groupId)
console.log(group)

await axios.get(`tournament/${route.params.tourId}/competition/${route.params.compId}/groupMatches`)
    .then((response) => {
      groupDetails.groups = response.data.groups
      currentGroup.matches = groupDetails.groups[group].matches
      console.log(groupDetails.groups)
      console.log(currentGroup.matches)
    })
    .catch((_) => {
    })

console.log("something")

</script>

<style scoped>

</style>
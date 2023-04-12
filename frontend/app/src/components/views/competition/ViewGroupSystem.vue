<template>
  <div class="container text-center hover-effect"  v-for="(group,index_group) in groupDetails.groups">
    <table class="table" @click="selected(index_group)">
      <tr class="table-light">
        <th scope="col">
          {{ $t("ViewGroupSystem.group") }} {{ index_group + 1 }}
        </th>
        <th scope="col" v-for="(team) in group.teams" id="table-header">
          {{ teams.get(team).playerA.firstName }}, {{ teams.get(team).playerA.lastName }}
        </th>
      </tr>
      <tbody class="table-group-divider">
      <tr v-for="(team,index_t1) in group.teams">
        <th>{{ teams.get(team).playerA.firstName }}, {{ teams.get(team).playerA.lastName }}</th>
        <td v-for="(team,index_t2) in group.teams">
          <div v-if="index_t1===index_t2">
            /
          </div>
          <div v-else>
            tba
          </div>
        </td>
      </tr>
      </tbody>
      <caption v-if="progress[index_group]">
        {{ $t("ViewGroupSystem.progress") }}
      </caption>
      <caption v-else>
        {{ $t("ViewGroupSystem.finished") }}
      </caption>
    </table>
  </div>
</template>

<script setup>
import {useRoute} from 'vue-router'
import axios from "axios";
import {router} from "@/main";
import {reactive} from "vue";

const route = useRoute()
const groupDetails = reactive({
  groups: []
})
const teams = new Map()

await axios.get(`tournament/${route.params.tourId}/competition/${route.params.compId}/groupMatches`)
    .then((response) => {
      groupDetails.groups = response.data.groups
        response.data.teams.forEach(team => {
            teams.set(team.id, team)
        })
    })
    .catch((_) => {
    })

// TODO update progress array
const progress = [false, true]
function selected (group){
  router.push({path: `/tournament/${route.params.tourId}/competition/${route.params.compId}/${group}`})
}

</script>


<style lang="scss">

#grid {
  border: 1px solid #0a0a0a;
  border-radius: 2px;
  background: #fbffff;
}

#table-header {
  font-weight: normal;
}

.hover-effect:hover {
  opacity: 0.7;
}

</style>
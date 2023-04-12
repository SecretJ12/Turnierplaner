<template>
  <div class="container text-center" v-for="(group,index_group) in groupDetails.groups">
    <table class="table">
      <thead class="table-light">
      <th scope="col">
        {{ $t("ViewGroupSystem.group") }} {{ index_group +1}}
      </th>
      <th scope="col" v-for="(team) in group.teams" id="table-header"> {{ team.playerA.firstName }}
        {{ team.playerA.lastName }}
      </th>
      </thead>
      <tbody class="table-group-divider">
      <tr v-for="(team,index_t1) in group.teams">
        <th>{{ team.playerA.firstName }} {{ team.playerA.lastName }}</th>
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
import {i18n, router} from "@/main";
import {inject, reactive, ref, watch} from "vue";

const route = useRoute()
const groupDetails = reactive({
  groups: []
})

await axios.get(`tournament/${route.params.tourId}/competition/${route.params.compId}/groupMatches`)
    .then((response) => {
      groupDetails.groups = response.data.groups
      console.log(groupDetails.groups)
    })
    .catch((_) => {
    })

console.log(groupDetails.groups)

const progress = [false, true]

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

</style>
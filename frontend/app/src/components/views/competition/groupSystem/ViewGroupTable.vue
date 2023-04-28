<template>
  <div class="container">
    <table class="table">
      <tbody >
      <tr class="table-active">
        <th class="table-secondary">
          {{ t("ViewGroupSystem.group") }} {{ props.group.index }}
        </th>
        <td class="table-light text-center" v-for="team in props.group.teams" >
          {{ team.playerA.firstName }}, {{ team.playerA.lastName }}
        </td>
      </tr>
      <tr  v-for="(teamA,index_t1) in props.group.teams">
        <td class="table-active">{{ teamA.playerA.firstName }}, {{ teamA.playerA.lastName }} <br/> <i>TSV Wolnzach / BTV - {{route.params.compId}}</i></td>
        <td  class="text-center" v-for="(teamB,index_t2) in props.group.teams">
          <div v-if="index_t1===index_t2">
            /
          </div>
          <div v-else>
            <ViewMatch :match="findMatch(teamA, teamB)" :order="teamA.id===findMatch(teamA,teamB)?.teamA?.id"/>
          </div>
        </td>
      </tr>
      </tbody>
      <caption v-if="progress[props.group.index-1]">
        {{ $t("ViewGroupSystem.progress") }}
      </caption>
      <caption v-else>
        {{ $t("ViewGroupSystem.finished") }}
      </caption>
    </table>
  </div>
</template>

<script lang="ts" setup>
import {useRoute} from 'vue-router'
import {Group} from "@/interfaces/groupSystem"
import {useI18n} from "vue-i18n"
import {Match, Team} from "@/interfaces/match";
import ViewMatch from "@/components/views/competition/groupSystem/ViewMatch.vue";

const {t} = useI18n({inheritLocale: true})

const route = useRoute()
const props = defineProps<{
  group: Group
}>()


function findMatch(teamA: Team, teamB: Team): Match {
  const match: Match | undefined = props.group.matches.find(match => {
    if (match.teamA === null || match.teamB === null)
      return false
    if (match.teamA.id === teamA.id && match.teamB.id === teamB.id)
      return true
    if (match.teamA.id === teamB.id && match.teamB.id === teamA.id)
      return true
  })
  if (match === undefined) {
    console.error("Match does not exist")
    throw new Error("Match does not exist")
  }
  return match
}

// TODO update progress array
const progress = [false, true]
</script>

<style scoped>


</style>
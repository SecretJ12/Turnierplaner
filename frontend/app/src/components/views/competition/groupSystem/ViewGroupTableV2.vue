<template>
  <table style="width: calc(100% - 1px);">
    <tr>
      <th id="first">
        {{ t("ViewGroupSystem.group") }} {{ props.group.index }}
      </th>
      <template v-for="(team, index) in props.group.teams.slice().reverse()" :key="index">
        <th v-if="index !== props.group.teams.length-1" :class="hoverIdB === index ? 'highlight' : ''"
            @mouseover="headerHover(team.id)"
            @mouseleave="hoverLeave()"
        >
          {{ team.playerA.lastName }}, {{ team.playerA.firstName }}
        </th>
      </template>
    </tr>
    <template v-for="(teamA, indexA) in props.group.teams" :key="indexA">
      <tr v-if="indexA !== props.group.teams.length-1">
        <th :class="(hoverIdA === indexA ? 'highlight ' : '')"
            @mouseover="headerHover(teamA.id)"
            @mouseleave="hoverLeave()"
        >
          {{ teamA.playerA.lastName }}, {{ teamA.playerA.firstName }}
        </th>
        <template v-for="(teamB, indexB) in props.group.teams.slice().reverse()" :key="indexB">
          <td v-if="indexB !== props.group.teams.length-1 && (indexA + indexB < props.group.teams.length-1)"
              @mouseover="matchHover(indexA, indexB)"
              @mouseleave="hoverLeave()"
              :class="{
                highlightLow: (hoverIdA === indexA && indexB < hoverIdB) || (hoverIdB === indexB && indexA < hoverIdA),
                highlight: hoverTeam === teamA.id || hoverTeam === teamB.id
              }"
          >
            <div>
              <ViewMatch :match="findMatch(teamA, teamB)"/>
            </div>
          </td>
        </template>
      </tr>
    </template>
  </table>
</template>

<script setup lang="ts">
import {Group} from "@/interfaces/groupSystem"
import {ref} from "vue"
import {useI18n} from "vue-i18n"
import ViewMatch from "@/components/views/competition/groupSystem/ViewMatch.vue"
import {Match, Team} from "@/interfaces/match"

const {t} = useI18n({inheritLocale: true})

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

const hoverIdA = ref<null | number>(null)
const hoverIdB = ref<null | number>(null)
const hoverTeam = ref<null | string>(null)

function matchHover(indexA: number, indexB: number) {
	hoverIdA.value = indexA
	hoverIdB.value = indexB
}

function headerHover(team: string) {
	hoverTeam.value = team
}

function hoverLeave() {
	hoverIdA.value = null
	hoverIdB.value = null
	hoverTeam.value = null
}
</script>

<style scoped>
table {
  table-layout: fixed;
  text-align: center;
  border-spacing: 0;
  border-collapse: separate;
  border-top-left-radius: 15px;
  border-top: solid black 1px;
  border-left: solid black 1px;
}

#first {
  width: 150px;
  border-top-left-radius: 15px;
}

th, td {
  width: 1fr;
  height: 80px;
  border-bottom: solid black 1px;
  border-right: solid black 1px;
}

th {
  background-color: #F0F0F0;
}

td:hover {
  background-color: #DFDFDF;;
}

.highlight {
  background-color: #DFDFDF;;
}

.highlightLow {
  background-color: #F9F9F9;
}
</style>
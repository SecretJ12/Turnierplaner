<template>
  <table id="table" style="width: calc(100% - 1px);">
    <tr>
      <th id="first">
        {{ t("ViewGroupSystem.group") }} {{ props.group.index }}
      </th>
      <template v-for="(team, index) in props.group.teams.slice().reverse()">
        <th v-if="index !== props.group.teams.length-1" :class="hoverIdB === index ? 'highlight' : ''"
            @mouseover="headerHover(team.id)"
            @mouseleave="hoverLeave()"
          >
          {{ team.playerA.lastName }}, {{ team.playerA.firstName }}
        </th>
      </template>
    </tr>
    <template v-for="(teamA, indexA) in props.group.teams">
      <tr v-if="indexA !== props.group.teams.length-1">
        <th :class="(hoverIdA === indexA ? 'highlight ' : '')"
            @mouseover="headerHover(teamA.id)"
            @mouseleave="hoverLeave()"
          >
          {{ teamA.playerA.lastName }}, {{ teamA.playerA.firstName }}
        </th>
        <template v-for="(teamB, indexB) in props.group.teams.slice().reverse()">
          <td v-if="indexB !== props.group.teams.length-1 && (indexA + indexB < props.group.teams.length-1)"
              @mouseover="matchHover(indexA, indexB)"
              @mouseleave="hoverLeave()"
              :class="((hoverIdA === indexA && indexB < hoverIdB) ? 'highlightLow ' : '')
                      + ((hoverIdB === indexB && indexA < hoverIdA) ? 'highlightLow ' : '')
                      + ((hoverTeam === teamA.id || hoverTeam === teamB.id) ? 'highlight ' : '')"
            >
            <div>
              {{ indexA }} - {{ indexB }}
            </div>
          </td>
        </template>
      </tr>
    </template>
  </table>
</template>

<script setup lang="ts">
import {useRoute} from 'vue-router'
import {Group} from "@/interfaces/groupSystem"
import {ref} from "vue"
import {useI18n} from "vue-i18n"

const {t} = useI18n({inheritLocale: true})

const route = useRoute()
const props = defineProps<{
  group: Group
}>()

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
  border-collapse: collapse;
}

#first {
  width: 150px;
}

th, td {
  width: 1fr;
  height: 80px;
  border: solid black 1px;
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
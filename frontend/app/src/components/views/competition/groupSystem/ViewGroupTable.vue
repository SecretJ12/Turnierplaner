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
      <tr  v-for="(team,index_t1) in props.group.teams">
        <td class="table-active">{{ team.playerA.firstName }}, {{ team.playerA.lastName }} <br/> {{route.params.compId}}</td>
        <td  class="text-center" v-for="(_,index_t2) in props.group.teams">
          <div v-if="index_t1===index_t2">
            /
          </div>
          <div v-else>
            tba
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

const {t} = useI18n({inheritLocale: true})

const route = useRoute()
const props = defineProps<{
  group: Group
}>()

// TODO update progress array
const progress = [false, true]
</script>

<style scoped>


</style>
<template>
  <table style="width: calc(100% - 1px);">
    <tr>
      <th v-for="index in rangeArr(maxDeph)">
        test {{index}}
      </th>
    </tr>
    <tr v-for="indexR in rangeArr(Math.pow(2,maxDeph))">
      <template v-for="indexC in rangeArr(maxDeph)">
        <td v-if="isName(indexR, indexC)" class="topCell"
            :class="isBorder(indexR, indexC) ? 'borderCell' : ''"
        >
          name
        </td>
        <td v-else-if="isDescr(indexR, indexC)"
            :class="isBorder(indexR, indexC) ? 'borderCell' : ''"
        >
          descr
        </td>
        <td v-else
            :class="isBorder(indexR, indexC) ? 'borderCell' : ''"
        >

        </td>
      </template>
    </tr>
  </table>
</template>

<script setup lang="ts">
import {KnockoutMatch} from "@/interfaces/knockoutSystem";
import {rangeArr} from "element-plus";

const props = defineProps<{
  match: KnockoutMatch
}>()

const maxDeph = calcMaxDeph(props.match)

function calcMaxDeph(match: KnockoutMatch): number {
  if (match.prevMatch === undefined)
    return 1

  return 1 + Math.max(calcMaxDeph(match.prevMatch.a), calcMaxDeph(match.prevMatch.b))
}

function isName(indexR: number, indexC: number): boolean {
  return (indexR - (Math.pow(2, indexC)-1)) % (Math.pow(2, indexC+1)) === 0
}
function isDescr(indexR: number, indexC: number): boolean {
  return (indexR - (Math.pow(2, indexC)-1)) % (Math.pow(2, indexC+1)) === 1
}
function isBorder(indexR: number, indexC: number): boolean {
  return (indexR - (Math.pow(2, indexC)) + Math.pow(2, indexC+2))
      % (Math.pow(2, indexC+2)) < Math.pow(2, indexC+1)
    && indexC !== maxDeph-1
}
</script>

<style scoped>
td, th {
  //border: solid black 1px;
}

.topCell {
  border-bottom: solid black 1px;
}

.borderCell {
  border-right: solid black 1px;
}
</style>
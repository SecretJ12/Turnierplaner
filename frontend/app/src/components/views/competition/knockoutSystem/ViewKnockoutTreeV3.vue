<template>
  <el-scrollbar>
    <table>
      <tr>
        <template v-for="index in rangeArr(maxDepth)">
          <th>
            test {{ index }}
          </th>
          <template v-if="index < maxDepth-1">
            <th class="interCell">
            </th>
            <th class="interCell">
            </th>
          </template>
        </template>
      </tr>
      <tr v-for="indexR in rangeArr(4*teamsCount+2*(teamsCount-1)+1)">
        <template v-for="indexC in rangeArr(maxDepth)">
          <td v-if="matchColumnCellType(indexR, indexC) === cellType.match" rowspan="5" class="matchCol">
            <ViewMatchV3 :match="props.match" />
          </td>
          <td v-else-if="matchColumnCellType(indexR, indexC) === cellType.emptyCell">
          </td>

          <template v-if="indexC < maxDepth-1">
            <td v-if="interColumnCellType(indexR, indexC) === interCellType.topRight"
                class="topRightInterCell interCell">
            </td>
            <td v-else-if="interColumnCellType(indexR, indexC) === interCellType.bottomRight"
                class="bottomRightInterCell interCell">
            </td>
            <td v-else-if="interColumnCellType(indexR, indexC) === interCellType.right"
                class="rightInterCell interCell">
            </td>
            <td v-else-if="interColumnCellType(indexR, indexC) === interCellType.blank" class="interCell">
            </td>

            <td v-if="isBottomInterCell(indexR, indexC)" class="bottomInterCell interCell">
            </td>
            <td v-else class="interCell">
            </td>
          </template>
        </template>
      </tr>
    </table>
  </el-scrollbar>
</template>

<script setup lang="ts">
import {KnockoutMatch} from "@/interfaces/knockoutSystem";
import {rangeArr} from "element-plus";
import {useI18n} from "vue-i18n"
import ViewMatchV3 from "@/components/views/competition/knockoutSystem/ViewMatchV3.vue";

const {t} = useI18n({inheritLocale: true})

const props = defineProps<{
  match: KnockoutMatch
}>()

const maxDepth = calcMaxDepth(props.match)
const teamsCount = Math.pow(2, maxDepth - 1)
const height = Math.pow(2, maxDepth)

function matchColumnCellType(indexR: number, indexC: number): cellType {
  if (indexC === 0) {
    const mod = (indexR % 6)
    if (mod === 0)
      return cellType.match
    else if (mod < 5)
      return cellType.empty
    else
      return cellType.emptyCell
  }

  const countMatchesLeftTop = Math.pow(2, indexC - 1)
  const heightLeftTop = 4 * countMatchesLeftTop + 2 * (countMatchesLeftTop - 1) - 1

  const countMatchesLeft = Math.pow(2, indexC)
  const heightLeft = 4 * countMatchesLeft + 2 * (countMatchesLeft)

  if (indexR < heightLeftTop)
    return cellType.emptyCell
  const mod = ((indexR - heightLeftTop) % heightLeft)
  if (mod === 0)
    return cellType.match
  else if (mod < 5)
    return cellType.empty
  else
    return cellType.emptyCell
}

function interColumnCellType(indexR: number, indexC: number): interCellType {
  const countMatchesLeftTop = Math.pow(2, indexC)
  const heightLeftTop = 4 * countMatchesLeftTop + 2 * (countMatchesLeftTop - 1)

  const countMatchesLeft = Math.pow(2, indexC + 1)
  const heightLeft = 4 * countMatchesLeft + 2 * (countMatchesLeft - 1) - heightLeftTop

  const mod = ((indexR - (heightLeftTop / 2)) % (2 * heightLeft))
  if (indexR < heightLeftTop / 2)
    return interCellType.blank
  if (mod === 0)
    return interCellType.topRight
  if (mod === heightLeft - 1)
    return interCellType.bottomRight
  if (mod < heightLeft)
    return interCellType.right
  else
    return interCellType.blank
}

function isBottomInterCell(indexR: number, indexC: number): boolean {
  const countMatchesLeftTop = Math.pow(2, indexC)
  const heightLeftTop = 4 * countMatchesLeftTop + 2 * (countMatchesLeftTop - 1) - 1

  const countMatchesLeft = Math.pow(2, indexC + 1)
  const heightLeft = 4 * countMatchesLeft + 2 * (countMatchesLeft)

  if (indexR < heightLeftTop)
    return false
  const mod = ((indexR - heightLeftTop) % heightLeft)
  return mod === 1;
}

function calcMaxDepth(match: KnockoutMatch): number {
  if (match.prevMatch === undefined)
    return 1

  return 1 + Math.max(calcMaxDepth(match.prevMatch.a), calcMaxDepth(match.prevMatch.b))
}

enum cellType {
  match, empty, emptyCell
}

enum interCellType {
  topRight, bottomRight, right, blank
}
</script>

<style scoped>
table {
  table-layout: fixed;
  border-collapse: collapse;
}

th {
  text-align: center;
}

td {
  height: 20px;
  padding: 0;
}

.matchCol {
}

.topRightInterCell {
  border-top: solid black 2px;
  border-right: solid black 2px;
}

.bottomRightInterCell {
  border-right: solid black 2px;
  border-bottom: solid black 2px;
}

.rightInterCell {
  border-right: solid black 2px;
}

.bottomInterCell {
  border-bottom: solid black 2px;
}

.interCell {
  min-width: 25px;
}
</style>
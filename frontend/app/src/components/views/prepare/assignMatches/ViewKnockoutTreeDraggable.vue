<template>
	<div>
		<table
			:class="props.mode === Mode.SINGLE ? 'tableSingles' : 'tableDoubles'"
		>
			<tr>
				<template v-for="index in rangeArr(maxDepth)" :key="index">
					<th>
						{{ roundTitle(index + 1, maxDepth) }}
					</th>
					<template v-if="index < maxDepth - 1">
						<th class="interCell"></th>
						<th class="interCell"></th>
					</template>
				</template>
			</tr>
			<tr v-for="indexR in rangeArr(height)" :key="indexR">
				<template v-for="indexC in rangeArr(maxDepth)" :key="indexC">
					<td
						v-if="
							matchColumnCellType(indexR, indexC) === cellType.match &&
							indexC === 0
						"
						rowspan="5"
						class="matchCol"
					>
						<ViewMatchEdit
							:teamA="props.assignedTeams[indexR / 6][0]"
							:teamB="props.assignedTeams[indexR / 6][1]"
							:single="props.mode === Mode.SINGLE"
							:competition="props.competition"
						>
						</ViewMatchEdit>
						{{ indexR }} {{ indexC }}
					</td>
					<td
						v-else-if="matchColumnCellType(indexR, indexC) === cellType.match"
						rowspan="5"
						class="matchCol"
					>
						<ViewMatchViewArray
							:teamA="props.assignedTeams[Math.floor((indexR - 3) / 6)][0]"
							:teamB="props.assignedTeams[Math.floor((indexR - 3) / 6)][1]"
							:mode="props.mode"
							:single="props.mode === Mode.SINGLE"
							:sets="null"
							:winner="false"
							:finished="true"
						>
						</ViewMatchViewArray>
						{{ indexR }} {{ indexC }}
					</td>
					<td
						v-else-if="
							matchColumnCellType(indexR, indexC) === cellType.emptyCell
						"
					></td>
					<td
						v-else-if="
							matchColumnCellType(indexR, indexC) === cellType.thirdPlace
						"
						class="fixHeight"
					>
						{{ t("ViewKnockout.thirdPlace") }}
					</td>

					<template v-if="indexC < maxDepth - 1">
						<td
							v-if="
								interColumnCellType(indexR, indexC) === interCellType.topRight
							"
							class="topRightInterCell interCell"
						></td>
						<td
							v-else-if="
								interColumnCellType(indexR, indexC) ===
								interCellType.bottomRight
							"
							class="bottomRightInterCell interCell"
						></td>
						<td
							v-else-if="
								interColumnCellType(indexR, indexC) === interCellType.right
							"
							class="rightInterCell interCell"
						></td>
						<td
							v-else-if="
								interColumnCellType(indexR, indexC) === interCellType.blank
							"
							class="interCell"
						></td>

						<td
							v-if="isBottomInterCell(indexR, indexC)"
							class="bottomInterCell interCell"
						></td>
						<td v-else class="interCell"></td>
					</template>
				</template>
			</tr>
		</table>
	</div>
</template>

<script setup lang="ts">
import { KnockoutMatch } from "@/interfaces/knockoutSystem"
import { useI18n } from "vue-i18n"
import ViewMatchEdit from "@/components/views/competition/knockoutSystem/ViewMatchEdit.vue"
import ViewMatchViewArray from "@/components/views/competition/knockoutSystem/ViewMatchViewArray.vue"
import { Competition, Mode } from "@/interfaces/competition"
import { computed } from "vue"
import { Team } from "@/interfaces/team"

const { t } = useI18n({ inheritLocale: true })

const props = defineProps<{
	depth: number
	// match: KnockoutMatch
	thirdPlace: KnockoutMatch
	mode: Mode
	competition: Competition
	assignedTeams: Team[][][]
}>()

const maxDepth = computed(() => props.depth)
const teamsCount = computed(() => Math.pow(2, maxDepth.value))
const height = computed(() => tableHeight())

console.log("maxDepth", maxDepth.value)

function tableHeight(): number {
	return 2 * teamsCount.value + 2 * (teamsCount.value / 2 - 1) + 1
}

function roundTitle(round: number, totalRounds: number): string {
	if (round === totalRounds) return t("ViewKnockout.finale")
	else if (round === totalRounds - 1) return t("ViewKnockout.semifinals")
	else if (round === totalRounds - 2) return t("ViewKnockout.quarterfinals")
	else return t("ViewKnockout.round") + " " + round
}

function matchColumnCellType(indexR: number, indexC: number): cellType {
	// if (teamsCount.value === 3 || teamsCount.value === 4) {
	// 	if (indexC === 0) {
	// 		if (indexR === 0) return cellType.match
	// 		else if (indexR < 5) return cellType.empty
	// 		else if (indexR < 12) return cellType.emptyCell
	// 		else if (indexR === 12) return cellType.match
	// 		else return cellType.empty
	// 	} else {
	// 		if (indexR === 3) return cellType.match
	// 		else if (indexR < 11) return cellType.empty
	// 		else if (indexR === 11) return cellType.thirdPlace
	// 		else if (indexR === 12) return cellType.match
	// 		else return cellType.empty
	// 	}
	// }

	if (indexC === 0) {
		const mod = indexR % 6
		if (mod === 0) return cellType.match
		else if (mod < 5) return cellType.empty
		else return cellType.emptyCell
	}

	const countMatchesLeftTop = Math.pow(2, indexC - 1)
	const heightLeftTop =
		4 * countMatchesLeftTop + 2 * (countMatchesLeftTop - 1) - 1

	const countMatchesLeft = Math.pow(2, indexC)
	const heightLeft = 4 * countMatchesLeft + 2 * countMatchesLeft

	if (indexR < heightLeftTop) return cellType.emptyCell
	const mod = (indexR - heightLeftTop) % heightLeft
	if (mod === 0) return cellType.match
	else if (mod < 5) return cellType.empty
	else {
		if (indexC < maxDepth.value - 1) return cellType.emptyCell
		else {
			if (mod === 5) return cellType.emptyCell
			else if (mod === 6) return cellType.thirdPlace
			else if (mod === 7) return cellType.match
			else if (mod < 11) return cellType.empty
			else return cellType.emptyCell
		}
	}
}

function interColumnCellType(indexR: number, indexC: number): interCellType {
	// if (teamsCount.value === 3 || teamsCount.value === 4) {
	// 	if (indexR < 2) return interCellType.blank
	// 	else if (indexR === 2) return interCellType.topRight
	// 	else if (indexR < 13) return interCellType.right
	// 	else if (indexR === 13) return interCellType.bottomRight
	// 	else return interCellType.blank
	// }

	const countMatchesLeftTop = Math.pow(2, indexC)
	const heightLeftTop = 4 * countMatchesLeftTop + 2 * (countMatchesLeftTop - 1)

	const countMatchesLeft = Math.pow(2, indexC + 1)
	const heightLeft =
		4 * countMatchesLeft + 2 * (countMatchesLeft - 1) - heightLeftTop

	const mod = (indexR - heightLeftTop / 2) % (2 * heightLeft)
	if (indexR < heightLeftTop / 2) return interCellType.blank
	if (mod === 0) return interCellType.topRight
	if (mod === heightLeft - 1) return interCellType.bottomRight
	if (mod < heightLeft) return interCellType.right
	else return interCellType.blank
}

function isBottomInterCell(indexR: number, indexC: number): boolean {
	// if (teamsCount.value === 3 || teamsCount.value === 4) {
	// 	return indexR === 7
	// }

	const countMatchesLeftTop = Math.pow(2, indexC)
	const heightLeftTop =
		4 * countMatchesLeftTop + 2 * (countMatchesLeftTop - 1) - 1

	const countMatchesLeft = Math.pow(2, indexC + 1)
	const heightLeft = 4 * countMatchesLeft + 2 * countMatchesLeft

	if (indexR < heightLeftTop) return false
	const mod = (indexR - heightLeftTop) % heightLeft
	return mod === 1
}

function calcMaxDepth(match: KnockoutMatch): number {
	if (match.prevMatch === undefined) return 1

	return (
		1 +
		Math.max(calcMaxDepth(match.prevMatch.a), calcMaxDepth(match.prevMatch.b))
	)
}

enum cellType {
	match,
	empty,
	emptyCell,
	thirdPlace,
}

enum interCellType {
	topRight,
	bottomRight,
	right,
	blank,
}

function rangeArr(r: number) {
	return Array.from({ length: r }, (value, index) => index)
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
	padding: 0;
}

.tableSingles > tr > td {
	height: 20px;
	font-size: 13px;
}

.tableDoubles > tr > td {
	height: 28px;
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

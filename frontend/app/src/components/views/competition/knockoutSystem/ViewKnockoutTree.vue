<template>
	<table style="width: calc(100% - 1px)">
		<tr>
			<th v-for="index in rangeArr(maxDepth)" :key="index">test {{ index }}</th>
		</tr>
		<tr v-for="indexR in rangeArr(height)" :key="indexR">
			<template v-for="indexC in rangeArr(maxDepth)" :key="indexC">
				<td
					v-if="isName(indexR, indexC)"
					class="topCell"
					:class="isBorder(indexR, indexC) ? 'borderCell' : ''"
				>
					{{ getTop(indexR, indexC) }}
				</td>
				<td
					v-else-if="isDescr(indexR, indexC)"
					:class="isBorder(indexR, indexC) ? 'borderCell' : ''"
				>
					{{ getBottom(indexR, indexC) }}
				</td>
				<td v-else :class="isBorder(indexR, indexC) ? 'borderCell' : ''"></td>
			</template>
		</tr>
	</table>
</template>

<script setup lang="ts">
import { KnockoutMatch } from "@/interfaces/knockoutSystem"
import { rangeArr } from "element-plus"
import { useI18n } from "vue-i18n"

const { t } = useI18n({ inheritLocale: true })

const props = defineProps<{
	match: KnockoutMatch
}>()

const maxDepth = calcMaxDepth(props.match)
const height = Math.pow(2, maxDepth)

function getTop(indexR: number, indexC: number): string {
	let curMatch = props.match
	let curHeight = height
	for (let i = 0; i < maxDepth - indexC - 1; i++) {
		curHeight /= 2
		if (curMatch.prevMatch === undefined) return "Error"
		if (indexR < curHeight) curMatch = curMatch.prevMatch.a
		else {
			curMatch = curMatch.prevMatch.b
			indexR -= curHeight
		}
	}

	if (curMatch.teamA != null) return curMatch.teamA.playerA.firstName
	else return curMatch.begin.toLocaleString(t("lang"), dateOptions)
}

function getBottom(indexR: number, indexC: number): string {
	let curMatch = props.match
	let curHeight = height
	for (let i = 0; i < maxDepth - indexC - 1; i++) {
		curHeight /= 2
		if (curMatch.prevMatch === undefined) return "Error"
		if (indexR < curHeight) curMatch = curMatch.prevMatch.a
		else {
			curMatch = curMatch.prevMatch.b
			indexR -= curHeight
		}
	}

	if (curMatch.finished) return "result"
	else return "" // TODO maybe player details
}

function calcMaxDepth(match: KnockoutMatch): number {
	if (match.prevMatch === undefined) return 1

	return (
		1 +
		Math.max(calcMaxDepth(match.prevMatch.a), calcMaxDepth(match.prevMatch.b))
	)
}

// style functions
function isName(indexR: number, indexC: number): boolean {
	return (indexR - (Math.pow(2, indexC) - 1)) % Math.pow(2, indexC + 1) === 0
}

function isDescr(indexR: number, indexC: number): boolean {
	return (indexR - (Math.pow(2, indexC) - 1)) % Math.pow(2, indexC + 1) === 1
}

function isBorder(indexR: number, indexC: number): boolean {
	return (
		(indexR - Math.pow(2, indexC) + Math.pow(2, indexC + 2)) %
			Math.pow(2, indexC + 2) <
			Math.pow(2, indexC + 1) && indexC !== maxDepth - 1
	)
}

const dateOptions: Intl.DateTimeFormatOptions = {
	year: "2-digit",
	month: "numeric",
	day: "numeric",
	hour: "numeric",
	minute: "numeric",
}
</script>

<style scoped>
table {
	table-layout: fixed;
}

td,
th {
}

.topCell {
	border-bottom: solid black 1px;
}

.borderCell {
	border-right: solid black 1px;
}
</style>

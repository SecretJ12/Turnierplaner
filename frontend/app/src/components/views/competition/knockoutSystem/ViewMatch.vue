<template>
	<table
		:class="{
			tableSingles: props.mode === Mode.SINGLE,
			tableDoubles: props.mode === Mode.DOUBLE,
		}"
	>
		<tr :class="{ winner: props.match.finished && !props.match.winner }">
			<td class="name">
				{{
					props.match.teamA?.playerA
						? `${props.match.teamA.playerA.lastName}, ${props.match.teamA.playerA.firstName}`
						: ""
				}}
				<template v-if="props.mode === Mode.DOUBLE">
					<br />
					{{
						props.match.teamA?.playerB
							? `${props.match.teamA.playerB.lastName}, ${props.match.teamA.playerB.firstName}`
							: ""
					}}
				</template>
			</td>

			<template v-if="props.match.sets !== null">
				<td v-for="set in props.match.sets" :key="set.index" class="result">
					{{ set.scoreA }}
				</td>
			</template>
		</tr>
		<tr :class="{ winner: props.match.finished && props.match.winner }">
			<td class="name">
				{{
					props.match.teamB?.playerA
						? `${props.match.teamB.playerA.lastName}, ${props.match.teamB.playerA.firstName}`
						: ""
				}}
				<template v-if="props.mode === Mode.DOUBLE">
					<br />
					{{
						props.match.teamB?.playerB
							? `${props.match.teamB.playerB.lastName}, ${props.match.teamB.playerB.firstName}`
							: ""
					}}
				</template>
			</td>

			<template v-if="props.match.sets !== null">
				<td v-for="set in props.match.sets" :key="set.index" class="result">
					{{ set.scoreB }}
				</td>
			</template>
		</tr>
	</table>
	<div
		id="date"
		:class="{
			dateSingles: props.mode === Mode.SINGLE,
			dateDoubles: props.mode === Mode.DOUBLE,
		}"
	>
		{{ props.match.begin?.toLocaleString(t("lang"), dateOptions) || "" }}
	</div>
</template>

<script setup lang="ts">
import { KnockoutMatch } from "@/interfaces/knockoutSystem"
import { useI18n } from "vue-i18n"
import { Mode } from "@/interfaces/competition"

const { t } = useI18n({ inheritLocale: true })

const props = defineProps<{
	match: KnockoutMatch
	mode: Mode
}>()

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
	width: 300px;
	background-color: #e0e0e0;
	margin: 0;
	border: solid black 2px;
}

.tableSingles {
	height: 80px;
}

.tableDoubles {
	height: 108px;
}

table > tr {
	height: 50%;
}

.winner {
	background-color: #c0c0c0;
}

tr {
	border: solid black 2px;
}

td {
	border: solid black 1px;
}

.name {
	text-align: left;
	padding-left: 20px;
}

#date {
	text-align: right;
	padding-right: 20px;
	font-style: italic;
}

.dateSingles {
	height: 20px;
}

.dateDoubles {
	height: 28px;
}

.result {
	text-align: center;
	width: 25px;
	background-color: #fdfdfd;
}

.winner > .result {
	background-color: #e0e0e0;
}
</style>

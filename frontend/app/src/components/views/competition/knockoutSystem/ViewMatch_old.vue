<template>
	<table
		:class="{
			tableSingles: props.single,
			tableDoubles: !props.single,
		}"
	>
		<tr :class="{ winner: props.finished && !props.winner }">
			<td class="name">
				<slot name="teamA-name"></slot>
			</td>
			<template v-if="props.sets !== null">
				<td v-for="set in props.sets" :key="set.index" class="result">
					{{ set.scoreA }}
				</td>
			</template>
		</tr>
		<tr :class="{ winner: props.finished && props.winner }">
			<td class="name">
				<slot name="teamB-name" />
			</td>

			<template v-if="props.sets !== null">
				<td v-for="set in props.sets" :key="set.index" class="result">
					{{ set.scoreB }}
				</td>
			</template>
		</tr>
	</table>
</template>

<script setup lang="ts">
import { Set } from "@/interfaces/match"

const props = defineProps<{
	sets: Array<Set> | null
	single: boolean
	finished: boolean
	winner: boolean | null
}>()
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

.result {
	text-align: center;
	width: 25px;
	background-color: #fdfdfd;
}

.winner > .result {
	background-color: #e0e0e0;
}
</style>

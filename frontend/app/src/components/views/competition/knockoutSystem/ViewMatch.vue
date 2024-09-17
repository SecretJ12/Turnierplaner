<template>
	<table
		:class="{
			tableSingles: props.mode === Mode.SINGLE,
			tableDoubles: props.mode === Mode.DOUBLE,
		}"
	>
		<tr :class="{ winner: props.match.finished && !props.match.winner }">
			<td class="name">
				<ViewPlayerName :player="props.match.teamA?.playerA" :inverted="true" />
				<template v-if="props.mode === Mode.DOUBLE">
					<br />
					<ViewPlayerName
						:player="props.match.teamA?.playerB"
						:inverted="true"
					/>
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
				<ViewPlayerName :player="props.match.teamB?.playerA" :inverted="true" />
				<template v-if="props.mode === Mode.DOUBLE">
					<br />
					<ViewPlayerName
						:player="props.match.teamB?.playerB"
						:inverted="true"
					/>
				</template>
			</td>

			<template v-if="props.match.sets !== null">
				<td v-for="set in props.match.sets" :key="set.index" class="result">
					{{ set.scoreB }}
				</td>
			</template>
		</tr>
	</table>
</template>

<script setup lang="ts">
import { KnockoutMatch } from "@/interfaces/knockoutSystem"
import { Mode } from "@/interfaces/competition"
import ViewPlayerName from "@/components/views/player/ViewPlayerName.vue"

const props = defineProps<{
	match: KnockoutMatch
	mode: Mode
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

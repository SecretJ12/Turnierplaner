<template>
	<ViewMatch
		:sets="props.sets"
		:finished="props.finished"
		:winner="props.winner"
		:single="props.single"
	>
		<template #teamA-name>
			<td class="name">
				{{
					props.teamA.length === 1
						? `${props.teamA[0].playerA?.lastName}, ${props.teamA[0].playerA?.firstName}`
						: ""
				}}
				<template v-if="!props.single">
					<br />
					{{
						props.teamA.length === 1
							? `${props.teamA[0].playerB?.lastName}, ${props.teamA[0].playerB?.firstName}`
							: ""
					}}
				</template>
			</td>
		</template>

		<template #teamB-name>
			<td class="name">
				{{
					props.teamB.length === 1
						? `${props.teamB[0].playerA?.lastName}, ${props.teamB[0].playerA?.firstName}`
						: ""
				}}
				<template v-if="!props.single">
					<br />
					{{
						props.teamB.length === 1
							? `${props.teamB[0].playerB?.lastName}, ${props.teamB[0].playerB?.firstName}`
							: ""
					}}
				</template>
			</td>
		</template>
	</ViewMatch>
	<div
		id="date"
		:class="{
			dateSingles: props.single,
			dateDoubles: !props.single,
		}"
	></div>
</template>

<script setup lang="ts">
import ViewMatch from "@/components/views/competition/knockoutSystem/ViewMatch.vue"
import { Mode } from "@/interfaces/competition"
import { Team } from "@/interfaces/team"
import { Set } from "@/interfaces/match"

const props = defineProps<{
	mode: Mode
	teamA: Team[]
	teamB: Team[]
	sets: Array<Set> | null
	single: boolean
	finished: boolean
	winner: boolean | null
}>()
</script>

<style scoped>
#date {
	text-align: right;
	padding-right: 1rem;
	font-style: italic;
}

.dateSingles {
	height: 1rem;
}

.dateDoubles {
	height: 1rem;
}

.name {
	text-align: left;
	padding-left: 20px;
}
</style>

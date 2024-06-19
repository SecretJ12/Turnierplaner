<template>
	<ViewMatch
		:sets="null"
		:finished="true"
		:winner="false"
		:single="props.single"
	>
		<template #teamA-name>
			<TeamTableElementDraggable
				:teams="teamA"
				:animated="false"
				:competition="competition"
			>
			</TeamTableElementDraggable>
		</template>
		<template #teamB-name>
			<TeamTableElementDraggable
				:teams="teamB"
				:animated="false"
				:competition="competition"
			>
			</TeamTableElementDraggable>
		</template>
	</ViewMatch>
	<div
		id="date"
		:class="{
			dateSingles: props.single,
			dateDoubles: !props.single,
		}"
	/>
</template>

<script setup lang="ts">
import ViewMatch from "@/components/views/competition/knockoutSystem/ViewMatch.vue"
import { Competition } from "@/interfaces/competition"
import { Team } from "@/interfaces/team"
import TeamTableElementDraggable from "@/components/views/prepare/components/TeamTableElementDraggable.vue"

const props = defineProps<{
	teamA: Team[]
	teamB: Team[]
	single: boolean
	competition: Competition
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
</style>

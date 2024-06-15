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
				@onAdd="emit('onAdd', index, 0)"
				@onRemove="emit('onRemove', index, 0)"
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
import { KnockoutMatch } from "@/interfaces/knockoutSystem"
import { Competition, Mode } from "@/interfaces/competition"
import { Team } from "@/interfaces/team"
import { useI18n } from "vue-i18n"
import { TransitionGroup } from "vue"
import DraggablePanel from "@/draggable/DraggablePanel.vue"
import TeamContainerDraggable from "@/components/views/prepare/components/TeamContainerDraggable.vue"
import TeamTableElementDraggable from "@/components/views/prepare/components/TeamTableElementDraggable.vue"

const { t } = useI18n({ inheritLocale: true })

const props = defineProps<{
	teamA: Team[]
	teamB: Team[]
	single: boolean
	competition: Competition
}>()

const emit = defineEmits(["onAdd", "onRemove"])
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

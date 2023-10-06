<template>
	<Fieldset :legend="legend" class="flex-1">
		<DraggablePanel
			id="playerA"
			:list="list"
			:put="put"
			item-key="id"
			:tag="TransitionGroup"
			:component-data="{
				tag: 'div',
				name: 'default',
				type: 'transition',
			}"
			group="playersA"
			class="flex flex-wrap gap-2 border-3 min-h-3rem border-round border-dashed"
			style="min-height: 4em"
		>
			<template #default="{ item }">
				<PlayerBox :name="item.name" />
			</template>
		</DraggablePanel>
	</Fieldset>
</template>

<script setup lang="ts">
import DraggablePanel from "@/draggable/DraggablePanel.vue"
import PlayerBox from "@/components/views/prepare/editTeamsRefactored/PlayerBox.vue"
import { Player } from "@/interfaces/player"
import { TransitionGroup } from "vue"

const props = defineProps<{
	group: string
	put: string[]
	list: Player[]
	secondary?: boolean
	legend: string
}>()
</script>

<style scoped></style>

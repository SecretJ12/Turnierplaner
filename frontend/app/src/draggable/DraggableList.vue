<template>
	<Fieldset :legend="legend" class="flex-1">
		<DraggablePanel
			:id="props.id"
			:list="list"
			:put="put"
			item-key="id"
			:tag="TransitionGroup"
			:component-data="{
				tag: 'div',
				name: 'default',
				type: 'transition',
			}"
			:group="props.group"
			class="flex flex-wrap gap-2 border-3 min-h-3rem border-round border-dashed justify-content-around"
			style="min-height: 4em"
		>
			<template #default="{ item }">
				<slot :playerName="item.name" />
			</template>
		</DraggablePanel>
	</Fieldset>
</template>

<script setup lang="ts">
import DraggablePanel from "@/draggable/DraggablePanel.vue"
import { Player } from "@/interfaces/player"
import { TransitionGroup } from "vue"

const props = defineProps<{
	group: string
	put: string[]
	list: Player[]
	legend: string
	id: string
}>()
</script>

<style scoped></style>

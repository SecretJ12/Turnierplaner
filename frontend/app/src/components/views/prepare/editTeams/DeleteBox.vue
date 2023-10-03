<template>
	<div
		class="h-3rem flex align-items-center pl-3 select-none w-9rem z-0"
		style="position: absolute; margin-top: 3px"
		@mouseover="showOp"
	>
		<span class="text-red-500">Drop to delete</span>
	</div>
	<DraggablePanel
		id="trash"
		:list="trash"
		:put="[props.group]"
		item-key="id"
		tag="div"
		:group="props.group"
		class="flex flex-row flex-row flex-wrap gap-2 border-3 h-3rem border-round border-dashed w-10rem z-1"
		style="border-color: var(--red-500); box-sizing: content-box"
		@mouseover="showOp"
	>
		<template #default="{ item }"></template>
	</DraggablePanel>
	<OverlayPanel ref="op">
		<span v-if="trash.length === 0">No player deleted</span>
		<DraggablePanel
			id="trash"
			:list="trash"
			:put="[props.group]"
			item-key="id"
			tag="div"
			:group="props.group"
			class="flex flex-wrap gap-2 max-w-30rem"
			style="border-color: var(--red-500)"
			dismissable
		>
			<template #default="{ item }">
				<PlayerCard
					:id="item.name"
					:player="item"
				/>
			</template>
		</DraggablePanel>
	</OverlayPanel>
</template>

<script setup lang="ts">
import DraggablePanel from "@/draggable/DraggablePanel.vue"
import { ref } from "vue"
import { signedUpPlayer } from "@/interfaces/player";
import PlayerCard from "@/components/views/prepare/editTeams/PlayerCard.vue"
import OverlayPanel from "primevue/overlaypanel"

const props = defineProps<{
	group: string
}>()

const trash = ref<signedUpPlayer[]>([])

const op = ref<OverlayPanel>()

function showOp(event: Event) {
	op.value?.show(event)
}
</script>

<style scoped>
</style>
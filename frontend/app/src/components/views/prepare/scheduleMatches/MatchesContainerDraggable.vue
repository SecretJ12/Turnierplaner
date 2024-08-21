<template>
	<DraggablePanel
		v-if="knockout"
		id="match"
		:component-data="{
			tag: 'div',
			name: props.isUpdating || isUpdating ? 'playerList' : 'default',
			type: 'transition',
		}"
		group="match"
		:list="[knockout.finale, knockout.thirdPlace]"
		:tag="TransitionGroup"
		class="flex flex-row flex-wrap gap-2 border-3 min-player-height border-round border-dashed"
		item-key="id"
		style="box-sizing: content-box; width: calc(100% - 6px)"
	>
		<template #default="{ item }">
			<div
				v-if="item"
				class="bg-primary-400 text-black border-round select-none cursor-pointer pl-3 pr-3 h-3rem inline text-50 flex align-items-center justify-content-center"
			>
				<span>{{ item.id }}</span>
			</div>
		</template>
	</DraggablePanel>
</template>

<script setup lang="ts">
import { TransitionGroup } from "vue"
import { Player } from "@/interfaces/player"
import PlayerCard from "@/components/views/prepare/components/PlayerCard.vue"
import DraggablePanel from "@/draggable/DraggablePanel.vue"
import { getKnockout } from "@/backend/knockout"
import { useRoute } from "vue-router"

const route = useRoute()
const props = withDefaults(
	defineProps<{
		isUpdating: boolean
	}>(),
	{
		isUpdating: false,
	},
)

const { data: knockout } = getKnockout(route)

</script>

<style scoped></style>

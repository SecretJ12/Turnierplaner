<template>
	<div class="flex flex-column gap-3">
		<div
			class="flex flex-wrap align-items-end justify-content-end gap-3"
			style="width: 100%"
		>
			<AddPlayer
				:competition="props.competition"
				:tournament="props.tournament"
				class="flex-grow-1"
			/>
			<DeleteBox :group="props.group" />
		</div>

		<Fieldset :legend="props.title" style="width: 100%">
			<DraggablePanel
				:list="props.players"
				:put="[group]"
				item-key="name"
				:tag="TransitionGroup"
				:component-data="{
					tag: 'div',
					name: props.animated ? 'playerList' : 'default',
					type: 'transition',
				}"
				:group="group"
				class="flex flex-row flex-wrap gap-2 border-3 min-h-3rem border-round border-dashed"
				style="box-sizing: content-box"
			>
				<template #default="{ item }">
					<PlayerCard :key="(<signedUpPlayer>item).name" :player="item" />
				</template>
			</DraggablePanel>
		</Fieldset>
	</div>
</template>

<script setup lang="ts">
import { Tournament } from "@/interfaces/tournament"
import { Competition } from "@/interfaces/competition"
import AddPlayer from "@/components/views/prepare/editTeams/AddPlayer.vue"
import DeleteBox from "@/components/views/prepare/editTeams/DeleteBox.vue"
import { TransitionGroup } from "vue"
import { signedUpPlayer } from "@/interfaces/player"
import PlayerCard from "@/components/views/prepare/editTeams/PlayerCard.vue"
import DraggablePanel from "@/draggable/DraggablePanel.vue"

const props = withDefaults(
	defineProps<{
		tournament: Tournament
		competition: Competition
		group: string
		animated?: boolean
		players: signedUpPlayer[]
		title: string
	}>(),
	{
		animated: false,
	},
)
</script>

<style scoped>
.min-h-3rem {
	min-height: 3rem;
}
</style>

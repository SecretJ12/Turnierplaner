<template>
	<div>
		<Card>
			<template #title>{{ props.title }}</template>
			<template #content>
				<div class="flex flex-column gap-3">
					<div
						class="flex flex-wrap align-items-end justify-content-end gap-3"
						style="width: 100%"
					>
						<AddPlayer
							:competition="props.competition"
							:tournament="props.tournament"
							class="flex-grow-1 w-1"
						/>
						<DeleteBox :group="props.group" :secondary="secondary" />
					</div>

					<DraggablePanel
						:id="id"
						:component-data="{
							tag: 'div',
							name: props.animated ? 'playerList' : 'default',
							type: 'transition',
						}"
						:group="group"
						:list="props.players"
						:put="[group]"
						:tag="TransitionGroup"
						class="flex flex-row flex-wrap gap-2 border-3 min-h-3rem border-round border-dashed"
						item-key="name"
						style="box-sizing: content-box; width: calc(100% - 6px)"
					>
						<template #default="{ item }">
							<PlayerCard
								:key="(<signedUpPlayer>item).name"
								:player="item"
								:secondary="secondary"
							/>
						</template>
					</DraggablePanel>
				</div>
			</template>
		</Card>
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
		id: string
		tournament: Tournament
		competition: Competition
		animated?: boolean
		players: signedUpPlayer[]
		title: string
		group: string
		secondary?: boolean
	}>(),
	{
		animated: false,
		secondary: false,
	},
)
</script>

<style scoped>
.min-h-3rem {
	min-height: 3rem;
}
</style>
<template>
	<div>
		<Card>
			<template #title>{{ props.title }}</template>
			<template #content>
				<div class="flex flex-column gap-3">
					<div
						class="flex flex-wrap align-items-end justify-content-end gap-3 w-full"
					>
						<AddPlayer
							:competition="props.competition"
							:tournament="props.tournament"
							class="flex-grow-1 w-1"
							:is-updating="isUpdating"
							@add-player="addPlayer"
						/>
						<DeleteBox :group="props.group" :secondary="secondary" />
					</div>

					<DraggablePanel
						:id="id"
						:component-data="{
							tag: 'div',
							name: props.isUpdating || locAnimated ? 'playerList' : 'default',
							type: 'transition',
						}"
						:group="group"
						:list="props.players"
						:put="[group]"
						:tag="TransitionGroup"
						class="flex flex-row flex-wrap gap-2 border-3 min-player-height border-round border-dashed"
						item-key="name"
						style="box-sizing: content-box; width: calc(100% - 6px)"
					>
						<template #default="{ item }">
							<PlayerCard
								:key="(<Player>item).name"
								:player="item"
								:secondary="secondary"
								@add-player="addPlayer"
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
import { ref, TransitionGroup } from "vue"
import { Player } from "@/interfaces/player"
import PlayerCard from "@/components/views/prepare/components/PlayerCard.vue"
import DraggablePanel from "@/draggable/DraggablePanel.vue"

const props = withDefaults(
	defineProps<{
		id: string
		tournament: Tournament
		competition: Competition
		isUpdating?: boolean
		players: Player[]
		title: string
		group: string
		secondary?: boolean
	}>(),
	{
		isUpdating: false,
		secondary: false,
	},
)

const locAnimated = ref(false)

async function addPlayer(p: Player) {
	locAnimated.value = true
	props.players.push(p)
	await new Promise((resolve) => setTimeout(resolve, 500))
	locAnimated.value = false
}
</script>

<style scoped></style>

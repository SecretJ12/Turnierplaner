<template>
	<Fieldset legend="team" class="flex-1">
		<DraggablePanel
			:list="teams"
			:put="[
				'teams',
				'playersA',
				'playersB',
				'deletedPlayerA',
				'deletedPlayerB',
			]"
			item-key="id"
			:tag="TransitionGroup"
			:component-data="{
				tag: 'div',
				name: 'default',
				type: 'transition',
			}"
			group="teams"
			class="flex flex-wrap inline-block gap-2 border-3 border-round border-dashed"
			style="min-height: 4em"
			wrap
		>
			<template #default="{ item: outerItem }">
				<div style="grid-auto-rows: 1fr">
					<DraggablePanel
						:list="outerItem.playerA"
						:put="different ? ['playersA'] : ['playersA', 'playersB']"
						item-key="id"
						:tag="TransitionGroup"
						single
						:component-data="{
							tag: 'div',
							name: 'default',
							type: 'transition',
						}"
						group="playersA"
						class="gap-2 align-items-center bg-blue-50 justify-content-center border border-dashed"
						style="min-width: 150px; min-height: 50px"
						hook
						@onRemove="cleanUpTeams"
					>
						<template #default="{ item: innerItem }">
							<PlayerBox :name="innerItem.name" />
						</template>
					</DraggablePanel>
					<DraggablePanel
						:list="outerItem.playerB"
						:put="
							playerBDifferent
								? ['playersB', 'deletedPlayerB']
								: ['playersA', 'playersB', 'deletedPlayerA']
						"
						item-key="id"
						:tag="TransitionGroup"
						single
						:component-data="{
							tag: 'div',
							name: 'default',
							type: 'transition',
						}"
						group="playersB"
						class="bg-red-50 border-round border-dashed"
						style="min-width: 150px; min-height: 50px"
						hook
						@onRemove="cleanUpTeams"
					>
						<template #default="{ item: innerItem }">
							<PlayerBox :name="innerItem.name" :secondary="different" />
						</template>
					</DraggablePanel>
				</div>
			</template>
		</DraggablePanel>
	</Fieldset>
</template>

<script setup lang="ts">
import { TeamArray } from "@/interfaces/player"
import { TransitionGroup } from "vue"
import DraggablePanel from "@/draggable/DraggablePanel.vue"
import PlayerBox from "@/components/views/prepare/editTeamsRefactored/PlayerBox.vue"

const props = defineProps<{
	different: boolean | undefined
	teams: TeamArray[]
}>()

const emits = defineEmits(["cleanUpTeams"])

function cleanUpTeams() {
	emits("cleanUpTeams")
}
</script>

<style scoped></style>

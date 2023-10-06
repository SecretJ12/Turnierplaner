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
						id="top"
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
						class="border-3 border-dashed h-3rem border-top-3 border-round content-box"
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
						:put="different ? ['playersB'] : ['playersA', 'playersB']"
						item-key="id"
						:tag="TransitionGroup"
						single
						:component-data="{
							tag: 'div',
							name: 'default',
							type: 'transition',
						}"
						group="playersB"
						class="border-3 border-top-none border-dashed h-3rem border-bottom-3 border-round content-box"
						:class="props.different ? 'bottomDifferent' : 'bottomSame'"
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

<style scoped>
#top {
	border-left-color: var(--primary-400);
	border-top-color: var(--primary-400);
	border-right-color: var(--primary-400);
}

.bottomSame {
	border-left-color: var(--primary-400);
	border-bottom-color: var(--primary-400);
	border-right-color: var(--primary-400);
}

.bottomDifferent {
	border-left-color: var(--yellow-500);
	border-bottom-color: var(--yellow-500);
	border-right-color: var(--yellow-500);
}
</style>

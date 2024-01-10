<template>
	<DraggablePanel
		:list="teams"
		:put="true"
		itemKey="id"
		:tag="TransitionGroup"
		:component-data="{
			tag: 'div',
			name: props.animated ? 'playerList' : 'default',
			type: 'transition',
		}"
		group="teams"
		class="flex flex-wrap gap-2 border-3 border-round border-dashed justify-content-start"
		:class="{
			'min-player-height': competition?.mode === Mode.SINGLE,
			'min-team-height': competition?.mode === Mode.DOUBLE,
		}"
		style="box-sizing: content-box"
	>
		<template #default="{ item }">
			<PlayerCard
				v-if="item.playerA && competition?.mode === Mode.SINGLE"
				:player="item.playerA"
			></PlayerCard>
			<TeamBox
				v-else-if="competition?.mode === Mode.DOUBLE"
				:different="competition?.playerB.different || false"
			>
				<template #playerA>
					<PlayerCard v-if="item.playerA" :player="item.playerA"></PlayerCard>
				</template>
				<template #playerB>
					<PlayerCard
						v-if="item.playerB"
						:player="item.playerB"
						:secondary="competition?.playerB.different || false"
					></PlayerCard>
				</template>
			</TeamBox>
		</template>
	</DraggablePanel>
</template>

<script setup lang="ts">
import { TransitionGroup } from "vue"
import { Competition, Mode } from "@/interfaces/competition"
import { Team } from "@/interfaces/match"
import PlayerCard from "@/components/views/prepare/components/PlayerCard.vue"
import TeamBox from "@/components/views/prepare/components/TeamBox.vue"
import DraggablePanel from "@/draggable/DraggablePanel.vue"

const props = defineProps<{
	teams: Team[]
	competition: Competition
	animated: boolean
}>()
</script>

<style scoped></style>

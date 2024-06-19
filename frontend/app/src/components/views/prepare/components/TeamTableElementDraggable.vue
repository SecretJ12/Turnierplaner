<template>
	<DraggablePanel
		:list="teams"
		:put="true"
		item-key="id"
		:tag="TransitionGroup"
		:component-data="{
			tag: 'div',
			name: props.animated ? 'playerList' : 'default',
			type: 'transition',
		}"
		single
		group="teams"
		class="flex justify-content-start"
		:class="{
			'min-player-height': competition?.mode === Mode.SINGLE,
			'min-team-height': competition?.mode === Mode.DOUBLE,
		}"
		style="box-sizing: content-box"
	>
		<template #default="{ item }">
			<td
				class="border-round select-none cursor-pointer font-bold pl-3 pr-3 min-player-height inline text-900 flex align-items-center justify-content-center"
			>
				{{
					props.teams[0]?.playerA
						? `${props.teams[0].playerA.lastName}, ${props.teams[0].playerA.firstName}`
						: ""
				}}
				<br />
				{{
					props.teams[0]?.playerB
						? `${props.teams[0].playerB.lastName}, ${props.teams[0].playerB.firstName}`
						: ""
				}}
			</td>
		</template>
	</DraggablePanel>
</template>

<script setup lang="ts">
import { TransitionGroup } from "vue"
import { Competition, Mode } from "@/interfaces/competition"
import { Team } from "@/interfaces/team"
import PlayerCard from "@/components/views/prepare/components/PlayerCard.vue"
import TeamBox from "@/components/views/prepare/components/TeamBox.vue"
import DraggablePanel from "@/draggable/DraggablePanel.vue"

const props = defineProps<{
	teams: Team[]
	competition: Competition
	animated: boolean
}>()
</script>

<style scoped>
table {
	table-layout: fixed;
	width: 300px;
	background-color: #e0e0e0;
	margin: 0;
	border: solid black 2px;
}

.tableSingles {
	height: 80px;
}

.tableDoubles {
	height: 108px;
}

table > tr {
	height: 50%;
}

.winner {
	background-color: #c0c0c0;
}

tr {
	border: solid black 2px;
}

td {
	border: solid black 1px;
}

.name {
	text-align: left;
	padding-left: 20px;
}

.result {
	text-align: center;
	width: 25px;
	background-color: #fdfdfd;
}
.fill {
	width: 100%;
	height: 100%;
}

.winner > .result {
	background-color: #e0e0e0;
}
</style>

<template>
	<MatchBox :different="false" style="width: 250px">
		<template #playerA>
			<DraggablePanel
				id="playerA"
				:component-data="{
					tag: 'div',
					type: 'transition',
				}"
				:list="teamA"
				:tag="TransitionGroup"
				group="team"
				item-key="id"
				style="width: 100%; height: 100%"
				single
			>
				<template #default="{ item: team }">
					<TeamCard :team="team" />
				</template>
			</DraggablePanel>
		</template>
		<template #playerB>
			<DraggablePanel
				id="playerA"
				:component-data="{
					tag: 'div',
					type: 'transition',
				}"
				:list="teamB"
				:tag="TransitionGroup"
				group="team"
				item-key="id"
				style="width: 100%; height: 100%"
				single
			>
				<template #default="{ item: team }">
					<TeamCard :team="team" />
				</template>
			</DraggablePanel>
		</template>
	</MatchBox>
</template>

<script setup lang="ts">
import { KnockoutMatch } from "@/interfaces/knockoutSystem"
import { ref, TransitionGroup, watch } from "vue"
import DraggablePanel from "@/draggable/DraggablePanel.vue"
import TeamCard from "@/components/views/prepare/components/TeamCard.vue"
import MatchBox from "@/components/views/prepare/components/MatchBox.vue"

const match = defineModel<KnockoutMatch>()

const teamA = ref([])
const teamB = ref([])

watch(teamA.value, () => {
	if (!match.value) return
	match.value.teamA = teamA.value[0]
})
watch(teamB.value, () => {
	if (!match.value) return
	match.value.teamB = teamB.value[0]
})
</script>

<style scoped></style>

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
				:wrap="updateA"
				single
				@on-remove="removeA"
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
				:wrap="updateB"
				single
				@on-remove="removeB"
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
import { Team } from "@/interfaces/team"

const props = defineProps<{ match: KnockoutMatch }>()
const emit = defineEmits(["update:teamA", "update:teamB"])

const teamA = ref<Team[]>([])
const teamB = ref<Team[]>([])

function updateA(val: Team) {
	emit("update:teamA", val)
	return val
}
function removeA() {
	emit("update:teamA", null)
}
function updateB(val: Team) {
	emit("update:teamB", val)
	return val
}
function removeB() {
	emit("update:teamB", null)
}

watch(props, () => {
	console.log("update")
	if (teamA.value !== (props.match.teamA ? [props.match.teamA] : []))
		teamA.value = props.match.teamA ? [props.match.teamA] : []
	if (teamB.value !== (props.match.teamB ? [props.match.teamB] : []))
		teamB.value = props.match.teamB ? [props.match.teamB] : []
})
</script>

<style scoped></style>

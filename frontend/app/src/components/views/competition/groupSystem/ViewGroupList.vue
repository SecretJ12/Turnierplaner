<template>
	<el-table :data="data" style="width: 100%">
		<el-table-column prop="begin" label="Begin Time"></el-table-column>
		<el-table-column prop="court" label="Court"></el-table-column>
		<el-table-column prop="teamA" label="Player 1"></el-table-column>
		<el-table-column prop="teamB" label="Player 2"></el-table-column>
	</el-table>
</template>

<script lang="ts" setup>
import { Group } from "@/interfaces/groupSystem"
import { ref } from "vue"

const props = defineProps<{
	group: Group
}>()

const data = ref<
	{
		begin: Date
		court: string
		teamA: string
		teamB: string
	}[]
>([])

for (const match of props.group.matches) {
	data.value.push({
		begin: match.begin,
		court: match.court,
		teamA:
			match.teamA?.playerA.firstName +
			" " +
			match.teamA?.playerA.lastName +
			(match.teamA?.playerB
				? " / " +
				  match.teamA?.playerB.firstName +
				  " " +
				  match.teamA?.playerB.lastName
				: ""),

		teamB:
			match.teamB?.playerA.firstName +
			" " +
			match.teamB?.playerA.lastName +
			(match.teamB?.playerB
				? " / " +
				  match.teamB?.playerB.firstName +
				  " " +
				  match.teamB?.playerB.lastName
				: ""),
	})
}
</script>

<style scoped></style>

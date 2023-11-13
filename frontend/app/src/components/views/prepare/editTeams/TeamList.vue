<template>
	<div>
		<Card>
			<template #title>Teams</template>
			<template #content>
				<slot></slot>

				<div
					class="flex flex-row flex-wrap gap-2 border-3 min-h-3rem border-round border-dashed"
					style="box-sizing: content-box; width: 100%; margin-top: 20px"
				>
					<TeamBox
						v-for="team in teams"
						:id="team.id"
						:different="competition.playerB.different"
						:team="team"
					/>
				</div>
			</template>
		</Card>
	</div>
</template>

<script lang="ts" setup>
import TeamBox from "@/components/views/prepare/editTeams/TeamBox.vue"
import { Team } from "@/interfaces/match"
import { Competition } from "@/interfaces/competition"

const props = defineProps<{
	teams: Team[]
	competition: Competition
}>()
</script>

<style sco;ped></style>
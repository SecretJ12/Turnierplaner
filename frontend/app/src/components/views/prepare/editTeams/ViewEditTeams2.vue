<template>
	<div class="flex flex-column gap-5">
		<div v-if="tournament && competition" class="grid">
			<PlayerList
				:competition="competition"
				:players="playersA"
				:tournament="tournament"
				class="col-4"
				group="playersA"
				title="Players A"
			/>
			<div class="col-4"></div>
			<PlayerList
				:competition="competition"
				:players="playersB"
				:tournament="tournament"
				class="col-4"
				group="playersA"
				title="Players B"
			/>
		</div>
		<div v-else class="grid">
			<div class="col-4">
				<Skeleton class="h-30rem" />
			</div>
			<div class="col-4">
				<Skeleton class="h-30rem" />
			</div>
			<div class="col-4">
				<Skeleton class="h-30rem" />
			</div>
		</div>

		<div class="mt-2 grid grid-nogutter justify-content-between">
			<Button disabled icon="pi pi-angle-left" icon-pos="left" label="Back" />
			<!--TODO internalization-->
			<Button label="Reset" severity="danger" @click="reset" />
			<!-- TODO add @click -->
			<Button :label="t('general.save')" severity="success" @click="save" />
			<Button
				v-if="route.params.step !== 'scheduleMatches'"
				icon="pi pi-angle-right"
				icon-pos="right"
				label="Next"
				@click="nextPage"
			/>
		</div>
	</div>
</template>

<script lang="ts" setup>
import Button from "primevue/button"
import { useRoute, useRouter } from "vue-router"
import { useToast } from "primevue/usetoast"
import { useI18n } from "vue-i18n"
import { getTournamentDetails } from "@/backend/tournament"
import { getCompetitionDetails } from "@/backend/competition"
import PlayerList from "@/components/views/prepare/editTeams/PlayerList.vue"
import { ref } from "vue"
import { Player } from "@/interfaces/player"
import axios from "axios"
import { Team, TeamServer, teamServerToClient } from "@/interfaces/match"

const route = useRoute()
const router = useRouter()
const toast = useToast()
const { t } = useI18n({ inheritLocale: true })

const tournament = getTournamentDetails(
	<string>route.params.tourId,
	t,
	toast,
	{},
)
const competition = getCompetitionDetails(route, t, toast, {
	suc: () => {
		update()
	},
})

// for restoring the initial state
const initialTeam = ref<Team[]>([])
const initialPlayerA = ref<Player[]>([])
const initialPlayerB = ref<Player[]>([])
const teams = ref<Team[]>([])
const playersA = ref<Player[]>([])
const playersB = ref<Player[]>([])

function reset() {
	// TODO reset players
	toast.add({
		severity: "error",
		summary: "Reset",
		detail: "Reseted",
		life: 3000,
	})
}

function save() {
	// TODO update players
	toast.add({
		severity: "success",
		summary: "Success",
		detail: "Players updated",
		life: 3000,
	})
}

function nextPage() {
	router.replace({
		name: "editPlayers",
		params: { tourId: route.params.tourId, compId: route.params.compId },
	})
}

function update() {
	teams.value = []
	playersA.value = []
	playersB.value = []

	initialTeam.value = []
	initialPlayerA.value = []
	initialPlayerB.value = []

	axios
		.get<TeamServer[]>(
			`/tournament/${route.params.tourId}/competition/${route.params.compId}/signedUpTeams`,
		)
		.then((response) => {
			response.data.map(teamServerToClient).forEach((team) => {
				if (team.playerA && team.playerB === null) {
					playersA.value.push(team.playerA)
					initialPlayerA.value.push(team.playerA)
				} else if (team.playerA === null && team.playerB) {
					playersB.value.push(team.playerB)
					initialPlayerB.value.push(team.playerB)
				} else if (team.playerA && team.playerB) {
					teams.value.push(team)
					initialTeam.value.push(team)
				}
			})
		})
}
</script>

<style scoped></style>
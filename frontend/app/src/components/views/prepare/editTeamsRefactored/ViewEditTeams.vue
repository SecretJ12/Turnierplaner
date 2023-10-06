<template>
	<div class="flex flex-column gap-5">
		<div class="flex justify-content-around">
			<div class="flex flex-row gap-2">
				<AddPlayer
					v-if="tournament && competition"
					:competition="competition"
					:tournament="tournament"
					:player-a="false"
					@addPlayer="addPlayerA"
				/>
				<div class="flex flex-column justify-content-end gap-2">
					<DeleteBox group="playersA" />
				</div>
			</div>
			<SplitButton
				v-if="competition?.mode === Mode.DOUBLE"
				label="Randomize"
				severity="help"
				:model="randomizeItems"
				class="w-fit"
				:disabled="isSorting"
				@click="randomize"
			>
				<template #icon>
					<span class="material-icons mb-1" style="font-size: 1.2rem"
						>casino</span
					>
				</template>
			</SplitButton>
			<div v-if="competition?.playerB.different" class="flex flex-row gap-2">
				<AddPlayer
					v-if="tournament && competition"
					:competition="competition"
					:tournament="tournament"
					:player-a="false"
					@addPlayer="addPlayerB"
				/>
				<div class="flex flex-column justify-content-end gap-2">
					<DeleteBox group="playersB" :secondary="true" />
				</div>
			</div>
		</div>
		<div class="flex flex-row justify-content-around">
			<PlayerList
				group="playersA"
				:put="
					competition?.playerB.different
						? ['playersA']
						: ['playersA', 'playersB']
				"
				:list="playersA"
				legend="Team A"
			/>
			<TeamList
				v-if="competition?.mode === Mode.DOUBLE"
				:teams="teams"
				:different="competition?.playerB.different"
				@cleanUpTeams="cleanUpTeams"
			/>
			<PlayerList
				v-if="competition?.playerB.different"
				legend="Team B"
				:list="playersB"
				:put="['playersB']"
				group="playersB"
				:secondary="true"
			/>
		</div>
		<div class="mt-2 grid grid-nogutter justify-content-between">
			<!--TODO internalization-->
			<Button label="Reset" @click="restore" />
			<!-- TODO add @click -->
			<Button :label="t('general.save')"> </Button>
			<Button
				v-if="route.params.step !== 'scheduleMatches'"
				label="Next"
				icon="pi pi-angle-right"
				icon-pos="right"
				@click="nextPage"
			/>
		</div>
	</div>
</template>

<script setup lang="ts">
import { Team } from "@/interfaces/match"
import { computed, ref } from "vue"
import axios from "axios"
import { useRoute, useRouter } from "vue-router"
import { useToast } from "primevue/usetoast"
import { useI18n } from "vue-i18n"
import { Player, playerServerToClient, TeamArray } from "@/interfaces/player"
import { getCompetitionDetails } from "@/backend/competition"
import Button from "primevue/button"
import { Mode } from "@/interfaces/competition"
import DeleteBox from "@/components/views/prepare/editTeamsRefactored/DeleteBox.vue"
import AddPlayer from "@/components/views/prepare/editTeamsRefactored/AddPlayer.vue"
import { getTournamentDetails } from "@/backend/tournament"
import PlayerList from "@/components/views/prepare/editTeamsRefactored/PlayerList.vue"
import TeamList from "@/components/views/prepare/editTeamsRefactored/TeamList.vue"

const route = useRoute()
const router = useRouter()
const toast = useToast()
const { t } = useI18n({ inheritLocale: true })

const isSorting = ref<boolean>(false)

// for restoring the initial state
const initialTeam = ref<TeamArray[]>([])
const initialPlayerA = ref<Player[]>([])
const initialPlayerB = ref<Player[]>([])

const teams = ref<TeamArray[]>([])
const playersA = ref<Player[]>([])
const playersB = ref<Player[]>([])

const deletedPlayerA = ref<Player[]>([])
const deletedPlayerB = ref<Player[]>([])

const competition = getCompetitionDetails(route, t, toast, {
	suc: () => {
		update()
	},
})

const tournament = getTournamentDetails(
	<string>route.params.tourId,
	t,
	toast,
	{},
)

const duration = 2000
const playerCount = ref(0)
const delay = computed(() =>
	Math.min((duration * 2) / 3 / playerCount.value, 100),
)
const delayBetween = computed(() => delay.value / 2)

const randomizeItems = [
	{
		label: "Reroll",
		icon: "pi pi-refresh",
		command: reroll,
	},
	{
		label: "Reset",
		icon: "pi pi-times",
		command: reset,
	},
]

function save() {
	// TODO udpate players
	toast.add({
		severity: "success",
		summary: "Success",
		detail: "Players updated",
		life: 3000,
	})
}

function update() {
	teams.value = []
	playersA.value = []
	playersB.value = []

	initialTeam.value = []
	initialPlayerA.value = []
	initialPlayerB.value = []

	deletedPlayerA.value = []
	deletedPlayerB.value = []

	axios
		.get<Team[]>(
			`/tournament/${route.params.tourId}/competition/${route.params.compId}/signedUpTeams`,
		)
		.then((response) => {
			response.data.forEach((team) => {
				if (team.playerA && team.playerB === null) {
					playersA.value.push(playerServerToClient(team.playerA))
					initialPlayerA.value.push(playerServerToClient(team.playerA))
				} else if (team.playerA === null && team.playerB) {
					playersB.value.push(playerServerToClient(team.playerB))
					initialPlayerB.value.push(playerServerToClient(team.playerB))
				} else if (team.playerA && team.playerB) {
					teams.value.push({
						id: team.id,
						playerA: [playerServerToClient(team.playerA)],
						playerB: [playerServerToClient(team.playerB)],
					})
					initialTeam.value.push({
						id: team.id,
						playerA: [playerServerToClient(team.playerA)],
						playerB: [playerServerToClient(team.playerB)],
					})
				}
			})
		})
}

function sleep(milliseconds: number) {
	return new Promise((resolve) => setTimeout(resolve, milliseconds))
}

async function randomize() {
	if (!competition.value) return
	isSorting.value = true
	for (const i in teams.value) {
		if (teams.value[i].playerA.length === 0) {
			const r = Math.floor(Math.random() * playersA.value.length)
			const element = playersA.value[r]
			playersA.value = playersA.value.filter((v, i) => i !== r)
			await sleep(delayBetween.value)
			teams.value[i].playerA.push(element)
			await sleep(delay.value)
		}
		if (competition.value.playerB.different) {
			if (teams.value[i].playerB.length === 0) {
				const r = Math.floor(Math.random() * playersB.value.length)
				const element = playersB.value[r]
				playersB.value = playersB.value.filter((v, i) => i !== r)
				await sleep(delayBetween.value)
				teams.value[i].playerB.push(element)
				await sleep(delay.value)
			}
		} else {
			if (teams.value[i].playerB.length === 0) {
				const r = Math.floor(Math.random() * playersA.value.length)
				const element = playersA.value[r]
				playersA.value = playersA.value.filter((v, i) => i !== r)
				await sleep(delayBetween.value)
				teams.value[i].playerB.push(element)
				await sleep(delay.value)
			}
		}
	}
	isSorting.value = false
}

async function reset() {
	if (!competition.value) return
	isSorting.value = true

	for (const i in teams.value) {
		if (teams.value[i].playerA.length === 1) {
			const element = teams.value[i].playerA[0]
			teams.value[i].playerA = []
			await sleep(delayBetween.value)
			playersA.value.push(element)
			await sleep(delay.value)
		}
		if (competition.value.playerB.different) {
			if (teams.value[i].playerB.length === 1) {
				const element = teams.value[i].playerB[0]
				teams.value[i].playerB = []
				await sleep(delayBetween.value)
				playersB.value.push(element)
				await sleep(delay.value)
			}
		} else {
			if (teams.value[i].playerB.length === 1) {
				const element = teams.value[i].playerB[0]
				teams.value[i].playerB = []
				await sleep(delayBetween.value)
				playersA.value.push(element)
				await sleep(delay.value)
			}
		}
	}
	isSorting.value = false
}

function cleanUpTeams() {
	for (let i = 0; i < teams.value.length; i++) {
		if (
			teams.value[i].playerA.length === 0 &&
			teams.value[i].playerB.length === 0
		) {
			teams.value.splice(i, 1)
			return
		}
	}
}

async function reroll() {
	await reset()
	await randomize()
}
function nextPage() {
	router.replace({
		name: "editPlayers",
		params: { tourId: route.params.tourId, compId: route.params.compId },
	})
}

function restore() {
	teams.value = initialTeam.value
	teams.value = []

	for (const team of initialTeam.value) {
		teams.value.push({
			id: team.id,
			playerA: team.playerA.slice(),
			playerB: team.playerB.slice(),
		})
	}

	playersA.value = []
	for (const playerA of initialPlayerA.value) {
		playersA.value.push(playerA)
	}

	playersB.value = []
	for (const playerB of initialPlayerB.value) {
		playersB.value.push(playerB)
	}

	deletedPlayerA.value = []
	deletedPlayerB.value = []
}
function addPlayerA(player: Player) {
	playersA.value.push(player)
	initialPlayerA.value.push(player)
}

function addPlayerB(player: Player) {
	playersB.value.push(player)
	initialPlayerB.value.push(player)
}
</script>

<style scoped></style>

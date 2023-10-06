<template>
	<div class="flex flex-column gap-5">
		<div v-if="tournament && competition" class="grid">
			<PlayerList
				:competition="competition"
				:players="playersA"
				:tournament="tournament"
				:animated="isSorting"
				group="playersA"
				title="Players A"
				:class="{
					'col-4': competition.mode === Mode.DOUBLE,
					'col-12': competition.mode === Mode.SINGLE,
				}"
			/>
			<template v-if="competition.mode === Mode.DOUBLE">
				<div class="col-4">
					<SplitButton
						v-if="competition?.mode === Mode.DOUBLE"
						:disabled="isSorting"
						:model="randomizeItems"
						class="w-fit"
						label="Randomize"
						severity="help"
						@click="randomize"
					>
						<template #icon>
							<span class="material-icons mb-1" style="font-size: 1.2rem"
								>casino</span
							>
						</template>
					</SplitButton>
					<TeamList />
				</div>
				<PlayerList
					:animated="isSorting"
					:competition="competition"
					:players="playersB"
					:tournament="tournament"
					class="col-4"
					group="playersA"
					title="Players B"
				/>
			</template>
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
import { computed, ref, Ref } from "vue"
import { Player } from "@/interfaces/player"
import axios from "axios"
import { Team, TeamServer, teamServerToClient } from "@/interfaces/match"
import { Mode } from "@/interfaces/competition"
import TeamList from "@/components/views/prepare/editTeams/TeamList.vue" // @ts-ignore
import { v4 as uuidv4 } from "uuid"

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
const isSorting = ref(false)

//;;;;; ;for restoring the initial state
const initialTeam = ref<Team[]>([])
const initialPlayerA = ref<Player[]>([])
const initialPlayerB = ref<Player[]>([])
const teams = ref<Team[]>([])
const playersA = ref<Player[]>([])
const playersB = ref<Player[]>([])

function sleep(milliseconds: number) {
	return new Promise((resolve) => setTimeout(resolve, milliseconds))
}

const duration = 2000
const playerCount = ref(0)
const delay = computed(() =>
	Math.min((duration * 2) / 3 / playerCount.value, 100),
)
const delayBetween = computed(() => delay.value / 2)

function selectRandomElement(players: Ref<any[]>) {
	const r = Math.floor(Math.random() * players.value.length)
	const element = players.value[r]
	players.value = players.value.filter((v, i) => i !== r)
	return element
}

async function randomize() {
	if (!competition.value) return
	isSorting.value = true

	// first fill up all existing teams
	for (const i in teams.value) {
		if (!teams.value[i].playerA && playersA.value.length > 0) {
			const element = selectRandomElement(playersA)
			await sleep(delayBetween.value)
			teams.value[i].playerA = element
			await sleep(delay.value)
		}
		if (!teams.value[i].playerB) {
			if (competition.value.playerB.different && playersB.value.length > 0) {
				const element = selectRandomElement(playersB)
				await sleep(delayBetween.value)
				teams.value[i].playerB = element
				await sleep(delay.value)
			} else if (playersA.value.length > 0) {
				const element = selectRandomElement(playersA)
				await sleep(delayBetween.value)
				teams.value[i].playerB = element
				await sleep(delay.value)
			}
		}
	}
	// create teams for all the remaining teams
	if (competition.value.playerB.different) {
		while (playersA.value.length > 0 && playersB.value.length > 0) {
			const elementA = selectRandomElement(playersA)
			const elementB = selectRandomElement(playersB)
			await sleep(delayBetween.value)
			teams.value.push({
				id: uuidv4(),
				playerA: elementA,
				playerB: elementB,
			})
			await sleep(delay.value)
		}
	} else {
		while (playersA.value.length >= 2) {
			const element1 = selectRandomElement(playersA)
			const element2 = selectRandomElement(playersA)
			await sleep(delayBetween.value)
			teams.value.push({
				id: uuidv4(),
				playerA: element1,
				playerB: element2,
			})
			await sleep(delay.value)
		}
	}

	isSorting.value = false
}

async function clearTeams() {
	if (!competition.value) return
	isSorting.value = true

	for (const i in teams.value) {
		const elementA = teams.value[i].playerA
		if (elementA) {
			teams.value[i].playerA = null
			await sleep(delayBetween.value)
			playersA.value.push(elementA)
			await sleep(delay.value)
		}
		const elementB = teams.value[i].playerB
		if (elementB) {
			teams.value[i].playerB = null
			await sleep(delayBetween.value)
			if (competition.value.playerB.different) playersB.value.push(elementB)
			else playersA.value.push(elementB)
			await sleep(delay.value)
		}
	}
	isSorting.value = false
}

async function reroll() {
	await clearTeams()
	await randomize()
}

const randomizeItems = [
	{
		label: "Reroll",
		icon: "pi pi-refresh",
		command: reroll,
	},
	{
		label: "Reset",
		icon: "pi pi-times",
		command: clearTeams,
	},
]

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
					playerCount.value++
				}
				playerCount.value++
			})
		})
}
</script>

<style scoped></style>

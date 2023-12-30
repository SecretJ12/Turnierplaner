<template>
	<div class="flex flex-column gap-5">
		<div
			v-if="tournament && competition"
			class="grid"
			style="flex-wrap: nowrap"
		>
			<PlayerList
				id="playerA"
				:competition="competition"
				:players="playersA"
				:tournament="tournament"
				:class="{
					'col-4':
						competition.mode === Mode.DOUBLE && competition.playerB.different,
					'col-6':
						competition.mode === Mode.DOUBLE && !competition.playerB.different,
					'col-12': competition.mode === Mode.SINGLE,
				}"
				class="transition-duration-200"
				group="playersA"
				title="Players A"
				:is-updating="isUpdating"
			/>
			<TeamList
				v-if="competition.mode === Mode.DOUBLE"
				:animated="isUpdating"
				:class="{
					'col-4': competition.playerB.different,
					'col-6': !competition.playerB.different,
				}"
				:competition="competition"
				:teams="teams"
				class="transition-duration-200"
			>
				<SplitButton
					:disabled="isUpdating"
					:model="randomizeItems"
					class="w-fit"
					label="Randomize"
					@click="randomize"
				>
					<template #icon>
						<span class="material-icons mb-1" style="font-size: 1.2rem"
							>casino</span
						>
					</template>
				</SplitButton>
			</TeamList>
			<PlayerList
				v-if="competition.mode === Mode.DOUBLE && competition.playerB.different"
				id="playerB"
				:competition="competition"
				:is-updating="isUpdating"
				:players="playersB"
				:secondary="true"
				:tournament="tournament"
				class="col-4 overflow-hidden"
				group="playersB"
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
import { computed, ref, Ref } from "vue"
import { Player } from "@/interfaces/player"
import axios from "axios"
import {
	TeamArray,
	teamArrayServerToClient,
	TeamServer,
} from "@/interfaces/match"
import { Mode } from "@/interfaces/competition"
import TeamList from "@/components/views/prepare/editTeams/TeamList.vue"
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
const isUpdating = ref(false)

// for restoring the initial state
const initialTeam = ref<TeamArray[]>([])
const initialPlayerA = ref<Player[]>([])
const initialPlayerB = ref<Player[]>([])

const teams = ref<TeamArray[]>([])
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

// TODO type this function
function selectRandomElement<T>(players: Ref<T[]>) {
	const r = Math.floor(Math.random() * players.value.length)
	const element = players.value[r]
	players.value.splice(r, 1)
	//players.value = players.value.filter((v, i) => i !== r)
	return element
}

async function randomize() {
	if (!competition.value) return
	isUpdating.value = true

	// first fill up all existing teams
	for (const i in teams.value) {
		if (!teams.value[i].playerA.length && playersA.value.length > 0) {
			const element = selectRandomElement(playersA)
			await sleep(delayBetween.value)
			teams.value[i].playerA.push(element)
			await sleep(delay.value)
		}
		if (!teams.value[i].playerB.length) {
			if (competition.value.playerB.different && playersB.value.length > 0) {
				const element = selectRandomElement(playersB)
				await sleep(delayBetween.value)
				teams.value[i].playerB.push(element)
				await sleep(delay.value)
			} else if (playersA.value.length > 0) {
				const element = selectRandomElement(playersA)
				await sleep(delayBetween.value)
				teams.value[i].playerB.push(element)
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
				playerA: [elementA],
				playerB: [elementB],
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
				playerA: [element1],
				playerB: [element2],
			})
			await sleep(delay.value)
		}
	}

	isUpdating.value = false
}

async function clearTeams() {
	if (!competition.value) return
	isUpdating.value = true

	while (teams.value.length > 0) {
		let i = teams.value.length - 1
		const elementA = teams.value[i].playerA
		if (elementA.length) {
			teams.value[i].playerA = []
			await sleep(delayBetween.value)
			playersA.value.push(elementA[0])
			await sleep(delay.value)
		}
		const elementB = teams.value[i].playerB
		if (elementB.length) {
			teams.value[i].playerB = []
			await sleep(delayBetween.value)
			if (competition.value.playerB.different) playersB.value.push(elementB[0])
			else playersA.value.push(elementB[0])
			await sleep(delay.value)
		}
		teams.value.splice(i, 1)
		await sleep(delay.value)
	}
	isUpdating.value = false
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

async function reset() {
	isUpdating.value = true

	teams.value.splice(0, teams.value.length)
	playersA.value.splice(0, playersA.value.length)
	playersB.value.splice(0, playersB.value.length)
	await sleep(1000)

	initialTeam.value.forEach((t) =>
		teams.value.push(JSON.parse(JSON.stringify(t))),
	)
	initialPlayerA.value.forEach((p) => playersA.value.push(p))
	initialPlayerB.value.forEach((p) => playersB.value.push(p))
	await sleep(500)
	isUpdating.value = false

	// TODO reset players
	toast.add({
		severity: "info",
		summary: "Reset",
		detail: "Restored initial configuration",
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
		name: "selectType",
		params: { tourId: route.params.tourId, compId: route.params.compId },
	})
}

async function update() {
	isUpdating.value = true
	teams.value = []
	playersA.value = []
	playersB.value = []
	const anFin = sleep(1000)
	axios
		.get<TeamServer[]>(
			`/tournament/${route.params.tourId}/competition/${route.params.compId}/signedUpTeams`,
		)
		.then(async (response) => {
			await anFin
			response.data.map(teamArrayServerToClient).forEach((team) => {
				if (team.playerA.length > 0 && team.playerB.length === 0) {
					playersA.value.push(team.playerA[0])
					initialPlayerA.value.push(team.playerA[0])
				} else if (
					team.playerA.length === 0 &&
					team.playerB.length > 0 &&
					competition.value?.playerB.different
				) {
					playersB.value.push(team.playerB[0])
					initialPlayerB.value.push(team.playerB[0])
				} else if (
					team.playerA.length === 0 &&
					team.playerB.length > 0 &&
					!competition.value?.playerB.different
				) {
					playersA.value.push(team.playerB[0])
					initialPlayerA.value.push(team.playerB[0])
				} else if (team.playerA.length > 0 && team.playerB.length > 0) {
					teams.value.push(team)
					initialTeam.value.push(JSON.parse(JSON.stringify(team)))
					playerCount.value++
				}
				playerCount.value++
			})
			await sleep(500)
			isUpdating.value = false
		})
}
</script>

<style scoped>
.team-enter-active {
	animation: enter 600ms;
}

.team-leave-active {
	animation: leave 400ms;
}

.playerB-enter-active {
	animation: enter 800ms;
}

.playerB-leave-active {
	animation: leave 200ms;
}

@keyframes enter {
	0% {
		visibility: hidden;
		width: 0;
		height: 0;
		overflow: hidden;
		transform: translateX(90vw);
	}
	66% {
		visibility: hidden;
		width: 30%;
		height: 30%;
		transform: translateX(90vw);
	}
	67% {
		transform: translateX(90vw);
	}
	100% {
	}
}

@keyframes leave {
	0% {
	}
	50% {
		transform: translateX(90vw);
	}
	100% {
		transform: translateX(90vw);
	}
}
</style>
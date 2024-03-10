<template>
	<div class="grid">
		<div class="col-4 flex flex-column gap-3">
			<Card>
				<!-- TODO i18n -->
				<template #title>
					<div class="flex flex-row justify-content-between">
						<div>Teams</div>
						<SplitButton
							:model="randomizeItems"
							class="w-fit"
							:label="t('ViewPrepare.editTeams.randomize')"
							@click="randomize"
						>
							<template #icon>
								<span class="material-icons mb-1" style="font-size: 1.2rem"
									>casino</span
								>
							</template>
						</SplitButton>
					</div>
				</template>
				<template #content>
					<TeamContainerDraggable
						v-if="competition"
						:animated="animated"
						:teams="teams"
						:competition="competition"
					/>
				</template>
			</Card>
		</div>
		<div class="col-2">
			<Card>
				<template #title>Sorted teams</template>
				<template #content>
					<DraggablePanel
						:list="sortedTeams"
						:put="true"
						item-key="id"
						:tag="TransitionGroup"
						:component-data="{
							tag: 'div',
							name: animated ? 'playerList' : 'default',
							type: 'transition',
						}"
						group="teams"
						class="flex flex-column gap-2 border-3 border-round border-dashed justify-content-center"
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
									<PlayerCard
										v-if="item.playerA"
										:player="item.playerA"
									></PlayerCard>
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
			</Card>
		</div>
		<div class="col-6">
			<Card class="w-full">
				<template #title>Tournament tree</template>
				<template #content>
					<ScrollPanel style="width: 100%; height: 500px">
						<ViewKnockoutTree
							v-if="competition && finale && thirdPlace"
							:mode="competition.mode"
							:match="finale"
							:thirdPlace="thirdPlace"
						/>
					</ScrollPanel>
				</template>
			</Card>
		</div>
	</div>
</template>

<script setup lang="ts">
import { useRoute } from "vue-router"
import { useToast } from "primevue/usetoast"
import { useI18n } from "vue-i18n"
import { computed, Ref, ref, TransitionGroup } from "vue"
import { getCompetitionDetails } from "@/backend/competition"
import TeamContainerDraggable from "@/components/views/prepare/assignMatches/TeamContainerDraggable.vue"
import {
	Team,
	teamClientToServer,
	teamServerToClient,
} from "@/interfaces/match"
import axios from "axios"
import { KnockoutOrder, Mode, Progress } from "@/interfaces/competition"
import TeamBox from "@/components/views/prepare/components/TeamBox.vue"
import DraggablePanel from "@/draggable/DraggablePanel.vue"
import PlayerCard from "@/components/views/prepare/components/PlayerCard.vue"
import { KnockoutMatch } from "@/interfaces/knockoutSystem"
import ViewKnockoutTree from "@/components/views/competition/knockoutSystem/ViewKnockoutTree.vue"

const route = useRoute()
const toast = useToast()
const { t } = useI18n({ inheritLocale: true })

const teams = ref<Team[]>([])
const sortedTeams = ref<Team[]>([])

const animated = ref<boolean>(false)

const duration = 2000
const teamCount = ref(0)
const delay = computed(() =>
	Math.min((duration * 2) / 3 / teamCount.value, 100),
)
const delayBetween = computed(() => delay.value / 2)

function sleep(milliseconds: number) {
	return new Promise((resolve) => setTimeout(resolve, milliseconds))
}

function $t(name: string) {
	return computed(() => t(name))
}

const competition = getCompetitionDetails(route, t, toast, {
	suc: () => {
		if (competition.value === null) return
		update()
	},
})

const randomizeItems = ref([
	{
		label: $t("ViewPrepare.editTeams.reroll"),
		icon: "pi pi-refresh",
		command: reroll,
	},
	{
		label: $t("ViewPrepare.editTeams.reset"),
		icon: "pi pi-times",
		command: reset,
	},
])

function selectRandomElement<T>(players: Ref<T[]>) {
	const r = Math.floor(Math.random() * players.value.length)
	const element = players.value[r]
	players.value.splice(r, 1)
	//players.value = players.value.filter((v, i) => i !== r)
	return element
}

async function randomize() {
	animated.value = true
	while (teams.value.length) {
		const element = selectRandomElement(teams)
		await sleep(delayBetween.value)
		sortedTeams.value.push(element)
		await sleep(delay.value)
	}
	animated.value = false
}

async function reroll() {
	await reset()
	await randomize()
}

async function reset() {
	animated.value = true
	while (sortedTeams.value.length) {
		let i = sortedTeams.value.length - 1
		const team = sortedTeams.value.splice(i, 1)[0]
		await sleep(delayBetween.value)
		teams.value.push(team)
		await sleep(delay.value)
	}
	animated.value = false
}

let firstUpdate = true

async function update() {
	animated.value = true
	teams.value = []
	sortedTeams.value = []
	const anFin = sleep(firstUpdate ? 0 : 500)
	firstUpdate = false
	axios
		.get<Team[]>(
			`tournament/${route.params.tourId}/competition/${route.params.compId}/signedUpTeams`,
		)
		.then(async (response) => {
			await anFin
			teamCount.value = 0
			response.data.forEach((team) => {
				teams.value.push(teamServerToClient(team))
				teamCount.value++
			})
			adjustUnsorted()
			await sleep(500)
			animated.value = false
		})
		.catch((error) => {
			console.log(error)
		})

	if (
		competition.value?.cProgress === Progress.GAMES ||
		competition.value?.cProgress === Progress.SCHEDULING
	) {
		axios
			.get<KnockoutOrder>(
				`tournament/${route.params.tourId}/competition/${route.params.compId}/knockoutOrder`,
			)
			.then(async (response) => {
				await anFin
				sortedTeams.value = response.data.teams.map((t) =>
					teamServerToClient(t),
				)
				adjustUnsorted()
			})
			.catch(() => {})
	}
}

function adjustUnsorted() {
	teams.value = teams.value.filter(
		(e) => sortedTeams.value.filter((t) => (t.id = e.id)).length === 0,
	)
}

function split(size: number, teams: Team[]): Team[][] {
	if (size === 0) {
		return [[teams[0]], [teams[1]]]
	}

	const count = 2 ** (size + 1)
	const first = split(size - 1, teams.slice(0, count / 2))
	const second = split(size - 1, teams.slice(count / 2, count).toReversed())
	return [
		first[0].concat(second[0].toReversed()),
		first[1].concat(second[1].toReversed()),
	]
}

function generateTree(
	size: number,
	teams: Team[],
	reversed = false,
): KnockoutMatch {
	const f = reversed ? 1 : 0
	const s = reversed ? 0 : 1

	const splits = split(size, teams)
	return {
		court: null,
		begin: null,
		end: null,
		finished: false,
		winner: null,
		teamA: size === 0 ? teams[f] : null,
		teamB: size === 0 ? teams[s] : null,
		sets: null,
		curGame: null,
		prevMatch:
			size > 0
				? {
						winner: true,
						a: generateTree(size - 1, splits[f]),
						b: generateTree(size - 1, splits[s], true),
					}
				: undefined,
	}
}

const size = computed(() =>
	Math.max(0, Math.ceil(Math.log2(teamCount.value)) - 1),
)
const finale = computed(() =>
	generateTree(
		size.value,
		sortedTeams.value.concat(
			new Array(2 ** (size.value + 1) - sortedTeams.value.length).fill(null),
		),
	),
)

const thirdPlace = computed(() => generateTree(0, []))

function save() {
	const req: KnockoutOrder = {
		teams: sortedTeams.value.map((t) => teamClientToServer(t)),
	}

	axios
		.post<boolean>(
			`/tournament/${route.params.tourId}/competition/${route.params.compId}/initKnockout`,
			req,
		)
		.then(() => {
			toast.add({
				severity: "success",
				summary: "Gspeichert",
				detail: "Ois subba",
				life: 3000,
				closable: false,
			})
		})
		.catch(() => {
			toast.add({
				severity: "error",
				summary: "Gfailed",
				detail: "Ned so guad",
				life: 3000,
				closable: false,
			})
		})
}

defineExpose({ save })
</script>

<style scoped></style>

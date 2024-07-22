<template>
	<div v-if="competition" class="grid">
		<div class="col-3 flex flex-column gap-3">
			<Card>
				<!-- TODO i18n -->
				<template #title>
					<div class="flex flex-row justify-content-between">
						<div>{{ t("ViewPrepare.assignMatches.teams") }}</div>
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
						v-if="competition && signedUp"
						:animated="animated"
						:teams="teams"
						:competition="competition"
					/>
				</template>
			</Card>
		</div>
		<div class="col-9">
			<Card class="w-full">
				<template #title>{{ t("general.tournament_tree") }}</template>
				<template #content>
					<ScrollPanel style="width: 100%; height: 1000px">
						<ViewKnockoutTree
							:finale="tree"
							:border-thickness="2"
							:border-radius="0"
						>
							<template #match="{ match, level }">
								<ViewMatchEdit v-if="level === 0" :match="match" />
								<ViewMatch v-else :match="match" :mode="competition.mode" />
							</template>
						</ViewKnockoutTree>
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
import { computed, Ref, ref, watch } from "vue"
import { getCompetitionDetails } from "@/backend/competition"
import TeamContainerDraggable from "@/components/views/prepare/components/TeamContainerDraggable.vue"
import { Team, teamClientToServer } from "@/interfaces/team"
import axios from "axios"
import { KnockoutOrder } from "@/interfaces/competition"
import { KnockoutMatch } from "@/interfaces/knockoutSystem"
import { getSignedUp } from "@/backend/signup"
import ViewKnockoutTree from "@/components/views/competition/knockoutSystem/ViewKnockoutTree.vue"
import ViewMatch from "@/components/views/competition/knockoutSystem/ViewMatch.vue"
import ViewMatchEdit from "@/components/views/prepare/assignMatches/ViewMatchEdit.vue"

const route = useRoute()
const toast = useToast()
const { t } = useI18n({ inheritLocale: true })

const { data: competition } = getCompetitionDetails(route, t, toast, {})
const { data: signedUp } = getSignedUp(route, t, toast)

const teams = ref<Team[]>([])

const tree = ref(genTree(0))
watch([signedUp], () => {
	if (!signedUp.value) return

	teams.value = []
	signedUp.value.forEach((t) => teams.value.push(t))
	tree.value = genTree(Math.ceil(Math.log2(signedUp.value.length)))
})

//  [row number in the tree][ upper or lower part of a bracket ][just in an array for draggable]
const assignedTeams = ref<Team[][][]>([])

function genTree(height: number): KnockoutMatch {
	const default_game = {
		court: null,
		begin: null,
		end: null,
		finished: false,
		winner: null,
		teamA: null,
		teamB: null,
		sets: null,
		curGame: null,
	}
	if (height === 0) return default_game

	return {
		...default_game,
		prevMatch: {
			winner: true,
			a: genTree(height - 1),
			b: genTree(height - 1),
		},
	}
}

const animated = ref<boolean>(true)

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
	return element
}

async function randomize() {
	animated.value = true
	let knockOutListIndex = 0
	while (teams.value.length) {
		const element = selectRandomElement(teams)
		await sleep(delayBetween.value)
		while (
			assignedTeams.value[knockOutListIndex][0].length === 1 &&
			assignedTeams.value[knockOutListIndex][1].length === 1
		) {
			knockOutListIndex++
		}
		if (assignedTeams.value[knockOutListIndex][0].length === 0) {
			assignedTeams.value[knockOutListIndex][0].push(element)
		} else {
			assignedTeams.value[knockOutListIndex][1].push(element)
			knockOutListIndex++
		}
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
	for (let i = 0; i < assignedTeams.value.length; i++) {
		let team1 = assignedTeams.value[i][0].pop()
		let team2 = assignedTeams.value[i][1].pop()
		if (team1) teams.value.push(team1)
		if (team2) teams.value.push(team2)
		await sleep(delay.value)
		console.log(assignedTeams.value.length)
	}

	animated.value = false
}

let firstUpdate = true

// async function update() {
// 	animated.value = true
// 	teams.value = []
// 	while (assignedTeams.value.length > 0) {
// 		assignedTeams.value.pop()
// 	}
// 	// const anFin = sleep(firstUpdate ? 0 : 500)
// 	firstUpdate = false
// 	teamCount.value = 0
// 	if (
// 		competition.value?.cProgress === Progress.GAMES ||
// 		competition.value?.cProgress === Progress.SCHEDULING
// 	) {
// 		axios
// 			.get<KnockoutOrder>(
// 				`tournament/${route.params.tourId}/competition/${route.params.compId}/knockoutOrder`,
// 			)
// 			.then(async (response) => {
// 				let sortedTeams = response.data.teams.map((t) => teamServerToClient(t))
// 				teamCount.value += sortedTeams.length
// 				// initialize the knockout order
// 				for (let i = 0; i < nearestPowerOf2(sortedTeams.length) / 2; i++) {
// 					assignedTeams.value.push([
// 						[sortedTeams[i * 2]],
// 						[sortedTeams[i * 2 + 1]],
// 					])
// 				}
// 				animated.value = false
// 			})
// 			.catch(() => {})
// 	} else {
// 		axios
// 			.get<Team[]>(
// 				`tournament/${route.params.tourId}/competition/${route.params.compId}/signedUpTeams`,
// 			)
// 			.then(async (response) => {
// 				// await anFin
// 				response.data.forEach((team) => {
// 					teams.value.push(teamServerToClient(team))
// 					teamCount.value++
// 				})
// 				// await sleep(500)
// 				if (competition.value?.cProgress === Progress.TEAMS) {
// 					for (let i = 0; i < nearestPowerOf2(teams.value.length) / 2; i++) {
// 						assignedTeams.value.push([[], []])
// 					}
// 				}
// 				animated.value = false
// 			})
// 			.catch((error) => {
// 				console.log(error)
// 			})
// 	}
// }

// function nearestPowerOf2(n: number): number {
// 	return 1 << (31 - Math.clz32(n))
// }

const size = computed(() =>
	Math.max(0, Math.ceil(Math.log2(teamCount.value)) - 1),
)

function knockoutTreeCompletelyAssigned(): boolean {
	console.log(assignedTeams.value)
	for (let i = 0; i < assignedTeams.value.length; i++) {
		if (
			assignedTeams.value[i][0].length === 0 ||
			assignedTeams.value[i][1].length === 0
		) {
			return false
		}
	}
	return true
}

function save() {
	if (!knockoutTreeCompletelyAssigned()) {
		toast.add({
			severity: "error",
			summary: t("general.failure"),
			detail: t("ViewPrepare.assignMatches.knockout_not_assigned"),
			life: 3000,
			closable: false,
		})
		return
	}

	const req: KnockoutOrder = {
		teams: assignedTeams.value
			.flat()
			.flat()
			.map((t) => teamClientToServer(t)),
	}

	axios
		.post<boolean>(
			`/tournament/${route.params.tourId}/competition/${route.params.compId}/initKnockout`,
			req,
		)
		.then(() => {
			toast.add({
				severity: "success",
				summary: t("general.success"),
				detail: t("general.saved"),
				life: 3000,
				closable: false,
			})
		})
		.catch(() => {
			toast.add({
				severity: "error",
				summary: t("general.failure"),
				detail: t("general.save_failed"),
				life: 3000,
				closable: false,
			})
		})
}

defineExpose({ save })
</script>

<style scoped></style>

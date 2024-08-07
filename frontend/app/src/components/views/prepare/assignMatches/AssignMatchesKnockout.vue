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
							<template
								#match="{
									match,
									level,
									'onUpdate:teamA': updateTeamA,
									'onUpdate:teamB': updateTeamB,
								}"
							>
								<ViewMatchEdit
									v-if="level === 0"
									:match="match"
									@update:team-a="updateTeamA"
									@update:team-b="updateTeamB"
								/>
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
import { Team } from "@/interfaces/team"
import axios from "axios"
import {
	KnockoutMatch,
	knockoutMatchClientToServer,
} from "@/interfaces/knockoutSystem"
import { getSignedUp } from "@/backend/signup"
import ViewKnockoutTree from "@/components/views/competition/knockoutSystem/ViewKnockoutTree.vue"
import ViewMatch from "@/components/views/competition/knockoutSystem/ViewMatch.vue"
import ViewMatchEdit from "@/components/views/prepare/assignMatches/ViewMatchEdit.vue"
import { Match } from "@/interfaces/match"

const route = useRoute()
const toast = useToast()
const { t } = useI18n({ inheritLocale: true })

let firstUpdate = true
let treeDepth = 0
const { data: competition } = getCompetitionDetails(route, t, toast, {
	suc: () => {
		if (competition.value === null) return
		loadFromServer()
	},
})
const { data: signedUp, isPlaceholderData: signedUpPlaceholder } = getSignedUp(
	route,
	t,
	toast,
)

const teams = ref<Team[]>([])

const tree = ref(genTree(0))
watch([signedUp], loadFromServer)
if (!signedUpPlaceholder.value) loadFromServer()

async function loadFromServer() {
	if (!signedUp.value) return

	teams.value = []
	treeDepth = Math.ceil(Math.log2(signedUp.value.length)) - 1
	tree.value = genTree(treeDepth)

	await sleep(firstUpdate ? 0 : 400)
	firstUpdate = false

	signedUp.value.forEach((t) => teams.value.push(t))
}

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
	if (players.value.length === 0) return null

	const r = Math.floor(Math.random() * players.value.length)
	const element = players.value[r]
	players.value.splice(r, 1)
	return element
}

function getMatches(): Match[] {
	let cur = [tree.value]
	for (let i = 0; i < treeDepth; i++) {
		cur = cur
			.map((m) => (m.prevMatch ? [m.prevMatch.a, m.prevMatch.b] : []))
			.flat()
	}
	return cur
}

async function randomize() {
	animated.value = true
	let knockOutListIndex = 0
	const matches = getMatches()
	while (teams.value.length && knockOutListIndex < matches.length) {
		if (!matches[knockOutListIndex].teamA) {
			const teamA = selectRandomElement(teams)
			await sleep(delayBetween.value)

			matches[knockOutListIndex].teamA = teamA
			await sleep(delay.value)
		}

		if (!matches[knockOutListIndex].teamB) {
			const teamB = selectRandomElement(teams)
			if (!teamB) break
			matches[knockOutListIndex].teamB = teamB
			await sleep(delayBetween.value)
		}

		knockOutListIndex++
		await sleep(delay.value)
	}
	animated.value = false
	console.log(tree.value)
}

async function reroll() {
	await reset()
	await randomize()
}

async function reset() {
	// TODO adapt to new structure
	animated.value = true

	animated.value = false
}

// TODO adapt to new structure
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

// const size = computed(() =>
// 	Math.max(0, Math.ceil(Math.log2(teamCount.value)) - 1),
// )

function knockoutTreeCompletelyAssigned(): boolean {
	// check that tree is completely assigned
	const check = (m: KnockoutMatch): boolean => {
		if (m.prevMatch) {
			return check(m.prevMatch.a) && check(m.prevMatch.b)
		} else return m.teamA !== null && m.teamB !== null
	}
	return check(tree.value)
}

function save() {
	// TODO send new save as a tree?
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

	console.log("tree", tree.value)
	console.log("tree: ", knockoutMatchClientToServer(tree.value))
	axios
		.post<boolean>(
			`/tournament/${route.params.tourId}/competition/${route.params.compId}/initKnockout`,
			knockoutMatchClientToServer(tree.value),
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

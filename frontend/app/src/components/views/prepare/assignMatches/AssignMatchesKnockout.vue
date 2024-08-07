<template>
	<div v-if="competition" class="grid">
		<div class="col-3 flex flex-column gap-3">
			<Card>
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
					<ScrollPanel style="width: 100%; height: 500px">
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
									:animated="animated"
									@update:team-a="updateTeamA"
									@update:team-b="updateTeamB"
								/>
								<ViewMatch v-else :match="match" :mode="Mode.SINGLE" />
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
import { KnockoutMatch } from "@/interfaces/knockoutSystem"
import { getSignedUp } from "@/backend/signup"
import ViewKnockoutTree from "@/components/views/competition/knockoutSystem/ViewKnockoutTree.vue"
import ViewMatch from "@/components/views/competition/knockoutSystem/ViewMatch.vue"
import ViewMatchEdit from "@/components/views/prepare/assignMatches/ViewMatchEdit.vue"
import { Match } from "@/interfaces/match"
import { getKnockout, useInitKnockout } from "@/backend/knockout"
import { Mode } from "@/interfaces/competition"

const route = useRoute()
const toast = useToast()
const { t } = useI18n({ inheritLocale: true })

// TODO refactoring for the whole file for smoother loading and clearer code

let loadFromServerRunning = false
let loadFromServerRefresh = false
let firstUpdate = true
let treeDepth = 0
const { data: competition } = getCompetitionDetails(route, t, toast, {})
const { data: signedUp, isPlaceholderData: signedUpPlaceholder } = getSignedUp(
	route,
	t,
	toast,
)
const { data: knockoutSystem } = getKnockout(route)
const { mutate: initKnockout } = useInitKnockout(route, t, toast)
const animated = ref<boolean>(true)

const teams = ref<Team[]>([])

const tree = ref(
	genTree(treeDepth, knockoutSystem.value ? knockoutSystem.value.finale : null),
)
watch([signedUp, competition, knockoutSystem], loadFromServer)
// if (!signedUpPlaceholder.value && knockoutSystem.value) loadFromServer()

async function loadFromServer() {
	if (!signedUp.value || signedUpPlaceholder.value || !knockoutSystem.value)
		return

	if (loadFromServerRunning) {
		loadFromServerRefresh = true
		return
	}
	loadFromServerRunning = true
	loadFromServerRefresh = false

	animated.value = true
	treeDepth = Math.ceil(Math.log2(signedUp.value.length)) - 1
	teams.value = []
	tree.value = genTree(treeDepth, null)
	await sleep(firstUpdate ? 0 : 400)
	firstUpdate = false
	tree.value = genTree(
		treeDepth,
		knockoutSystem.value ? knockoutSystem.value.finale : null,
	)
	signedUp.value.forEach((t) => teams.value.push(t))
	getMatches().forEach((m) => {
		teams.value = teams.value.filter(
			(t) => t.id !== m.teamA?.id && t.id !== m.teamB?.id,
		)
	})
	await sleep(400)
	animated.value = false

	loadFromServerRunning = false
	if (loadFromServerRefresh) {
		loadFromServerRefresh = false
		await loadFromServer()
	}
}

function genTree(height: number, tree: KnockoutMatch | null): KnockoutMatch {
	const default_game = {
		court: null,
		begin: null,
		end: null,
		finished: false,
		winner: null,
		teamA: tree ? tree.teamA : null,
		teamB: tree ? tree.teamB : null,
		sets: null,
		curGame: null,
	}
	if (height === 0) return default_game

	return {
		...default_game,
		prevMatch: {
			winner: true,
			a: genTree(height - 1, tree?.prevMatch ? tree.prevMatch.a : null),
			b: genTree(height - 1, tree?.prevMatch ? tree.prevMatch.b : null),
		},
	}
}

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
}

async function reset() {
	animated.value = true
	const matches = getMatches()
	matches.forEach((m) => {
		m.teamA = null
		m.teamB = null
	})
	await loadFromServer()
	await sleep(400)
}

async function reroll() {
	await reset()
	await randomize()
}

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

	initKnockout(tree.value)
}

defineExpose({ save })
</script>

<style scoped></style>

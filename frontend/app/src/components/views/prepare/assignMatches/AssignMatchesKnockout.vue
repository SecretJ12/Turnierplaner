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
						v-if="competition && signedUpTeams"
						:animated="isUpdating"
						:teams="data.teams"
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
							:finale="data.tree"
							:third-place="thirdPlace ?? undefined"
							:border-thickness="2"
							:border-radius="0"
							:titles="knockoutTitle(t)"
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
									:animated="isUpdating"
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
import { computed, Ref, ref } from "vue"
import { getCompetitionDetails } from "@/backend/competition"
import TeamContainerDraggable from "@/components/views/prepare/components/TeamContainerDraggable.vue"
import { Team } from "@/interfaces/team"
import { KnockoutMatch, KnockoutSystem } from "@/interfaces/knockoutSystem"
import { getSignedUp } from "@/backend/signup"
import ViewKnockoutTree from "@/components/views/competition/knockoutSystem/ViewKnockoutTree.vue"
import ViewMatch from "@/components/views/competition/knockoutSystem/ViewMatch.vue"
import ViewMatchEdit from "@/components/views/prepare/assignMatches/ViewMatchEdit.vue"
import { Match } from "@/interfaces/match"
import { getKnockout, useInitKnockout } from "@/backend/knockout"
import { Mode } from "@/interfaces/competition"
import {
	genRandomizeItems,
	selectRandomElement,
} from "@/components/views/prepare/assignMatches/AssginMatchesHelper"
import { sleep, track } from "@/backend/Tracker"
import { knockoutTitle } from "@/components/views/competition/knockoutSystem/KnockoutTitleGenerator"

const route = useRoute()
const toast = useToast()
const { t } = useI18n({ inheritLocale: true })
const randomizeItems = genRandomizeItems(t, reroll, reset)

const isUpdating = defineModel<boolean>("isUpdating", { default: false })

const { data: competition } = getCompetitionDetails(route, t, toast)
const { data: signedUpTeams, isLoading: signedUpLoading } = getSignedUp(
	route,
	t,
	toast,
)
const { data: knockoutSystem, isLoading: knockoutSystemLoading } =
	getKnockout(route)
const { mutate: initKnockout } = useInitKnockout(route, t, toast)

const { data, reload } = track(
	loadFromServer,
	{
		teams: [],
		tree: genTree(0, null),
		treeDepth: 0,
		teamCount: 0,
	},
	{ signedUpTeams, knockoutSystem },
	[signedUpTeams, knockoutSystem],
	[signedUpLoading, knockoutSystemLoading],
	isUpdating,
	400,
)
const thirdPlace = computed(() => {
	if (signedUpTeams.value && signedUpTeams.value.length > 2)
		return genTree(0, null)
	else return null
})

function loadFromServer({
	signedUpTeams,
	knockoutSystem,
}: {
	signedUpTeams: Ref<undefined> | Ref<Team[]>
	knockoutSystem: Ref<undefined> | Ref<KnockoutSystem>
}) {
	if (!signedUpTeams.value || !knockoutSystem.value) return null

	const treeDepth = Math.ceil(Math.log2(signedUpTeams.value.length)) - 1
	let teams: Team[] = []
	const tree = genTree(
		treeDepth,
		knockoutSystem.value ? knockoutSystem.value.finale : null,
	)
	signedUpTeams.value.forEach((t) => teams.push(t))
	if (knockoutSystem.value.finale)
		getMatches(knockoutSystem.value.finale, treeDepth).forEach((m) => {
			teams = teams.filter((t) => t.id !== m.teamA?.id && t.id !== m.teamB?.id)
		})
	const teamCount = signedUpTeams.value.length
	return {
		teams,
		treeDepth,
		tree,
		teamCount,
	}
}

const duration = 2000
const teamCount = ref(0)
const delay = computed(() =>
	Math.min((duration * 2) / 3 / teamCount.value, 100),
)
const delayBetween = computed(() => delay.value / 2)

async function randomize() {
	isUpdating.value = true
	let knockOutListIndex = 0
	const matches = getMatches()
	while (data.value.teams.length && knockOutListIndex < matches.length) {
		if (!matches[knockOutListIndex].teamA) {
			const teamA = selectRandomElement(data.value.teams)
			await sleep(delayBetween.value)

			matches[knockOutListIndex].teamA = teamA
			await sleep(delay.value)
		}

		if (!matches[knockOutListIndex].teamB) {
			const teamB = selectRandomElement(data.value.teams)
			if (!teamB) break
			matches[knockOutListIndex].teamB = teamB
			await sleep(delayBetween.value)
		}

		knockOutListIndex++
		await sleep(delay.value)
	}
	isUpdating.value = false
}

async function reset() {
	isUpdating.value = true
	const matches = getMatches()
	matches.forEach((m) => {
		if (m.teamA) data.value.teams.push(m.teamA)
		if (m.teamB) data.value.teams.push(m.teamB)
		m.teamA = null
		m.teamB = null
	})
	await sleep(400)
}

async function reroll() {
	await reset()
	await randomize()
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

	initKnockout(data.value.tree)
}

function knockoutTreeCompletelyAssigned(): boolean {
	// check that tree is completely assigned
	const check = (m: KnockoutMatch): boolean => {
		if (m.prevMatch) {
			return check(m.prevMatch.a) && check(m.prevMatch.b)
		} else return m.teamA !== null && m.teamB !== null
	}
	return check(data.value.tree)
}

function getMatches(tree?: KnockoutMatch, treeDepth?: number): Match[] {
	let cur = [tree ?? data.value.tree]
	for (let i = 0; i < (treeDepth ?? data.value.treeDepth); i++) {
		cur = cur
			.map((m) => (m.prevMatch ? [m.prevMatch.a, m.prevMatch.b] : []))
			.flat()
	}
	return cur
}

function genTree(height: number, tree: KnockoutMatch | null): KnockoutMatch {
	const default_game: KnockoutMatch = {
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

defineExpose({ save, reload })
</script>

<style scoped></style>

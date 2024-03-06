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
							:disabled="disabled"
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
		<div class="col-8 flex flex-column gap-3">
			<template v-if="competition?.tourType === TourType.GROUPS">
				<Card v-for="(group, i) in groups" :key="i">
					<template #title>Group {{ i + 1 }}</template>
					<template #content>
						<TeamContainerDraggable
							v-if="competition"
							:animated="animated"
							:teams="group"
							:competition="competition"
						/>
					</template>
				</Card>
				<div class="flex flex-row gap-2">
					<Button
						:disabled="noGroups <= 2 || disabled"
						severity="danger"
						@click="
							() => {
								noGroups /= 2
								adjustSize(noGroups)
							}
						"
					>
						Remove
					</Button>
					<Button
						:disabled="noGroups >= 64 || disabled"
						severity="success"
						@click="
							() => {
								noGroups *= 2
								adjustSize(noGroups)
							}
						"
						>Add
					</Button>
				</div>
			</template>
			<template v-else>
				<Card v-if="competition">
					<template #content>
						<ViewKnockoutTree
							:mode="competition.mode"
							:match="knockoutSystem.finale"
							:third-place="knockoutSystem.thirdPlace"
						/>
					</template>
				</Card>
			</template>
		</div>
	</div>
</template>

<script setup lang="ts">
import { useRoute } from "vue-router"
import { useI18n } from "vue-i18n"
import { useToast } from "primevue/usetoast"
import { KnockoutMatch } from "@/interfaces/knockoutSystem"
import { computed, Ref, ref } from "vue"
import axios from "axios"
import { Team, teamServerToClient } from "@/interfaces/match"
import TeamContainerDraggable from "@/components/views/prepare/assignMatches/TeamContainerDraggable.vue"
import { TourType } from "@/interfaces/competition"
import ViewKnockoutTree from "@/components/views/competition/knockoutSystem/ViewKnockoutTree.vue"
import { getCompetitionDetails } from "@/backend/competition"

const route = useRoute()
const toast = useToast()
const { t } = useI18n({ inheritLocale: true })

function $t(name: string) {
	return computed(() => t(name))
}

const competition = getCompetitionDetails(route, t, toast, {
	suc: () => {
		if (competition.value === null) return
		update()
	},
})

const noGroups = ref<number>(2)

const teams = ref<Team[]>([])
const groups = ref<Team[][]>([[], []])
const disabled = ref<boolean>(false)
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

function adjustSize(size: number) {
	disabled.value = true
	if (groups.value.length > size) {
		for (let i = size; i < groups.value.length; i++) {
			groups.value[i].forEach((e) => teams.value.push(e))
		}
		groups.value.splice(size, groups.value.length - size)
	} else {
		groups.value.splice(
			groups.value.length,
			0,
			...Array.from({ length: size - groups.value.length }, () => []),
		)
	}
	disabled.value = false
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
	//players.value = players.value.filter((v, i) => i !== r)
	return element
}

async function randomize() {
	animated.value = true
	while (teams.value.length) {
		const min = Math.min(...groups.value.map((group) => group.length))
		const minInd = groups.value.findIndex((group) => group.length === min)

		const element = selectRandomElement(teams)
		await sleep(delayBetween.value)
		groups.value[minInd].push(element)
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
	for (const group of groups.value) {
		while (group.length) {
			let i = group.length - 1
			const team = group.splice(i, 1)[0]
			await sleep(delayBetween.value)
			teams.value.push(team)
			await sleep(delay.value)
		}
	}
	animated.value = false
}

function generateTree(size: number): KnockoutMatch {
	return {
		court: "a",
		begin: new Date(),
		end: new Date(),
		finished: false,
		winner: null,
		teamA: null,
		teamB: null,
		sets: null,
		curGame: null,
		prevMatch:
			size > 0
				? {
						winner: true,
						a: generateTree(size - 1),
						b: generateTree(size - 1),
					}
				: undefined,
	}
}

const knockoutSystem = {
	finale: generateTree(1),
	thirdPlace: generateTree(0),
}

let firstUpdate = true
async function update() {
	animated.value = true
	disabled.value = true
	teams.value = []
	const anFin = sleep(firstUpdate ? 0 : 400)
	firstUpdate = false
	axios
		.get<Team[]>(
			`tournament/${route.params.tourId}/competition/${route.params.compId}/signedUpTeams`,
		)
		.then(async (response) => {
			await anFin
			response.data.forEach((team) => {
				teams.value.push(teamServerToClient(team))
				teamCount.value++
			})
			await sleep(400)
			animated.value = false
			disabled.value = false
		})
		.catch((error) => {
			console.log(error)
		})
}

function save() {
	console.log("save")
}

defineExpose({save})
</script>

<style scoped></style>

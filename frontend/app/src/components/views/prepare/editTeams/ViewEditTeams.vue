<template>
	<div class="flex flex-column gap-5">
		<div class="flex justify-content-around">
			<div class="flex flex-row gap-2">
				<Button label="Register Player 1"> </Button>
				<div @click="toggleA">
					<DraggablePanel
						:list="deletedPlayerA"
						:put="['playersA']"
						group="deletedPlayerA"
						class="flex"
						:component-data="{}"
						item-key="id"
						:tag="TransitionGroup"
						bin
					>
					</DraggablePanel>
				</div>
				<OverlayPanel ref="showDeletedPlayerA">
					<DraggablePanel
						:list="deletedPlayerA"
						:put="['deletedPlayerA', 'playersA']"
						group="deletedPlayerA"
						class="flex"
						item-key="id"
						:tag="TransitionGroup"
						:component-data="{
							tag: 'div',
							name: 'default',
							type: 'transition',
						}"
					>
						<template #default="{ item }">
							<div
								class="border-round select-none bg-primary-400 cursor-pointer inline p-3 text-50"
							>
								{{ item.value }}
							</div>
						</template>
					</DraggablePanel>
				</OverlayPanel>
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
				<Button label="Register Player 2" />
				<div @click="toggleB">
					<DraggablePanel
						:list="deletedPlayerB"
						:put="['playersB']"
						group="deletedPlayerB"
						class="flex"
						:component-data="{}"
						item-key="id"
						:tag="TransitionGroup"
						bin
					>
					</DraggablePanel>
				</div>
				<OverlayPanel ref="showDeletedPlayerB">
					<DraggablePanel
						:list="deletedPlayerB"
						:put="['playersB', 'deletedPlayerB']"
						group="deletedPlayerB"
						class="flex"
						item-key="id"
						:tag="TransitionGroup"
						:component-data="{
							tag: 'div',
							name: 'default',
							type: 'transition',
						}"
					>
						<template #default="{ item }">
							<div
								class="border-round select-none bg-yellow-500 cursor-pointer inline p-3 text-50"
							>
								{{ item.value }}
							</div>
						</template>
					</DraggablePanel>
				</OverlayPanel>
			</div>
		</div>
		<div class="flex flex-row justify-content-around">
			<Fieldset legend="Team A" class="flex-1">
				<DraggablePanel
					id="playerA"
					:list="playersA"
					:put="
						competition?.playerB.different
							? ['playersA', 'deletedPlayerA']
							: ['playersA', 'playersB', 'deletedPlayerA']
					"
					item-key="id"
					:tag="TransitionGroup"
					:component-data="{
						tag: 'div',
						name: 'default',
						type: 'transition',
					}"
					group="playersA"
					class="flex flex-wrap gap-2 border-3 min-h-3rem border-round border-dashed"
					style="min-height: 4em"
				>
					<template #default="{ item }">
						<div
							class="border-round select-none bg-primary-400 cursor-pointer inline p-3 text-50"
						>
							{{ item.value }}
						</div>
					</template>
				</DraggablePanel>
			</Fieldset>
			<Fieldset
				v-if="competition?.mode === Mode.DOUBLE"
				legend="team"
				class="flex-1"
			>
				<DraggablePanel
					:list="teams"
					:put="[
						'teams',
						'playersA',
						'playersB',
						'deletedPlayerA',
						'deletedPlayerB',
					]"
					item-key="id"
					:tag="TransitionGroup"
					:component-data="{
						tag: 'div',
						name: 'default',
						type: 'transition',
					}"
					group="teams"
					class="flex flex-wrap inline-block gap-2 border-3 border-round border-dashed"
					style="min-height: 4em"
					wrap
				>
					<template #default="{ item: outerItem }">
						<div style="grid-auto-rows: 1fr">
							<DraggablePanel
								:list="outerItem.playerA"
								:put="['playersA', 'deletedPlayerA']"
								item-key="id"
								:tag="TransitionGroup"
								single
								:component-data="{
									tag: 'div',
									name: 'default',
									type: 'transition',
								}"
								group="playersA"
								class="gap-2 align-items-center bg-blue-50 justify-content-center border border-dashed"
								style="min-width: 150px; min-height: 50px"
								hook
								@onRemove="cleanUpTeams"
							>
								<template #default="{ item: innerItem }">
									<div
										class="border-round select-none bg-primary-400 cursor-pointer p-3 text-50"
									>
										{{ innerItem.value }}
									</div>
								</template>
							</DraggablePanel>
							<DraggablePanel
								:list="outerItem.playerB"
								:put="
									competition?.playerB.different
										? ['playersB', 'deletedPlayerB']
										: ['playersA', 'playersB', 'deletedPlayerA']
								"
								item-key="id"
								:tag="TransitionGroup"
								single
								:component-data="{
									tag: 'div',
									name: 'default',
									type: 'transition',
								}"
								group="playersB"
								class="bg-red-50 border-round border-dashed"
								style="min-width: 150px; min-height: 50px"
								hook
								@onRemove="cleanUpTeams"
							>
								<template #default="{ item: innerItem }">
									<div
										class="border-round select-none cursor-pointer p-3 text-50 w-full h-full"
										:class="{
											'bg-yellow-500': competition?.playerB.different,
											'bg-primary-400': !competition?.playerB.different,
										}"
									>
										{{ innerItem.value }}
									</div>
								</template>
							</DraggablePanel>
						</div>
					</template>
				</DraggablePanel>
			</Fieldset>
			<Fieldset
				v-if="competition?.playerB.different"
				legend="Team B"
				class="flex-1"
			>
				<DraggablePanel
					id="playerB"
					:list="playersB"
					:put="['playersB', 'deletedPlayerB']"
					item-key="id"
					:tag="TransitionGroup"
					:component-data="{
						tag: 'div',
						name: 'default',
						type: 'transition',
					}"
					group="playersB"
					class="flex flex-wrap gap-2 border-3 min-h-3rem border-round border-dashed"
					style="min-height: 4em"
				>
					<template #default="{ item }">
						<div
							class="border-round select-none cursor-pointer inline p-3 text-50"
							:class="{
								'bg-yellow-500': competition?.playerB.different,
								'bg-primary-400': !competition?.playerB.different,
							}"
						>
							{{ item.value }}
						</div>
					</template>
				</DraggablePanel>
			</Fieldset>
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
import { computed, ref, TransitionGroup } from "vue"
import axios from "axios"
import { useRoute, useRouter } from "vue-router"
import { useToast } from "primevue/usetoast"
import { useI18n } from "vue-i18n"
import { searchedPlayer, TeamArray } from "@/interfaces/player"
import DraggablePanel from "@/draggable/DraggablePanel.vue"
import { getCompetitionDetails } from "@/backend/competition"
import Button from "primevue/button"
import { Mode } from "@/interfaces/competition"

const route = useRoute()
const router = useRouter()
const toast = useToast()
const { t } = useI18n({ inheritLocale: true })

const isSorting = ref<boolean>(false)

// for restoring the initial state
const initialTeam = ref<TeamArray[]>([])
const initialPlayerA = ref<searchedPlayer[]>([])
const initialPlayerB = ref<searchedPlayer[]>([])

const teams = ref<TeamArray[]>([])
const playersA = ref<searchedPlayer[]>([])
const playersB = ref<searchedPlayer[]>([])

const deletedPlayerA = ref<searchedPlayer[]>([])
const deletedPlayerB = ref<searchedPlayer[]>([])

const showDeletedPlayerA = ref()
const showDeletedPlayerB = ref()

const competition = getCompetitionDetails(route, t, toast, {
	suc: () => {
		update()
	},
})

console.log(playersA.value)

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

// update()

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
				if (team.playerA !== null && team.playerB === null) {
					playersA.value.push({
						id: team.playerA.id,
						firstName: team.playerA.firstName,
						lastName: team.playerA.lastName,
						value: team.playerA.firstName + " " + team.playerA.lastName,
					})
					initialPlayerA.value.push({
						id: team.playerA.id,
						firstName: team.playerA.firstName,
						lastName: team.playerA.lastName,
						value: team.playerA.firstName + " " + team.playerA.lastName,
					})
				} else if (team.playerA === null && team.playerB !== null) {
					playersB.value.push({
						id: team.playerB.id,
						firstName: team.playerB.firstName,
						lastName: team.playerB.lastName,
						value: team.playerB.firstName + " " + team.playerB.lastName,
					})
					initialPlayerB.value.push({
						id: team.playerB.id,
						firstName: team.playerB.firstName,
						lastName: team.playerB.lastName,
						value: team.playerB.firstName + " " + team.playerB.lastName,
					})
				} else if (team.playerA !== null && team.playerB !== null) {
					teams.value.push({
						id: team.id,
						playerA: [
							{
								id: team.playerA.id,
								firstName: team.playerA.firstName,
								lastName: team.playerA.lastName,
								value: team.playerA.firstName + " " + team.playerA.lastName,
							},
						],
						playerB: [
							{
								id: team.playerB.id,
								firstName: team.playerB.firstName,
								lastName: team.playerB.lastName,
								value: team.playerB.firstName + " " + team.playerB.lastName,
							},
						],
					})
					initialTeam.value.push({
						id: team.id,
						playerA: [
							{
								id: team.playerA.id,
								firstName: team.playerA.firstName,
								lastName: team.playerA.lastName,
								value: team.playerA.firstName + " " + team.playerA.lastName,
							},
						],
						playerB: [
							{
								id: team.playerB.id,
								firstName: team.playerB.firstName,
								lastName: team.playerB.lastName,
								value: team.playerB.firstName + " " + team.playerB.lastName,
							},
						],
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

const toggleA = (event) => {
	showDeletedPlayerA.value.toggle(event)
}

const toggleB = (event) => {
	showDeletedPlayerB.value.toggle(event)
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
</script>

<style scoped></style>

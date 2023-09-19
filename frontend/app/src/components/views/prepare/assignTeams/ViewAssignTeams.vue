<template>
	<template
		v-if="
			competition?.mode === Mode.SINGLE ||
			competition?.signUp === SignUp.TOGETHER
		"
	>
		<!-- TODO internalization -->
		There's nothing to do for this configuration
	</template>
	<template v-else>
		<div class="grid">
			<div class="col-5 flex flex-column gap-4">
				<SplitButton
					label="Randomize"
					severity="help"
					:model="randomizeItems"
					class="w-fit"
					@click="randomize"
					:disabled="isSorting"
				>
					<template #icon>
						<span class="material-icons mb-1" style="font-size: 1.2rem"
							>casino</span
						>
					</template>
				</SplitButton>
				<Fieldset
					:legend="competition?.playerB.different ? 'Player 1' : 'Player'"
				>
					<DraggablePanel
						:list="playersA"
						:put="['playersA']"
						item-key="name"
						:tag="TransitionGroup"
						:component-data="{
							tag: 'div',
							name: isSorting ? 'playerList' : 'default',
							type: 'transition',
						}"
						group="playersA"
						class="flex flex-row flex-row flex-wrap gap-2 border-3 min-h-3rem border-round border-dashed"
					>
						<template #default="{ item }">
							<PlayerCard :key="(<signedUpPlayer>item).name" :player="item" />
						</template>
					</DraggablePanel>
				</Fieldset>
				<Fieldset v-if="competition?.playerB.different" legend="Player 2">
					<DraggablePanel
						:list="playersB"
						:put="['playersB']"
						item-key="name"
						:tag="TransitionGroup"
						:component-data="{
							tag: 'div',
							name: isSorting ? 'playerList' : 'default',
							type: 'transition',
						}"
						group="playersB"
						class="flex flex-row flex-row flex-wrap gap-2 border-3 min-h-3rem border-round border-dashed"
					>
						<template #default="{ item }">
							<PlayerCard
								:key="(<signedUpPlayer>item).name"
								:player="item"
								secondary
							/>
						</template>
					</DraggablePanel>
				</Fieldset>
			</div>
			<div class="col-7 flex flex-column gap-4">
				<DataTable :value="teams" show-gridlines striped-rows size="small">
					<Column class="w-6" header="Player 1" field="name">
						<template #body="{ index }">
							<div
								:class="{
									'flex justify-content-center':
										teams[index].playerA.length === 0,
								}"
							>
								<DraggablePanel
									:list="teams[index].playerA"
									:put="['playersA']"
									item-key="name"
									:tag="TransitionGroup"
									:component-data="{
										tag: 'div',
										name: isSorting ? 'playerList' : 'default',
										type: 'transition',
									}"
									single
									group="playersA"
									class="flex align-items-center justify-content-center border-round"
									:class="{
										'h-3rem border-dashed w-10rem':
											teams[index].playerA.length === 0,
									}"
								>
									<template #default="{ item }">
										<PlayerCard
											:key="(<signedUpPlayer>item).name"
											:player="item"
										/>
									</template>
								</DraggablePanel>
							</div>
						</template>
					</Column>
					<Column class="w-6" header="Player 2" field="name">
						<template #body="{ index }">
							<div
								:class="{
									'flex justify-content-center':
										teams[index].playerB.length === 0,
								}"
							>
								<DraggablePanel
									:list="teams[index].playerB"
									:put="
										competition?.playerB.different ? ['playersB'] : ['playersA']
									"
									item-key="name"
									:tag="TransitionGroup"
									:component-data="{
										tag: 'div',
										name: isSorting ? 'playerList' : 'default',
										type: 'transition',
									}"
									single
									:group="
										competition?.playerB.different ? 'playersB' : 'playersA'
									"
									class="flex align-items-center justify-content-center border-round"
									:class="{
										'h-3rem border-dashed w-10rem':
											teams[index].playerB.length === 0,
									}"
								>
									<template #default="{ item }">
										<PlayerCard
											:key="(<signedUpPlayer>item).name"
											:player="item"
											:secondary="competition?.playerB.different"
										/>
									</template>
								</DraggablePanel>
							</div>
						</template>
					</Column>
				</DataTable>
			</div>
		</div>
	</template>

	<div class="mt-2 grid grid-nogutter justify-content-between">
		<Button
			label="Back"
			icon="pi pi-angle-left"
			icon-pos="left"
			@click="previousPage"
		/>
		<!-- TODO add @click -->
		<Button
			v-if="
				!(
					competition?.mode === Mode.SINGLE ||
					competition?.signUp === SignUp.TOGETHER
				)
			"
			:label="t('general.save')"
		>
		</Button>
		<Button
			v-if="route.params.step !== 'scheduleMatches'"
			label="Next"
			icon="pi pi-angle-right"
			icon-pos="right"
			@click="nextPage"
		/>
	</div>
</template>

<script setup lang="ts">
import PlayerCard from "@/components/views/prepare/assignTeams/PlayerCard.vue"
import DraggablePanel from "@/draggable/DraggablePanel.vue"
import { ref, TransitionGroup } from "vue"
import { getCompetitionDetails } from "@/backend/competition"
import { useRoute, useRouter } from "vue-router"
import { useToast } from "primevue/usetoast"
import { useI18n } from "vue-i18n"
import { Mode, SignUp } from "@/interfaces/competition"
import axios from "axios"
import { Team } from "@/interfaces/registration/team"
import { signedUpPlayer } from "@/interfaces/player"
import Button from "primevue/button"

const route = useRoute()
const router = useRouter()
const toast = useToast()
const { t } = useI18n({ inheritLocale: true })
const competition = getCompetitionDetails(route, t, toast, {
	suc: update,
})

const isSorting = ref<boolean>(false)

const playersA = ref<signedUpPlayer[]>([])
const playersB = ref<signedUpPlayer[]>([])

const teams = ref<{ playerA: signedUpPlayer[]; playerB: signedUpPlayer[] }[]>(
	[],
)

function sleep(milliseconds: number) {
	return new Promise((resolve) => setTimeout(resolve, milliseconds))
}

const delay = 100
const delayBetween = 50

async function randomize() {
	if (!competition.value) return
	isSorting.value = true

	for (const i in teams.value) {
		if (teams.value[i].playerA.length === 0) {
			const r = Math.floor(Math.random() * playersA.value.length)
			const element = playersA.value[r]
			playersA.value = playersA.value.filter((v, i) => i !== r)
			await sleep(delayBetween)
			teams.value[i].playerA.push(element)
			await sleep(delay)
		}
		if (competition.value.playerB.different) {
			if (teams.value[i].playerB.length === 0) {
				const r = Math.floor(Math.random() * playersB.value.length)
				const element = playersB.value[r]
				playersB.value = playersB.value.filter((v, i) => i !== r)
				await sleep(delayBetween)
				teams.value[i].playerB.push(element)
				await sleep(delay)
			}
		} else {
			if (teams.value[i].playerB.length === 0) {
				const r = Math.floor(Math.random() * playersA.value.length)
				const element = playersA.value[r]
				playersA.value = playersA.value.filter((v, i) => i !== r)
				await sleep(delayBetween)
				teams.value[i].playerB.push(element)
				await sleep(delay)
			}
		}
	}
	isSorting.value = false
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
		command: reset,
	},
]

async function reset() {
	if (!competition.value) return
	isSorting.value = true

	for (const i in teams.value) {
		if (teams.value[i].playerA.length === 1) {
			const element = teams.value[i].playerA[0]
			teams.value[i].playerA = []
			await sleep(delayBetween)
			playersA.value.push(element)
			await sleep(delay)
		}
		if (competition.value.playerB.different) {
			if (teams.value[i].playerB.length === 1) {
				const element = teams.value[i].playerB[0]
				teams.value[i].playerB = []
				await sleep(delayBetween)
				playersB.value.push(element)
				await sleep(delay)
			}
		} else {
			if (teams.value[i].playerB.length === 1) {
				const element = teams.value[i].playerB[0]
				teams.value[i].playerB = []
				await sleep(delayBetween)
				playersA.value.push(element)
				await sleep(delay)
			}
		}
	}
	isSorting.value = false
}

async function reroll() {
	await reset()
	await randomize()
}

function update() {
	playersA.value = []
	playersB.value = []
	teams.value = []

	if (
		competition.value?.mode === Mode.SINGLE ||
		competition.value?.signUp === SignUp.TOGETHER
	)
		return

	axios
		.get<Team[]>(
			`/tournament/${route.params.tourId}/competition/${route.params.compId}/signedUpTeams`,
		)
		.then((response) => {
			response.data.forEach((team) => {
				if (team.playerA !== undefined && team.playerB === undefined)
					playersA.value.push({
						firstName: team.playerA.firstName,
						lastName: team.playerA.lastName,
						name: team.playerA.firstName + " " + team.playerA.lastName,
					})
				else if (team.playerA === undefined && team.playerB !== undefined)
					playersB.value.push({
						firstName: team.playerB.firstName,
						lastName: team.playerB.lastName,
						name: team.playerB.firstName + " " + team.playerB.lastName,
					})
				else if (team.playerA !== undefined && team.playerB !== undefined) {
					teams.value.push({
						playerA: [
							{
								firstName: team.playerA.firstName,
								lastName: team.playerA.lastName,
								name: team.playerA.firstName + " " + team.playerA.lastName,
							},
						],
						playerB: [
							{
								firstName: team.playerB.firstName,
								lastName: team.playerB.lastName,
								name: team.playerB.firstName + " " + team.playerB.lastName,
							},
						],
					})
				}
				while (
					teams.value.length <
					Math.min(playersA.value.length, playersB.value.length)
				) {
					teams.value.push({
						playerA: [],
						playerB: [],
					})
				}
			})
		})
		.catch((error) => {
			teams.value = []
			playersA.value = []
			playersB.value = []
			toast.add({
				severity: "error",
				summary: t("ViewCompetition.query_player_failed"),
				life: 3000,
			})
			console.log(error)
		})
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

defineExpose({ save })

function previousPage() {
	router.replace({
		name: "chooseMode",
		params: { tourId: route.params.tourId, compId: route.params.compId },
	})
}

function nextPage() {
	router.replace({
		name: "selectType",
		params: { tourId: route.params.tourId, compId: route.params.compId },
	})
}
</script>

<style scoped>
.playerList-enter-active,
.playerList-leave-active,
.teamList-enter-active,
.teamList-leave-active {
	transition: all 0.5s ease;
}

.playerList-enter-from,
.playerList-leave-to,
.teamList-enter-from,
.teamList-leave-to {
	opacity: 0;
	transform: scale(0%);
}

.ghost {
	opacity: 50%;
}
.swapGhost {
	display: none !important;
}

.min-h-3rem {
	min-height: 3rem;
}
</style>

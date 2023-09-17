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
		PlayersA: {{ playersA }}<br />
		PlayersB: {{ playersB }}<br />
		Animation: {{ animation }}<br />
		<div class="grid">
			<div class="col-5 flex flex-column gap-4">
				<SplitButton
					label="Randomize"
					severity="help"
					:model="randomizeItems"
					@click="randomize"
					class="w-fit"
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
					<Draggable
						:list="playersA"
						:put="['playersA', 'playersB']"
						itemKey="name"
						tag="div"
						group="playerA"
						class="flex flex-row flex-row flex-wrap gap-2"
					>
						<template #default="{ item }">
							<Player :key="item.name" :player="item" />
						</template>
					</Draggable>
					<!--<Draggable
						:list="playersA"
						group="playerA"
						tag="transition-group"
						:="{
							animation: 200,
							ghostClass: 'ghost',
						}"
						:component-data="{
							tag: 'div',
							name: animation ? 'playerList' : 'default',
							type: 'transition',
						}"
						class="flex flex-row flex-row flex-wrap gap-2"
					>
						<template #item="{ element }">
							<PlayerV2 :key="element.name" :player="element" />
						</template>
					</Draggable>-->
				</Fieldset>
				<Fieldset legend="Player 2" v-if="competition?.playerB.different">
					<Draggable
						:list="playersB"
						:put="['playersB']"
						itemKey="name"
						tag="div"
						group="playersB"
						class="flex flex-row flex-row flex-wrap gap-2"
					>
						<template #default="{ item }">
							<Player :key="item.name" :player="item" />
						</template>
					</Draggable>
					<!--<draggable
						:list="playersB"
						group="playerB"
						tag="transition-group"
						:component-data="{
							tag: 'div',
							name: animation ? 'playerList' : 'default',
						}"
						class="flex flex-row flex-row flex-wrap gap-2"
					>
						<template #item="{ element }">
							<PlayerV2 :key="element.name" :player="element" secondary />
						</template>
					</draggable>-->
				</Fieldset>
			</div>
			<div class="col-7 flex flex-column gap-4">
				<!--<DataTable :value="teams" showGridlines stripedRows>
					<Column class="w-6" header="Player 1" field="name">
						<template #body="{ index }">
							<draggable
								:list="teams[index].playerA"
								:group="
									teams[index].playerA.length === 0
										? 'playerA'
										: {
												name: 'playerA',
												put: false,
										  }
								"
								:="{
									animation: 200,
									ghostClass: 'ghost',
								}"
								tag="transition-group"
								:component-data="{
									tag: 'div',
									name: animation ? 'teamList' : 'default',
								}"
								class="mt-1 mb-1 flex align-items-center justify-content-center"
								:class="{
									'h-2rem border-dashed dragTo':
										teams[index].playerA.length === 0,
								}"
							>
								<template #item="{ element: element }">
									<div :key="element.name">
										<PlayerV2 :player="element" />
									</div>
								</template>
							</draggable>
						</template>
					</Column>
					<Column class="w-6" header="Player 2" field="name">
						<template #body="{ index }">
							<draggable
								:list="teams[index].playerB"
								:group="
									teams[index].playerB.length === 0
										? competition?.playerB.different
											? 'playerB'
											: 'playerA'
										: {
												name: competition?.playerB.different
													? 'playerB'
													: 'playerA',
												put: false,
										  }
								"
								:="{
									animation: 200,
									ghostClass: 'ghost',
								}"
								tag="transition-group"
								:component-data="{
									tag: 'div',
									name: animation ? 'teamList' : 'default',
								}"
								class="mt-1 mb-1 flex align-items-center justify-content-center"
								:class="{
									'h-2rem border-dashed dragTo':
										teams[index].playerB.length === 0,
								}"
							>
								<template #item="{ element: element }">
									<div :key="element.name">
										<PlayerV2
											:player="element"
											:secondary="competition?.playerB.different || false"
										/>
									</div>
								</template>
							</draggable>
						</template>
					</Column>
				</DataTable>-->
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
import Player from "@/components/views/prepare/assignTeams/Player.vue"
import Draggable from "@/draggable/Draggable.vue"
import { ref } from "vue"
import { getCompetitionDetails } from "@/backend/competition"
import { useRoute, useRouter } from "vue-router"
import { useToast } from "primevue/usetoast"
import { useI18n } from "vue-i18n"
import { Mode, SignUp } from "@/interfaces/competition"
import axios from "axios"
import { Team } from "@/interfaces/registration/team"
import { signedUpPlayer } from "@/interfaces/player"

const route = useRoute()
const router = useRouter()
const toast = useToast()
const { t } = useI18n({ inheritLocale: true })
const competition = getCompetitionDetails(route, t, toast, {
	suc: update,
})

const animation = ref<boolean>(false)

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

	animation.value = true
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
	animation.value = false
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

	animation.value = true
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
	animation.value = false
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
			reset()
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
	visibility: hidden;
}
</style>
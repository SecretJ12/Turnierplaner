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
			<div class="col-4 flex flex-column gap-4">
				<Button
					severity="help"
					class="flex justify-content-center align-items-center"
					@click="randomize"
				>
					<span class="material-icons">casino</span>
					<span class="mt-1">Randomize</span>
				</Button>
				<Fieldset legend="Player 1">
					<draggable
						:list="playersA"
						group="playerA"
						tag="div"
						class="flex flex-row flex-row flex-wrap gap-2"
						itemKey="name"
					>
						<template #item="{ element }">
							<PlayerV2 :player="element" />
						</template>
					</draggable>
				</Fieldset>
				<Fieldset legend="Player 2" v-if="competition?.playerB.different">
					<draggable
						:list="playersB"
						group="playerB"
						tag="div"
						class="flex flex-row flex-row flex-wrap gap-2"
						itemKey="name"
					>
						<template #item="{ element }">
							<PlayerV2 :player="element" secondary />
						</template>
					</draggable>
				</Fieldset>
			</div>
			<div class="col-offset-1 col-7 flex flex-column gap-4">
				<DataTable :value="teams" showGridlines stripedRows>
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
								tag="div"
								class="mt-1 mb-1 flex align-items-center justify-content-center"
								:class="{
									'h-2rem border-dashed dragTo':
										teams[index].playerA.length === 0,
								}"
								itemKey="name"
							>
								<template #item="{ element: element }">
									<div>
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
										? 'playerB'
										: {
												name: 'playerB',
												put: false,
										  }
								"
								tag="div"
								class="mt-1 mb-1 flex align-items-center justify-content-center"
								:class="{
									'h-2rem border-dashed dragTo':
										teams[index].playerB.length === 0,
								}"
								itemKey="name"
							>
								<template #item="{ element: element }">
									<div>
										<PlayerV2
											:player="element"
											:secondary="competition?.playerB.different || false"
										/>
									</div>
								</template>
							</draggable>
						</template>
					</Column>
				</DataTable>
			</div>
		</div>
	</template>
</template>

<script setup lang="ts">
import PlayerV2 from "@/components/views/prepare/assignTeams/PlayerV2.vue"
import draggable from "vuedraggable"
import { ref, watch } from "vue"
import { getCompetitionDetails } from "@/backend/competition"
import { useRoute } from "vue-router"
import { useToast } from "primevue/usetoast"
import { useI18n } from "vue-i18n"
import { Mode, SignUp } from "@/interfaces/competition"
import axios from "axios"
import { Team } from "@/interfaces/registration/team"
import { signedUpPlayer } from "@/interfaces/player"

const route = useRoute()
const toast = useToast()
const { t } = useI18n({ inheritLocale: true })
const competition = getCompetitionDetails(route, t, toast, {})

const playersA = ref<signedUpPlayer[]>([])
const playersB = ref<signedUpPlayer[]>([])

const teams = ref<{ playerA: signedUpPlayer[]; playerB: signedUpPlayer[] }[]>(
	[],
)

function randomize() {
	for (const i in teams.value) {
		if (teams.value[i].playerA.length === 0) {
			const r = Math.floor(Math.random() * playersA.value.length)
			teams.value[i].playerA.push(playersA.value[r])
			playersA.value = playersA.value.filter((v, i) => i !== r)
		}
		if (teams.value[i].playerB.length === 0) {
			const r = Math.floor(Math.random() * playersB.value.length)
			teams.value[i].playerB.push(playersB.value[r])
			playersB.value = playersB.value.filter((v, i) => i !== r)
		}
	}
}

watch(route, update)
update()

function update() {
	playersA.value = []
	playersB.value = []
	teams.value = []

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
</script>

<style scoped></style>
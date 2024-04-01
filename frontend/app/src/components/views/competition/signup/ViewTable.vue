<template>
	<!-- SINGLE -->
	<DataTable
		v-if="props.competition.mode === Mode.SINGLE"
		:value="playersA"
		striped-rows
		show-gridlines
		removable-sort
	>
		<template #empty>{{ t("ViewCompetition.no_registration") }}</template>
		<Column :header="t('general.name')" field="name" sortable>
			<template #body="{ data }">
				<div class="flex justify-content-between align-items-center">
					{{
						// @ts-ignore
						data.name
					}}
					<Button
						v-if="canEdit"
						severity="danger"
						text
						rounded
						size="small"
						class="h-2rem"
						@click="deletePlayer(data)"
					>
						<span class="material-icons">delete_forever</span>
					</Button>
				</div>
			</template>
		</Column>
	</DataTable>

	<!-- DOUBLE TOGETHER -->
	<DataTable
		v-else-if="props.competition.signUp === SignUp.TOGETHER"
		:value="teams"
		striped-rows
		show-gridlines
		removable-sort
	>
		<template #empty>{{ t("ViewCompetition.no_registration") }}</template>
		<Column
			:header="t('ViewCompetition.playerA')"
			sortable
			field="playerA.name"
			style="width: 50%"
		/>
		<Column
			:header="t('ViewCompetition.playerB')"
			sortable
			field="playerB.name"
			style="width: 50%"
		>
			<template #body="{ data }">
				<div class="flex justify-content-between align-items-center">
					{{
						// @ts-ignore
						data.playerB.name
					}}
					<Button
						v-if="canEdit"
						severity="danger"
						text
						rounded
						size="small"
						class="h-2rem"
						@click="deletePlayer(data)"
					>
						<span class="material-icons">delete_forever</span>
					</Button>
				</div>
			</template>
		</Column>
	</DataTable>
	<!-- DOUBLE INDIVIDUAL SAME -->
	<DataTable
		v-else-if="!props.competition.playerB.different"
		:value="playersA"
		striped-rows
		show-gridlines
		removable-sort
	>
		<template #empty>{{ t("ViewCompetition.no_registration") }}</template>
		<Column :header="t('general.name')" sortable field="name">
			<template #body="{ data }">
				<div class="flex justify-content-between align-items-center">
					{{
						// @ts-ignore
						data.name
					}}
					<Button
						v-if="canEdit"
						severity="danger"
						text
						rounded
						size="small"
						class="h-2rem"
						@click="deletePlayer(data)"
					>
						<span class="material-icons">delete_forever</span>
					</Button>
				</div>
			</template>
		</Column>
	</DataTable>

	<!-- DOUBLE INDIVIDUAL DIFFERENT -->
	<div v-else class="grid">
		<DataTable
			class="col"
			:value="playersA"
			striped-rows
			show-gridlines
			removable-sort
		>
			<template #empty>{{ t("ViewCompetition.no_registration") }}</template>
			<Column :header="t('ViewCompetition.playerA')" sortable field="name">
				<template #body="{ data }">
					<div class="flex justify-content-between align-items-center">
						{{
							// @ts-ignore
							data.name
						}}
						<Button
							v-if="canEdit"
							severity="danger"
							text
							rounded
							size="small"
							class="h-2rem"
							@click="deletePlayer(data)"
						>
							<span class="material-icons">delete_forever</span>
						</Button>
					</div>
				</template>
			</Column>
		</DataTable>
		<DataTable
			class="col"
			:value="playersB"
			striped-rows
			show-gridlines
			removable-sort
		>
			<template #empty>{{ t("ViewCompetition.no_registration") }}</template>
			<Column :header="t('ViewCompetition.playerB')" sortable field="name">
				<template #body="{ data }">
					<div class="flex justify-content-between align-items-center">
						{{
							// @ts-ignore
							data.name
						}}
						<Button
							v-if="canEdit"
							severity="danger"
							text
							rounded
							size="small"
							class="h-2rem"
							@click="deletePlayer(data)"
						>
							<span class="material-icons">delete_forever</span>
						</Button>
					</div>
				</template>
			</Column>
		</DataTable>
	</div>
</template>

<script lang="ts" setup>
import axios from "axios"
import { inject, ref, watch } from "vue"
import { useRoute } from "vue-router"
import { Competition, Mode, SignUp } from "@/interfaces/competition"
import { useI18n } from "vue-i18n"
import { Player, playerServerToClient } from "@/interfaces/player"
import { useToast } from "primevue/usetoast"
import { getCanEdit } from "@/backend/security"
import { Team, teamServerToClient } from "@/interfaces/team"

const { t } = useI18n({ inheritLocale: true })
const toast = useToast()

const props = defineProps<{
	update: number
	competition: Competition
}>()

const route = useRoute()

const isLoggedIn = inject("loggedIn", ref(false))
const canEdit = getCanEdit(<string>route.params.tourId, isLoggedIn)
const teams = ref<Team[]>([])
const playersA = ref<Player[]>([])
const playersB = ref<Player[]>([])

watch(() => props.update, updateTeams)
watch(route, updateTeams)
updateTeams()

function updateTeams() {
	playersA.value = []
	playersB.value = []
	teams.value = []
	if (!route.params.tourId || !route.params.compId) return

	axios
		.get<Team[]>(
			`/tournament/${route.params.tourId}/competition/${route.params.compId}/signedUpTeams`,
		)
		.then((response) => {
			if (
				props.competition.mode === Mode.SINGLE ||
				(props.competition.mode === Mode.DOUBLE &&
					props.competition.signUp === SignUp.INDIVIDUAL &&
					!props.competition.playerB.different)
			) {
				playersA.value = response.data
					.filter((team) => team.playerA !== null)
					.map((team) => {
						const player = team.playerA
						if (player === null) throw new Error("Player A is null")
						return playerServerToClient(player)
					})
			} else if (
				props.competition.mode === Mode.DOUBLE &&
				props.competition.signUp === SignUp.INDIVIDUAL &&
				props.competition.playerB.different
			) {
				playersA.value = response.data
					.filter((team) => team.playerA !== null)
					.map((team) => {
						let player = team.playerA
						if (player === null) throw new Error("Player A is null")
						return playerServerToClient(player)
					})
				playersB.value = response.data
					.filter((team) => team.playerB !== null)
					.map((team) => {
						let player = team.playerB
						if (player === null) throw new Error("Player B is null")
						return playerServerToClient(player)
					})
			} else if (
				props.competition.mode === Mode.DOUBLE &&
				props.competition.signUp === SignUp.TOGETHER
			) {
				teams.value = response.data.map((team) => {
					if (team.playerA === undefined) throw new Error("Player A is null")
					if (team.playerB === undefined) throw new Error("Player B is null")
					return teamServerToClient(team)
				})
			} else {
				toast.add({
					severity: "error",
					summary: t("ViewSignUp.invalidMode"),
					life: 3000,
				})
			}
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

function deletePlayer(player: Player) {
	console.log(player)
	// TODO implement
	toast.add({
		severity: "success",
		summary: t("ViewSignUp.playerDeleted"),
		life: 3000,
	})
}
</script>

<style scoped></style>

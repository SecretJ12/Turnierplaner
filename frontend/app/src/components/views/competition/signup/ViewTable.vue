<template>
	<!-- SINGLE -->
	<DataTable
		v-if="props.competition.mode === Mode.SINGLE"
		:value="playersA"
		striped-rows
		show-gridlines
		removable-sort
	>
		<template #empty> t('ViewCompetition.no_registration')</template>
		<Column :header="t('general.name')" sortable field="name" />
	</DataTable>

	<!-- DOUBLE TOGETHER -->
	<DataTable
		v-else-if="props.competition.signUp === SignUp.TOGETHER"
		:value="teams"
		striped-rows
		show-gridlines
		removable-sort
	>
		<template #empty> t('ViewCompetition.no_registration')</template>
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
		/>
	</DataTable>
	<!-- DOUBLE INDIVIDUAL SAME -->
	<DataTable
		v-else-if="!props.competition.playerB.different"
		:value="playersA"
		striped-rows
		show-gridlines
		removable-sort
	>
		<template #empty> t('ViewCompetition.no_registration')</template>
		<Column :header="t('general.name')" sortable field="name" />
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
			<template #empty> t('ViewCompetition.no_registration')</template>
			<Column :header="t('ViewCompetition.playerA')" sortable field="name" />
		</DataTable>
		<DataTable
			class="col"
			:value="playersB"
			striped-rows
			show-gridlines
			removable-sort
		>
			<template #empty> t('ViewCompetition.no_registration')</template>
			<Column :header="t('ViewCompetition.playerB')" sortable field="name" />
		</DataTable>
	</div>
</template>

<script lang="ts" setup>
import axios from "axios"
import { ElMessage } from "element-plus"
import { ref, watch } from "vue"
import { useRoute } from "vue-router"
import { Competition, Mode, SignUp } from "@/interfaces/competition"
import { signedUpTeam, Team } from "@/interfaces/registration/team"
import { useI18n } from "vue-i18n"
import { signedUpPlayer } from "@/interfaces/player"

const { t } = useI18n({ inheritLocale: true })

const props = defineProps<{
	update: number
	competition: Competition
}>()

const route = useRoute()
const teams = ref<signedUpTeam[]>([])
const playersA = ref<signedUpPlayer[]>([])
const playersB = ref<signedUpPlayer[]>([])

watch(
	() => props.update,
	async () => {
		updateTeams()
	},
)
updateTeams()

function updateTeams() {
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
						if (player === undefined) throw new Error("Player A is null")
						return {
							firstName: player.firstName,
							lastName: player.lastName,
							name: player.firstName + " " + player.lastName,
						}
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
						if (player === undefined) throw new Error("Player A is null")
						return {
							firstName: player.firstName,
							lastName: player.lastName,
							name: player.firstName + " " + player.lastName,
						}
					})
				playersB.value = response.data
					.filter((team) => team.playerB !== null)
					.map((team) => {
						let player = team.playerB
						if (player === undefined) throw new Error("Player B is null")
						return {
							firstName: player.firstName,
							lastName: player.lastName,
							name: player.firstName + " " + player.lastName,
						}
					})
			} else if (
				props.competition.mode === Mode.DOUBLE &&
				props.competition.signUp === SignUp.TOGETHER
			) {
				teams.value = response.data.map((team) => {
					if (team.playerA === undefined) throw new Error("Player A is null")
					if (team.playerB === undefined) throw new Error("Player B is null")
					return {
						playerA: {
							firstName: team.playerA.firstName,
							lastName: team.playerA.lastName,
							name: team.playerA.firstName + " " + team.playerA.lastName,
						},
						playerB: {
							firstName: team.playerB.firstName,
							lastName: team.playerB.lastName,
							name: team.playerB.firstName + " " + team.playerB.lastName,
						},
					}
				})
			} else {
				ElMessage.error("invalid mode")
			}
		})
		.catch((error) => {
			teams.value = []
			playersA.value = []
			playersB.value = []
			ElMessage.error(t("ViewCompetition.query_player_failed"))
			console.log(error)
		})
}
</script>

<style scoped></style>

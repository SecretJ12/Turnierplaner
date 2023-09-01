<template>
	<div id="container">
		<h2>
			<span v-if="canEdit" id="settings" @click="settings" class="material-icons cursor-pointer" style="font-size: 2rem">settings</span>
			{{ route.params.tourId }}
		</h2>
		<div id="content">
			<div id="competitions">
				<item
					v-for="competition in competitions"
					:key="competition.id"
					:can-edit="canEdit"
					:description="competition.description"
					:name="competition.name"
					:type="competition.tourType"
					@selected="selected"
					@settings="settingsItem"
				/>
				<AddItem v-if="canEdit" :title="t('ViewCompetitions.add_title')" :content="t('ViewCompetitions.add_content')" @selected="addCompetition" />
			</div>
			<div id="aside">
				<Timeline v-if="tournament !== null" id="progress" op :value="status">
					<template #marker="slotProps">
						<span
							class="progress-icon"
							:style="{ borderColor: slotProps.item.color }"
						>
							<i
								class="pi"
								:class="[slotProps.item.icon]"
								:style="{ color: slotProps.item.color }"
							></i>
						</span>
					</template>
					<template #content="slotProps">
						<div style="height: 3px"></div>
						<template v-if="slotProps.item.registration">
							<strong>{{ t("TournamentSettings.registration_phase") }}</strong
							><br />
							<strong>{{ t("ViewCompetitions.from")}}</strong> {{ formatDate(tournament?.registration_phase.begin) }}<br>
							<strong>{{ t("ViewCompetitions.till")}}</strong> {{ formatDate(tournament?.registration_phase.end) }}
						</template>
						<template v-else>
							<strong>{{ t("TournamentSettings.game_phase") }}</strong
							><br />
							<strong>{{ t("ViewCompetitions.from")}}</strong> {{ formatDate(tournament?.game_phase.begin) }}<br>
							<strong>{{ t("ViewCompetitions.till")}}</strong> {{ formatDate(tournament?.game_phase.end) }}
						</template>
					</template>
				</Timeline>
				<Button
					v-if="isLoggedIn"
					id="prepare"
					:label="t('ViewCompetition.prepare') + ' >>'"
					@click="prepare"
				></Button>
			</div>
		</div>
	</div>
</template>

<script lang="ts" setup>
import Item from "../../items/ItemCompetition.vue"
import { inject, ref, watch } from "vue"
import { useRoute } from "vue-router"
import AddItem from "@/components/items/ItemAdd.vue"
import { router } from "@/main"
import axios from "axios"
import { auth } from "@/security/AuthService"
import { ElMessage } from "element-plus"
import { useI18n } from "vue-i18n"
import {
	Competition,
	CompetitionServer,
	competitionServerToClient,
} from "@/interfaces/competition"
import {
	Tournament,
	TournamentServer,
	tournamentServerToClient,
} from "@/interfaces/tournament"
import Button from "primevue/button"
import Timeline from "primevue/timeline"

const { t } = useI18n({ inheritLocale: true })

const route = useRoute()

const competitions = ref<Competition[]>([])

const isLoggedIn = inject("loggedIn", ref(false))
const canEdit = ref(false)

const progress = ref(0)
const statusActive = ref<"wait" | "process" | "success" | "error">("wait")

const tournament = ref<Tournament | null>(null)
const options: Intl.DateTimeFormatOptions = {
	weekday: "long",
	year: "numeric",
	month: "long",
	day: "numeric",
	hour: "numeric",
	minute: "numeric",
}

const status = ref([
	{
		registration: true,
		icon: "pi-pencil",
		color: "#000000",
	},
	{
		registration: false,
		icon: "pi-play",
		color: "#000000",
	},
])

watch(isLoggedIn, async () => {
	update()
})
update()

function update() {
	canEdit.value = false
	auth.getUser().then((user) => {
		if (user !== null) {
			axios
				.get<boolean>(`/tournament/${route.params.tourId}/competition/canEdit`)
				.then((response) => {
					canEdit.value = response.data
				})
				.catch(() => {
					canEdit.value = false
				})
		}
	})
	axios
		.get<CompetitionServer[]>(
			`/tournament/${route.params.tourId}/competition/list`,
		)
		.then((response) => {
			if (response.status === 200)
				competitions.value = response.data.map(competitionServerToClient)
			else {
				ElMessage.error(t("ViewCompetitions.loadingFailed"))
			}
		})
		.catch((error) => {
			ElMessage.error(t("ViewCompetitions.loadingFailed"))
			console.log(error)
			router.push("/")
		})
	axios
		.get<TournamentServer>(`/tournament/${route.params.tourId}/details`)
		.then((response) => {
			const date = new Date()
			tournament.value = tournamentServerToClient(response.data)

			if (date > tournament.value.registration_phase.end) {
				status.value[0].color = "green"
				status.value[0].icon = "pi-check"
			} else if (date > tournament.value.registration_phase.begin) {
				status.value[0].color = "blue"
			}
			if (date > tournament.value.game_phase.end) {
				status.value[1].color = "green"
				status.value[1].icon = "pi-check"
			} else if (date > tournament.value.game_phase.begin) {
				status.value[1].color = "blue"
			}
		})
		.catch((error) => {
			statusActive.value = "error"
			console.log(error)
		})
}

function formatDate(d: Date) {
	return d.toLocaleString(t("lang"), options)
}

function settings() {
	router.push({ path: "/tournament/" + route.params.tourId + "/edit" })
}

function selected(competition: string) {
	router.push({
		path: `/tournament/${route.params.tourId}/competition/${competition}`,
	})
}

function prepare() {
	router.push({
		path: `/tournament/${route.params.tourId}/prepare/editPlayers/${competitions.value[0].name}`,
	})
}

function settingsItem(competition: string) {
	router.push({
		path: `/tournament/${route.params.tourId}/competition/${competition}/edit`,
	})
}

function addCompetition() {
	router.push({
		path: `/tournament/${route.params.tourId}/createCompetition`,
	})
}
</script>

<style scoped>
#settings {
	color: #303030;
	margin-right: 5px;
}

#settings:hover {
	filter: drop-shadow(0 0 6px #808080);
}

#settings:active {
	color: #505050;
}

#competitions {
	margin: 10px;
	display: flex;
	flex-wrap: wrap;
	flex-direction: row;
	justify-content: center;
	flex-shrink: 2;
}

#container {
	width: 100%;
}

#content {
	display: flex;
	flex-direction: row;
	justify-content: center;
}

#competitions > * {
	margin: 0 10px 10px 10px;
}

#aside {
	flex-shrink: 0.5;
	margin-top: 20px;
	margin-right: 10px;
	display: flex;
	flex-direction: column;
	justify-content: flex-start;
	align-items: flex-start;
	width: 25dvw;
	height: fit-content;
}

#progress {
	height: fit-content;
}

#prepare {
	margin-top: 20px;
	width: min(100%, 30rem);
}

h2 {
	text-align: center;
	font-size: 30px;
}

@media only screen and (max-width: 900px) {
	#competitions {
		width: auto;
		left: 0;
		right: 0;
		margin: 10px 0 10px 0;
	}
}

@media only screen and (max-width: 1300px) {
	#content {
		flex-direction: column-reverse;
	}

	#aside {
		padding: 10px;
		width: 100%;
	}
}

::v-deep(.p-timeline-event-opposite) {
	display: none;
}

.progress-icon {
	width: 2rem;
	height: 2rem;
	border: 2px solid;
	border-radius: 100%;
	box-sizing: border-box;
	display: flex;
	justify-content: center;
	align-items: center;
}

.progress-icon > i {
	font-weight: bold;
}
</style>

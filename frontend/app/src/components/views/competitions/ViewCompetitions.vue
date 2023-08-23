<template>
	<div id="container">
		<h2>
			<font-awesome-icon
				v-if="canEdit"
				id="settings"
				:icon="['fas', 'gear']"
				class="fa-1x"
				@click="settings"
			>
			</font-awesome-icon>
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
				<AddItem v-if="canEdit" @selected="addCompetition" />
			</div>
			<div id="aside">
        <!-- TODO fix height of timeline -->
				<Timeline v-if="tournament !== null" id="progress" align="left" :value="status" >
          <template #marker="slotProps">
            text
            <!-- TODO add fitting font awesome icon -->
          </template>
					<template #content="slotProps">
            {{ slotProps.item.status }}
            {{ slotProps.item.begin }} -
            {{ slotProps.item.end }}
            <!-- TODO fix dates begin reponsive -->
					</template>
				</Timeline>
				<Button
					:label="t('ViewCompetition.prepare') + ' >>'"
					id="prepare"
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
import Card from "primevue/card"

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
		status: t("TournamentSettings.registration_phase"),
		begin:
			tournament.value?.registration_phase.begin.toLocaleString(
				t("lang"),
				options,
			),
    end:
			tournament.value?.registration_phase.end.toLocaleString(
				t("lang"),
				options,
			),
		icon: "pi pi-shopping-cart",
		color: "#9C27B0",
	},
	{
		status: t("TournamentSettings.game_phase"),
		date:
			tournament.value?.game_phase.begin.toLocaleString(t("lang"), options) +
			" - " +
			tournament.value?.game_phase.end.toLocaleString(t("lang"), options),
		icon: "pi pi-shopping-cart",
		color: "#9C27B0",
	},
])

watch(isLoggedIn, async () => {
	update()
})
update()

let windowWidth = ref(window.innerWidth)
window.addEventListener("resize", () => {
	windowWidth.value = window.innerWidth
})

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

			if (date < tournament.value.registration_phase.begin) {
				progress.value = 0
				statusActive.value = "wait"
			} else if (date < tournament.value.registration_phase.end) {
				progress.value = 0
				statusActive.value = "process"
			} else if (date < tournament.value.game_phase.begin) {
				progress.value = 1
				statusActive.value = "wait"
			} else if (date < tournament.value.game_phase.end) {
				progress.value = 1
				statusActive.value = "process"
			} else {
				progress.value = 1
				statusActive.value = "success"
			}
		})
		.catch((error) => {
			statusActive.value = "error"
			console.log(error)
		})
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
		path: `/tournament/${route.params.tourId}/prepare`,
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
	align-items: flex-start;
  width: 20dvw;
}

#progress {
  align-items: flex-start;
	height: fit-content;
}

#prepare {
	margin-top: 20px;
	width: 100%;
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

	#progress {
		margin: 10px;
	}
}
</style>
<template>
	<div id="container">
		<div style="margin-bottom: 20px">
			<span
				v-if="canEdit"
				id="settings"
				@click="settings"
				class="material-icons cursor-pointer"
				style="font-size: 1.7rem"
				>settings</span
			>
			<div id="tourName">
				{{ route.params.tourId }}
			</div>
			<h2>
				{{ route.params.compId }}
			</h2>
		</div>
		<template v-if="tournament !== null">
			<template v-if="new Date() < tournament.registration_phase.begin">
				{{ t("ViewCompetition.registration_not_started") }}
				{{
					tournament.registration_phase.begin.toLocaleString(
						t("lang"),
						dateOptions,
					)
				}}
			</template>
			<template v-else-if="new Date() < tournament.game_phase.begin">
				<!-- show registration page -->
				<ViewSignUp
					v-if="competition !== null"
					:allow-registration="tournament.registration_phase.end > new Date()"
					:begin-game-phase="tournament.game_phase.begin"
					:competition="competition"
				/>
			</template>
			<template v-else>
				<!-- TODO show after plan has been published -->
				<!-- show game page -->
				<ViewGame
					v-if="competition !== null"
					:tour-type="competition.tourType"
					:mode="competition.mode"
				/>
			</template>
		</template>
	</div>
</template>

<script lang="ts" setup>
import { useRoute } from "vue-router"
import { inject, ref, watch } from "vue"
import { auth } from "@/security/AuthService"
import axios from "axios"
import { router } from "@/main"
import ViewSignUp from "@/components/views/competition/signup/ViewSignUp.vue"
import ViewGame from "@/components/views/competition/ViewGame.vue"
import { ElMessage } from "element-plus"
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
import { useI18n } from "vue-i18n"

const { t } = useI18n({ inheritLocale: true })

const route = useRoute()
const isLoggedIn = inject("loggedIn", ref(false))
const canEdit = ref(false)

const competition = ref<Competition | null>(null)
const tournament = ref<Tournament | null>(null)

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
		.get<CompetitionServer>(
			`/tournament/${route.params.tourId}/competition/${route.params.compId}/details`,
		)
		.then((response) => {
			competition.value = competitionServerToClient(response.data)
		})
		.catch((error) => {
			console.log(error)
			ElMessage.error(t("ViewEditTournament.loadingDetailsFailed"))
		})
	axios
		.get<TournamentServer>(`/tournament/${route.params.tourId}/details`)
		.then((response) => {
			tournament.value = tournamentServerToClient(response.data)
		})
		.catch((error) => {
			console.log(error)
			ElMessage.error(t("ViewEditCompetition.loadingDetailsFailed"))
		})
}

function settings() {
	router.push({
		path: `/tournament/${route.params.tourId}/competition/${route.params.compId}/edit`,
	})
}

const dateOptions: Intl.DateTimeFormatOptions = {
	weekday: "long",
	year: "numeric",
	month: "long",
	day: "numeric",
	hour: "numeric",
	minute: "numeric",
}
</script>

<style scoped>
#container {
	width: 100%;
	margin: 0 10px 0 10px;
	display: flex;
	flex-direction: column;
	align-items: center;
}

h2 {
	font-size: 30px;
	display: inline;
	margin-left: 15px;
}

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

#tourName {
	display: inline;
	color: gray;
	font-size: 18px;
}
</style>

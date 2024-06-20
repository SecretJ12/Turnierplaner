<template>
	<div id="container">
		<div style="margin-bottom: 20px">
			<span
				v-if="canEdit"
				id="settings"
				class="material-icons cursor-pointer"
				style="font-size: 1.7rem"
				@click="settings"
				>settings</span
			>
			<div id="tourName">
				{{ route.params.tourId }}
			</div>
			<h2>
				{{ route.params.compId }}
			</h2>
		</div>
		<template v-if="tournamentSuc && tournament">
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
import { inject, ref } from "vue"
import { router } from "@/main"
import ViewSignUp from "@/components/views/competition/signup/ViewSignUp.vue"
import ViewGame from "@/components/views/competition/ViewGame.vue"
import { useI18n } from "vue-i18n"
import { getCanEdit } from "@/backend/security"
import { getTournamentDetails } from "@/backend/tournament"
import { useToast } from "primevue/usetoast"
import { getCompetitionDetails } from "@/backend/competition"

const { t } = useI18n({ inheritLocale: true })
const toast = useToast()

const route = useRoute()
const isLoggedIn = inject("loggedIn", ref(false))
const canEdit = getCanEdit(<string>route.params.tourId, isLoggedIn)

const { data: tournament, isSuccess: tournamentSuc } = getTournamentDetails(
	<string>route.params.tourId,
	t,
	toast,
	{},
)
const competition = getCompetitionDetails(route, t, toast, {})

function settings() {
	router.push({
		name: "Edit competition",
		params: { tourId: route.params.tourId, compId: route.params.compId },
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

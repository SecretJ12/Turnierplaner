<template>
	<FormTournament
		v-if="data.id === null"
		:data="data"
		:disabled="true"
		:submit-text="t('general.update')"
		header="ViewEditTournament.tournamentUpdate"
		@submit="submit"
	/>
	<FormTournament
		v-else
		:data="data"
		:disabled="false"
		:submit-text="t('general.update')"
		header="ViewEditTournament.tournamentUpdate"
		@submit="submit"
	/>
</template>

<script lang="ts" setup>
import { useRoute } from "vue-router"
import { TournamentServer } from "@/interfaces/tournament"
import FormTournament from "@/components/views/tournaments/FormTournament.vue"
import { useI18n } from "vue-i18n"
import { useToast } from "primevue/usetoast"
import {
	getTournamentFormDetails,
	updateTournament,
} from "@/backend/tournament"
import { router } from "@/main"

const { t } = useI18n({ inheritLocale: true })
const toast = useToast()

const route = useRoute()

const data = getTournamentFormDetails(
	<string>route.params.tourId,
	t,
	toast,
	() => {
		router.back()
	},
)

function submit(server_data: TournamentServer) {
	server_data["id"] = data.value.id

	updateTournament(server_data, t, toast)
}
</script>

<style scoped></style>

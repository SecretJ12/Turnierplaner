<template>
	<FormTournament
		v-if="isLoading || isError"
		:tour-details="TournamentDefault"
		:disabled="true"
		:submit-text="t('general.update')"
		header="ViewEditTournament.tournamentUpdate"
		@submit="submit"
	/>
	<FormTournament
		v-else
		:tour-details="tourDetails"
		:disabled="false"
		:submit-text="t('general.update')"
		header="ViewEditTournament.tournamentUpdate"
		@submit="submit"
	/>
</template>

<script lang="ts" setup>
import { useRoute } from "vue-router"
import { TournamentDefault, TournamentServer } from "@/interfaces/tournament"
import FormTournament from "@/components/views/tournaments/FormTournament.vue"
import { useI18n } from "vue-i18n"
import { useToast } from "primevue/usetoast"
import { getTournamentDetails, useUpdateTournament } from "@/backend/tournament"
import { router } from "@/main"

const { t } = useI18n({ inheritLocale: true })
const toast = useToast()

const route = useRoute()

const {
	data: tourDetails,
	isLoading,
	isError,
} = getTournamentDetails(route, t, toast, {
	err: () => {
		router.back()
	},
})

const { mutate } = useUpdateTournament(t, toast, {
	err: () => {
		router.back()
	},
})

function submit(server_data: TournamentServer) {
	mutate(server_data)
}
</script>

<style scoped></style>

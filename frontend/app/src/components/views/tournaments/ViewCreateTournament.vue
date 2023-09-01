<template>
	<FormTournament
		:data="data"
		:submit-text="t('general.create')"
		:disabled="false"
		header="ViewCreateTournament.tournamentCreation"
		@submit="submit"
	/>
</template>

<script lang="ts" setup>
import { reactive } from "vue"
import { router } from "@/main"
import { useToast } from "primevue/usetoast"
import FormTournament from "@/components/views/tournaments/FormTournament.vue"
import { TournamentForm, TournamentServer } from "@/interfaces/tournament"
import { useI18n } from "vue-i18n"
import { addTournament } from "@/backend/tournament"

const { t } = useI18n({ inheritLocale: true })
const toast = useToast()

const data = reactive<TournamentForm>({
	name: "",
	visible: false,
	description: "",
	registration_phase: undefined,
	game_phase: undefined,
})

function submit(server_data: TournamentServer) {
	addTournament(server_data, t, toast, {
		suc: () => {
			router.push({ path: `/tournament/${server_data.name}` })
		},
	})
}
</script>

<style scoped></style>

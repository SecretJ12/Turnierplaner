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
import axios from "axios"
import { router } from "@/main"
import { useToast } from "primevue/usetoast"
import FormTournament from "@/components/views/tournaments/FormTournament.vue"
import { TournamentForm, TournamentServer } from "@/interfaces/tournament"
import { useI18n } from "vue-i18n"

const { t } = useI18n({ inheritLocale: true })
const toast = useToast()

const data = reactive<TournamentForm>({
	name: "",
	visible: true,
	description: "",
	registration_phase: undefined,
	game_phase: undefined,
})

function submit(server_data: TournamentServer) {
	axios
		.post("/tournament/add", server_data)
		.then(() => {
			toast.add({
				severity: "success",
				summary: t("ViewCreateTournament.tournamentCreating"),
				detail: t("ViewCreateTournament.tournamentCreated"),
				life: 3000,
				closable: false,
			})
			router.push({ path: `/tournament/${server_data.name}` })
		})
		.catch(() => {
			toast.add({
				severity: "error",
				summary: t("ViewCreateTournament.tournamentCreating"),
				detail: t("ViewCreateTournament.tournamentCreationFailed"),
				life: 3000,
				closable: false,
			})
		})
}
</script>

<style scoped></style>

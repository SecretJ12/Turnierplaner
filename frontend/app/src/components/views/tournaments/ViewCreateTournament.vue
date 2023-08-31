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
import { ElMessage } from "element-plus"
import FormTournament from "@/components/views/tournaments/FormTournament.vue"
import { TournamentForm, TournamentServer } from "@/interfaces/tournament"
import { useI18n } from "vue-i18n"

const { t } = useI18n({ inheritLocale: true })

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
			ElMessage.success(t("ViewCreateTournament.tournamentCreated"))
			router.push({ path: "/tournament/" + data.name })
		})
		.catch(() => {
			ElMessage.error(t("ViewCreateTournament.tournamentCreationFailed"))
		})
}
</script>

<style scoped></style>

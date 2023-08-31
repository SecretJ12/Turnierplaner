<template>
	<FormTournament
		:data="data"
		:disabled="disabled"
		:submit-text="t('general.update')"
		header="ViewEditTournament.tournamentUpdate"
		@submit="submit"
	/>
</template>

<script lang="ts" setup>
import { ref } from "vue"
import axios from "axios"
import { router } from "@/main"
import { ElMessage } from "element-plus"
import { useRoute } from "vue-router"
import {
	TournamentForm,
	tournamentFormServerToClient,
	TournamentServer,
} from "@/interfaces/tournament"
import FormTournament from "@/components/views/tournaments/FormTournament.vue"
import { useI18n } from "vue-i18n"
import { useToast } from "primevue/usetoast"

const { t } = useI18n({ inheritLocale: true })
const toast = useToast()

const route = useRoute()

const data = ref<TournamentForm>({
	id: null,
	name: "",
	visible: true,
	description: "",
	registration_phase: [new Date(), new Date()],
	game_phase: [new Date(), new Date()],
})

const disabled = ref<boolean>(true)

await axios
	.get<TournamentServer>(`/tournament/${route.params.tourId}/details`)
	.then((response) => {
		data.value = tournamentFormServerToClient(response.data)
		disabled.value = false
	})
	.catch((error) => {
		ElMessage.error(t("ViewEditTournament.loadingDetailsFailed"))
		console.log(error)
		router.back()
	})

function submit(server_data: TournamentServer) {
	server_data["id"] = data.value.id

	axios
		.post("/tournament/update", server_data)
		.then(() => {
			toast.add({
				severity: "success",
				summary: t("ViewEditTournament.tournamentUpdating"),
				detail: t("ViewEditTournament.tournamentUpdated"),
				life: 3000,
				closable: false,
			})
		})
		.catch(() => {
			toast.add({
				severity: "error",
				summary: t("ViewEditTournament.tournamentUpdating"),
				detail: t("ViewEditTournament.tournamentUpdateFailed"),
				life: 3000,
				closable: false,
			})
		})
}
</script>

<style scoped></style>

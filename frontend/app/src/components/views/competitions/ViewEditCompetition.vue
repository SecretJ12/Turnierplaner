<template>
	<FormCompetition
		v-if="competition === null"
		:competition="CompetitionDefault"
		:disabled="true"
		:submit-text="t('general.update')"
		:header="t('ViewEditCompetition.header')"
		@submit="submit"
	/>
	<FormCompetition
		v-else
		:competition="competition"
		:disabled="false"
		:submit-text="t('general.update')"
		:header="t('ViewEditCompetition.header')"
		@submit="submit"
	/>
</template>

<script lang="ts" setup>
import { router } from "@/main"
import { useRoute } from "vue-router"
import FormCompetition from "@/components/views/competitions/FormCompetition.vue"
import { useI18n } from "vue-i18n"
import { CompetitionDefault, CompetitionServer } from "@/interfaces/competition"
import { getCompetitionDetails, updateCompetition } from "@/backend/competition"
import { useToast } from "primevue/usetoast"

const { t } = useI18n({ inheritLocale: true })
const toast = useToast()

const route = useRoute()

const competition = getCompetitionDetails(
	<string>route.params.tourId,
	<string>route.params.compId,
	t,
	toast,
	{
		err: () => {
			router.back()
		},
	},
)

function submit(server_data: CompetitionServer) {
	updateCompetition(server_data, <string>route.params.tourId, t, toast, {})
}
</script>

<style scoped></style>

<template>
	<FormCompetition
		:competition="competition"
		:submit-text="t('general.create')"
		:header="t('ViewCreateCompetition.header')"
		@submit="submit"
	/>
</template>

<script lang="ts" setup>
import { router } from "@/main"
import { reactive } from "vue"
import { useRoute } from "vue-router"
import FormCompetition from "@/components/views/competitions/FormCompetition.vue"
import { useI18n } from "vue-i18n"
import {
	Competition,
	CompetitionServer,
	Mode,
	Sex,
	SignUp,
	TourType,
} from "@/interfaces/competition"
import { useToast } from "primevue/usetoast"
import { addCompetition } from "@/backend/competition"

const { t } = useI18n({ inheritLocale: true })
const toast = useToast()

const route = useRoute()

const competition = reactive<Competition>({
	name: "",
	description: "",
	tourType: TourType.KNOCKOUT,
	mode: Mode.SINGLE,
	signUp: SignUp.INDIVIDUAL,
	playerA: {
		sex: Sex.ANY,
		hasMinAge: false,
		minAge: null,
		hasMaxAge: false,
		maxAge: null,
	},
	playerB: {
		different: false,
		sex: Sex.ANY,
		hasMinAge: false,
		minAge: null,
		hasMaxAge: false,
		maxAge: null,
	},
})

function submit(server_data: CompetitionServer) {
	addCompetition(server_data, <string>route.params.tourId, t, toast, {
		suc: () => {
			router.push({ path: "/tournament/" + route.params.tourId })
		},
	})
}
</script>

<style scoped></style>

<template>
  <FormCompetition :competition="competition" :submit-text="t('general.create')" @submit="submit"/>
</template>

<script lang="ts" setup>
import {router} from "@/main"
import {reactive} from "vue"
import axios from "axios"
import {ElMessage} from "element-plus"
import {useRoute} from "vue-router"
import FormCompetition from "@/components/views/competitions/FormCompetition.vue"
import {useI18n} from "vue-i18n"
import {Competition, CompetitionServer, Mode, Sex, SignUp, TourType} from "@/interfaces/competition"

const {t} = useI18n({inheritLocale: true})

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
		maxAge: null
	},
	playerB: {
		different: false,
		sex: Sex.ANY,
		hasMinAge: false,
		minAge: null,
		hasMaxAge: false,
		maxAge: null
	}
})

function submit(server_data: CompetitionServer) {
	axios.post(`/tournament/${route.params.tourId}/competition/add`, server_data)
		.then(() => {
			router.push({path: "/tournament/" + route.params.tourId})
		})
		.catch(() => {
			ElMessage.error(t("ViewCreateCompetition.creationFailed"))
		})
}
</script>

<style scoped>
</style>
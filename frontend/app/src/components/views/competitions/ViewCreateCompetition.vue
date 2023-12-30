<template>
	<div class="flex justify-content-center w-full">
		<Card id="card">
			<template #title>{{ t("ViewCreateCompetition.header") }}</template>
			<template #content>
				<FormCompetition
					ref="form"
					:competition="competition"
					:disabled="false"
					@submit="submit"
				/>
			</template>
			<template #footer>
				<div class="justify-content-end flex">
					<Button
						:label="t('general.create')"
						@click="() => form !== null && form.onSubmit()"
					/>
				</div>
			</template>
		</Card>
	</div>
</template>

<script lang="ts" setup>
import { router } from "@/main"
import { reactive, ref } from "vue"
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
const form = ref<InstanceType<typeof FormCompetition> | null>(null)

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
			router.push({
				name: "Competitions",
				params: { tourId: route.params.tourId },
			})
		},
	})
}
</script>

<style scoped>
#card {
	width: min(90dvw, 50rem);
}
</style>

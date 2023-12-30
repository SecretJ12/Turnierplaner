<template>
	<div class="flex justify-content-center">
		<Card id="card">
			<template #content>
				<FormCompetition
					v-if="competition === null || reload"
					:competition="CompetitionDefault"
					:disabled="true"
					@submit="submit"
				/>
				<FormCompetition
					v-else
					ref="form"
					:competition="competition"
					:disabled="false"
					@submit="submit"
				/>
			</template>
		</Card>
	</div>

	<div class="mt-5 grid grid-nogutter justify-content-between">
		<Button
			:disabled="true"
			:label="t('general.back')"
			icon="pi pi-angle-left"
			icon-pos="left"
		/>
		<Button
			:disabled="competition === null"
			:label="t('general.save')"
			severity="success"
			@click="() => form !== null && form.onSubmit()"
		/>
		<Button
			v-if="route.params.step !== 'scheduleMatches'"
			:label="t('general.next')"
			icon="pi pi-angle-right"
			icon-pos="right"
			@click="nextPage"
		/>
	</div>
</template>

<script lang="ts" setup>
import { router } from "@/main"
import { useRoute } from "vue-router"
import FormCompetition from "@/components/views/competitions/FormCompetition.vue"
import { useI18n } from "vue-i18n"
import { CompetitionDefault, CompetitionServer } from "@/interfaces/competition"
import { getCompetitionDetails, updateCompetition } from "@/backend/competition"
import { useToast } from "primevue/usetoast"
import { ref } from "vue"
import Button from "primevue/button"

const { t } = useI18n({ inheritLocale: true })
const toast = useToast()

const route = useRoute()
const form = ref<InstanceType<typeof FormCompetition> | null>(null)

function sleep(milliseconds: number) {
	return new Promise((resolve) => setTimeout(resolve, milliseconds))
}

const reload = ref(false)
const competition = getCompetitionDetails(route, t, toast, {
	suc: async () => {
		reload.value = true
		await sleep(100)
		reload.value = false
	},
	err: () => {
		router.back()
	},
})

function submit(server_data: CompetitionServer) {
	updateCompetition(server_data, <string>route.params.tourId, t, toast, {})
}

function nextPage() {
	router.replace({
		name: "editTeams",
		params: { tourId: route.params.tourId, compId: route.params.compId },
	})
}
</script>

<style scoped>
#card {
	width: min(90dvw, 50rem);
}
</style>

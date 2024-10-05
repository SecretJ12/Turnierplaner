<template>
	<ViewTable v-if="competition" :competition="competition" :update="1" />

	<div class="grid grid-nogutter justify-content-between">
		<Button
			label="Back"
			icon="pi pi-angle-left"
			icon-pos="left"
			@click="previousPage"
		/>
		<!-- TODO add @click -->
		<Button
			v-if="
				!(
					competition?.mode === Mode.SINGLE ||
					competition?.signUp === SignUp.TOGETHER
				)
			"
			:label="t('general.save')"
		>
		</Button>
		<Button
			v-if="route.params.step !== 'scheduleMatches'"
			label="Next"
			icon="pi pi-angle-right"
			icon-pos="right"
			@click="nextPage"
		/>
	</div>
</template>

<script setup lang="ts">
import ViewTable from "@/components/views/competition/signup/ViewTable.vue"
import { useToast } from "primevue/usetoast"
import { useRoute, useRouter } from "vue-router"
import { useI18n } from "vue-i18n"
import { onUpdated } from "vue"
import { Mode, SignUp } from "@/interfaces/competition"
import Button from "primevue/button"
import { getCompetitionDetails } from "@/backend/competition"

const { t } = useI18n()
const router = useRouter()
const route = useRoute()
const toast = useToast()

const { data: competition } = getCompetitionDetails(route, t, toast)

onUpdated(() => {})

function save() {
	// TODO udpate players
	toast.add({
		severity: "success",
		summary: "Success",
		detail: "Players updated",
		life: 3000,
	})
}

defineExpose({ save })

function previousPage() {
	router.replace({
		name: "editTeams",
		params: { tourId: route.params.tourId, compId: route.params.compId },
	})
}

function nextPage() {
	router.replace({
		name: "selectType",
		params: { tourId: route.params.tourId, compId: route.params.compId },
	})
}
</script>

<style scoped></style>

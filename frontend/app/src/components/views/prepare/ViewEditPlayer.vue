<template>
	<ViewTable v-if="competition" :competition="competition" :update="1" />

	<div class="grid grid-nogutter justify-content-between">
		<Button
			label="Back"
			icon="pi pi-angle-left"
			style="visibility: hidden"
			disabled
		/>
		<!-- TODO add @click -->
		<Button :label="t('general.save')"> </Button>
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
import { getCompetitionDetails } from "@/backend/competition"
import { onUpdated } from "vue"

const { t } = useI18n({ inheritLocale: true })
const router = useRouter()
const route = useRoute()
const toast = useToast()

const competition = getCompetitionDetails(route, t, toast, {
	suc: () => {
		if (competition.value === null) return
	},
})

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

function nextPage() {
	router.replace({
		name: "selectType",
		params: { tourId: route.params.tourId, compId: route.params.compId },
	})
}
</script>

<style scoped></style>

<template>
	<div class="flex flex-column gap-1">
		<div class="grid">
			<div class="col-4">
				<Card>
					<template #title>Matches</template>
					<template #content> We show the matches here </template>
				</Card>
			</div>
			<div class="col-8">
				<Card>
					<template #title>Calendar</template>
					<template #content> The calendar should be shown here </template>
				</Card>
			</div>
		</div>
	</div>
</template>

<script setup lang="ts">
import { getCompetitionDetails } from "@/backend/competition"
import { useRoute, useRouter } from "vue-router"
import { useToast } from "primevue/usetoast"
import { useI18n } from "vue-i18n"

const route = useRoute()
const router = useRouter()
const toast = useToast()
const { t } = useI18n({ inheritLocale: true })

const isUpdating = defineModel<boolean>("isUpdating", { default: false })
const { data: competition } = getCompetitionDetails(route, t, toast)

function prevPage() {
	router.replace({
		name: "assignMatches",
		params: { tourId: route.params.tourId, compId: route.params.compId },
	})
}

function nextPage() {
	// TODO
	toast.add({
		severity: "success",
		summary: "Competition updated",
		detail: "TODO",
		life: 3000,
	})
}

function save() {
	// TODO
	isUpdating.value = true
	toast.add({
		severity: "success",
		summary: "Scheduling saved",
		detail: "TODO",
		life: 3000,
	})
	isUpdating.value = false
}

defineExpose({ prevPage, save, nextPage })
</script>

<style scoped></style>

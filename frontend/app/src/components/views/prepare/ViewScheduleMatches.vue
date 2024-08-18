<template>
	<div class="flex flex-column gap-1">
		<div class="grid">
			<div class="col-4">
				<Card>
					<template #title>Matches</template>
					<template #content>
						<MatchesContainerDraggable :competition="competition" />
					</template>
				</Card>
			</div>
			<div class="col-8">
				<Card>
					<template #title>Calendar</template>
					<template #content>
						The calendar should be shown here
					</template>
				</Card>
			</div>
		</div>

		<div class="grid grid-nogutter justify-content-between mt-4">
			<Button label="Back" icon="pi pi-angle-left" @click="prevPage" />
			<Button
				label="Complete"
				icon="pi pi-check"
				icon-pos="right"
				class="p-button-success"
				@click="complete"
			/>
		</div>
	</div>
</template>

<script setup lang="ts">
import { getCompetitionDetails } from "@/backend/competition"
import { useRoute, useRouter } from "vue-router"
import { useToast } from "primevue/usetoast"
import { useI18n } from "vue-i18n"
import MatchesContainerDraggable from "@/components/views/prepare/scheduleMatches/MatchesContainerDraggable.vue"

const route = useRoute()
const router = useRouter()
const toast = useToast()
const { t } = useI18n({ inheritLocale: true })

const { data: competition } = getCompetitionDetails(route, t, toast, {
	suc: () => {
		if (competition.value === null) return
	},
})

function prevPage() {
	router.replace({
		name: "assignMatches",
		params: { tourId: route.params.tourId, compId: route.params.compId },
	})
}

function complete() {
	// TODO
	toast.add({
		severity: "success",
		summary: "Competition updated",
		detail: "TODO",
		life: 3000,
	})
}
</script>

<style scoped></style>

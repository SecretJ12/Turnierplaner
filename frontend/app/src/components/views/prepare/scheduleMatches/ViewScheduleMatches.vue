<template>
	<div class="grid">
		<div class="col-4 flex flex-column gap-3">
			<Card>
				<template #title>Options</template>
				<template #content>
					<MultiSelect
						v-model="selectedCourts"
						:loading="!courts || !selectedCourts"
						:options="courts"
						option-label="name"
						placeholder="Select courts"
						class="w-full"
					>
						<template #footer>
							<div class="w-full flex align-items-center p-2">
								<ViewCreateCourtSmall />
							</div>
						</template>
					</MultiSelect>
				</template>
			</Card>
			<Card>
				<template #title> Matches</template>
				<template #content>
					<MatchesContainerDraggable
						v-model="matches"
						:is-updating="isUpdating"
					/>
				</template>
			</Card>
		</div>
		<div class="col-8">
			<div class="flex flex-column gap-3">
				<SchedulingCalendar
					:courts="selectedCourts"
					@remove-id="
						(id) => {
							const extEventToDeletePos = matches.findIndex(
								(match) => id === match.id,
							)
							if (extEventToDeletePos > -1)
								matches.splice(extEventToDeletePos, 1)
						}
					"
				/>
			</div>
		</div>
	</div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from "vue-router"
import { useToast } from "primevue/usetoast"
import SchedulingCalendar from "@/components/views/prepare/scheduleMatches/SchedulingCalendar.vue"
import { getCourts } from "@/backend/court"
import { ref, watch } from "vue"
import { Court } from "@/interfaces/court"
import ViewCreateCourtSmall from "@/components/views/court/ViewCreateCourtSmall.vue"
import MatchesContainerDraggable from "@/components/views/prepare/scheduleMatches/MatchesContainerDraggable.vue"
import { Match } from "@/interfaces/match"

const route = useRoute()
const router = useRouter()
const toast = useToast()

const isUpdating = defineModel<boolean>("isUpdating", { default: false })

const { data: courts } = getCourts()

const matches = ref<Match[]>([])

const selectedCourts = ref<Court[]>([])
watch(selectedCourts, () => {
	selectedCourts.value = selectedCourts.value.sort((a, b) =>
		a.name < b.name ? -1 : 1,
	)
})

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

<template>
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

<script setup lang="ts">
import ViewCreateCourtSmall from "@/components/views/court/ViewCreateCourtSmall.vue"
import { Court } from "@/interfaces/court"
import { getCourts } from "@/backend/court"
import { watch } from "vue"

const selectedCourts = defineModel<Court[]>({ default: [] })
const { data: courts } = getCourts()

watch(selectedCourts, () => {
	selectedCourts.value = selectedCourts.value.sort((a, b) =>
		a.name < b.name ? -1 : 1,
	)
})
</script>

<style scoped></style>

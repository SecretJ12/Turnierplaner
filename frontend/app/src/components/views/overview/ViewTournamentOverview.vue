<template>
	<div class="w-full p-2">
		<Card style="margin-top: -10px !important">
			<template #content>
				<SelectButton
					v-model="calendarView"
					class="pb-2"
					:options="views"
					option-label="label"
				/>
				<OverviewCalendar v-if="calendarView.id === 'calendar'" />
				<div v-else-if="calendarView.id === 'list'">test</div>
			</template>
		</Card>
	</div>
</template>

<script setup lang="ts">
import { ref } from "vue"
import OverviewCalendar from "@/components/views/overview/OverviewCalendar.vue"

const views = [
	{
		id: "calendar",
		label: "Calendar",
	},
	{
		id: "list",
		label: "List",
	},
]

const calendarView = ref(views[0])
</script>

<style scoped></style>

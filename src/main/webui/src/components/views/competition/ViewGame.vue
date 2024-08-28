<template>
	<Suspense>
		<ViewGroupSystem v-if="props.tourType === TourType.GROUPS" />
		<ViewKnockoutSystem
			v-else-if="props.tourType === TourType.KNOCKOUT"
			:mode="props.mode"
		/>
		<p v-else>Invalid type</p>
	</Suspense>
</template>

<script lang="ts" setup>
import ViewGroupSystem from "@/components/views/competition/groupSystem/ViewGroupSystem.vue"
import ViewKnockoutSystem from "@/components/views/competition/knockoutSystem/ViewKnockoutSystem.vue"
import { Mode, TourType } from "@/interfaces/competition"

const props = defineProps<{
	tourType: TourType
	mode: Mode
}>()
</script>

<style scoped></style>

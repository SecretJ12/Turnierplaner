<template>
	<Suspense>
		<ViewGroupSystem v-if="props.tourType === CompType.GROUPS" />
		<ViewKnockoutSystem
			v-else-if="props.tourType === CompType.KNOCKOUT"
			:mode="props.mode"
		/>
		<p v-else>Invalid type</p>
	</Suspense>
</template>

<script lang="ts" setup>
import ViewGroupSystem from "@/components/views/competition/groupSystem/ViewGroupSystem.vue"
import ViewKnockoutSystem from "@/components/views/competition/knockoutSystem/ViewKnockoutSystem.vue"
import { Mode, CompType } from "@/interfaces/competition"

const props = defineProps<{
	tourType: CompType
	mode: Mode
}>()
</script>

<style scoped></style>

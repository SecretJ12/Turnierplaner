<template>
	<ViewTable
		v-if="props.competition"
		:competition="props.competition"
		:update="1"
	/>
</template>

<script setup lang="ts">
import { Competition } from "@/interfaces/competition"
import ViewTable from "@/components/views/competition/signup/ViewTable.vue"

// eslint-disable-next-line @typescript-eslint/no-unused-vars
const props = defineProps<{
	competition: Competition | undefined
}>()
</script>

<style scoped></style>

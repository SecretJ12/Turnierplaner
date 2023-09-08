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
import { useToast } from "primevue/usetoast"

// eslint-disable-next-line @typescript-eslint/no-unused-vars
const props = defineProps<{
	competition: Competition | undefined
}>()

const toast = useToast()

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
</script>

<style scoped></style>

<template>
	<span class="cursor-pointer hover:underline" @click="click">
		{{ props.competition }}
	</span>
</template>

<script setup lang="ts">
import { router } from "@/main"

const props = defineProps<{
	tournament?: string
	competition?: string
}>()

function click() {
	if (props.tournament && props.competition)
		router.push({
			name: "Competition",
			params: {
				tourId: props.tournament,
				compId: props.competition,
			},
		})
}
</script>

<style scoped></style>

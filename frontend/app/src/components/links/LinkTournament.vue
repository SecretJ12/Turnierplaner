<template>
	<span class="cursor-pointer hover:underline" @click="click">
		{{ props.tournament }}
	</span>
</template>

<script setup lang="ts">
import { router } from "@/main"

const props = defineProps<{
	tournament?: string
}>()

function click() {
	if (props.tournament)
		router.push({
			name: "Competitions",
			params: {
				tourId: props.tournament,
			},
		})
}
</script>

<style scoped></style>

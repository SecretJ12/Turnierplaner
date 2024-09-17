<template>
	<span
		v-if="props.player"
		class="cursor-pointer hover:underline"
		@click="click"
	>
		<template v-if="inverted">
			{{ props.player.lastName }}, {{ props.player.firstName }}
		</template>
		<template v-else> {{ props.player.name }}</template>
	</span>
</template>

<script setup lang="ts">
import { Player } from "@/interfaces/player"
import { router } from "@/main"

const props = withDefaults(
	defineProps<{
		player?: Player | null
		inverted?: boolean
	}>(),
	{
		player: null,
		inverted: false,
	},
)

function click() {
	if (props.player)
		router.push({
			name: "Player overview",
			params: {
				playerId: props.player.id,
			},
		})
}
</script>

<style scoped></style>

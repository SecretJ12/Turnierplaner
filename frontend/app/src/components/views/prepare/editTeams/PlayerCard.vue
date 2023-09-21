<template>
	<div
		class="border-round select-none cursor-pointer inline p-3 text-50"
		:class="{ 'bg-yellow-500': secondary, 'bg-primary-400': !secondary }"
	>
		{{ props.player.name }}
	</div>
</template>

<script setup lang="ts">
const props = withDefaults(
	defineProps<{ player: { name: string }; secondary?: boolean }>(),
	{
		secondary: false,
	},
)
</script>

<style scoped></style>

<template>
	<div
		class="border-round select-none cursor-pointer pl-3 pr-3 h-3rem inline text-50 flex align-items-center"
		:class="{
			'bg-yellow-500': secondary,
			'bg-primary-400': !secondary,
			'text-black': secondary,
		}"
	>
		<span>{{ props.player.name }}</span>
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

<style scoped>
.text-black {
	color: black !important;
}
</style>

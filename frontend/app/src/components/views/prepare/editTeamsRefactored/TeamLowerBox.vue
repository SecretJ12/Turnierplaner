<template>
	<div
		:class="{
			bottomSame: !props.different,
			bottomDifferent: props.different,
		}"
		class="border-3 border-top-none border-dashed h-3rem border-round-bottom content-box"
	></div>
</template>

<script setup lang="ts">
const props = defineProps<{
	name: string
	different: boolean
}>()
</script>

<style scoped>
.bottomSame {
	border-left-color: var(--primary-400);
	border-bottom-color: var(--primary-400);
	border-right-color: var(--primary-400);
}

.bottomDifferent {
	border-left-color: var(--yellow-500);
	border-bottom-color: var(--yellow-500);
	border-right-color: var(--yellow-500);
}
</style>

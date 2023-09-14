<template>
	<div id="player" class="pointer-events-none border-solid border-1 flex align-items-center justify-content-center">{{props.name}}</div>
</template>

<script setup lang="ts">
const props = defineProps<{ name: string }>()
</script>

<style scoped></style>
<template>Edit players: {{ competition.name }}</template>

<script setup lang="ts">
import { Competition } from "@/interfaces/competition"

// eslint-disable-next-line @typescript-eslint/no-unused-vars
const props = defineProps<{
	competition: Competition
}>()
</script>

<style scoped></style>

<template>
  Choose mode: {{ competition.name }}
</template>

<script setup lang="ts">
import { Competition } from "@/interfaces/competition"

const props = defineProps<{
  competition: Competition
}>()
</script>

<style scoped>

</style>
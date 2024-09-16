<template>
	<div class="w-full p-2">
		<Card style="margin-top: -10px !important">
			<template #title> Player Overview - {{ player?.name }}</template>
		</Card>
	</div>
</template>

<script setup lang="ts">
import { useRoute } from "vue-router"
import { getPlayer } from "@/backend/player"
import { useI18n } from "vue-i18n"
import { useToast } from "primevue/usetoast"

const route = useRoute()
const { t } = useI18n({ inheritLocale: true })
const toast = useToast()

const { data: player } = getPlayer(route, t, toast)
</script>

<style scoped></style>

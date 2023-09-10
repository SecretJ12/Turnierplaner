<template>
	Assign matches: {{ competition?.name }}

	<div class="grid grid-nogutter justify-content-between mt-4">
		<Button label="Back" icon="pi pi-angle-left" @click="prevPage" />
		<!-- TODO add @click -->
		<Button
			v-if="route.params.step === 'editPlayers'"
			:label="t('general.save')"
		>
		</Button>
		<Button
			v-if="route.params.step !== 'scheduleMatches'"
			label="Next"
			icon="pi pi-angle-right"
			icon-pos="right"
			@click="nextPage"
		/>
	</div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from "vue-router"
import { useI18n } from "vue-i18n"
import { getCompetitionDetails } from "@/backend/competition"
import { useToast } from "primevue/usetoast"

const route = useRoute()
const router = useRouter()
const toast = useToast()
const { t } = useI18n({ inheritLocale: true })

const competition = getCompetitionDetails(route, t, toast, {
	suc: () => {
		if (competition.value === null) return
	},
})

function prevPage() {
	router.replace({
		name: "assignTeams",
		params: { tourId: route.params.tourId, compId: route.params.compId },
	})
}

function nextPage() {
	router.replace({
		name: "scheduleMatches",
		params: { tourId: route.params.tourId, compId: route.params.compId },
	})
}
</script>

<style scoped></style>
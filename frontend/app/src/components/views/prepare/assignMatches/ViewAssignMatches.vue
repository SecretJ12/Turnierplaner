<template>
	{{ competition?.tourType }}
	<div class="flex flex-column gap-1">
		<Groups v-if="competition?.tourType === TourType.GROUPS" />
		<Knockout v-if="competition?.tourType === TourType.KNOCKOUT" />

		<div class="grid grid-nogutter justify-content-between mt-4">
			<Button label="Back" icon="pi pi-angle-left" @click="prevPage" />
			<Button :label="t('general.save')" severity="success"></Button>
			<Button
				v-if="route.params.step !== 'scheduleMatches'"
				label="Next"
				icon="pi pi-angle-right"
				icon-pos="right"
				@click="nextPage"
			/>
		</div>
	</div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from "vue-router"
import { useI18n } from "vue-i18n"
import { getCompetitionDetails } from "@/backend/competition"
import { useToast } from "primevue/usetoast"
import { TourType } from "@/interfaces/competition"
import Groups from "@/components/views/prepare/assignMatches/AssignMatchesGroups.vue"
import Knockout from "@/components/views/prepare/assignMatches/AssignMatchesKnockout.vue"

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
		name: "editTeams",
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

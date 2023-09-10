<template>
	<div class="w-full flex flex-row justify-content-around">
		<Card
			style="width: 25em"
			:class="{
				active: competition?.tourType === TourType.GROUPS,
			}"
			@click="updateMode(TourType.GROUPS)"
		>
			<template #content>
				<div class="flex flex-row">
					<span class="material-symbols-outlined">grid_view</span>
					<b class="align-self-center">
						&nbsp;
						{{ t("CompetitionSettings.groups") }}
					</b>
				</div>
			</template>
		</Card>
		<!-- TODO choose size of groups -->
		<Card
			style="width: 25em"
			:class="{
				active: competition?.tourType === TourType.KNOCKOUT,
			}"
			@click="updateMode(TourType.KNOCKOUT)"
		>
			<template #content>
				<div class="flex flex-row">
					<span class="material-symbols-outlined">groups</span>
					<b class="align-self-center">
						&nbsp;
						{{ t("CompetitionSettings.knockout") }}</b
					>
				</div>
			</template>
		</Card>
	</div>
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
import {
	competitionClientToServer,
	CompetitionServer,
	TourType,
} from "@/interfaces/competition"

import { useI18n } from "vue-i18n"
import { getCompetitionDetails, updateCompetition } from "@/backend/competition"
import { useToast } from "primevue/usetoast"
import { useRoute, useRouter } from "vue-router"

const { t } = useI18n({ inheritLocale: true })
const toast = useToast()
const route = useRoute()
const router = useRouter()

const competition = getCompetitionDetails(route, t, toast, {
	suc: () => {
		if (competition.value === null) return
	},
})

function updateMode(mode: TourType) {
	if (competition.value === null) return

	competition.value.tourType = mode

	const server_data: CompetitionServer = competitionClientToServer(
		competition.value,
	)
	updateCompetition(server_data, <string>route.params.tourId, t, toast, {})
}

function prevPage() {
	router.replace({
		name: "editPlayers",
		params: { tourId: route.params.tourId, compId: route.params.compId },
	})
}

function nextPage() {
	router.replace({
		name: "assignTeams",
		params: { tourId: route.params.tourId, compId: route.params.compId },
	})
}
</script>

<style scoped>
.active {
	border: 2px solid #b7bbff;
}
</style>
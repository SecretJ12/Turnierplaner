<template>
	<div class="w-full flex flex-row justify-content-around">
		<div>
			<Card
				style="width: 25em"
				:class="{
					active: competition?.tourType === TourType.GROUPS,
				}"
				class="h-fit"
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
			<div class="mt-3">
				<label>Number of groups</label>
				<!-- TODO select number of groups & internalization
					Minimum: 2
					Maximum: at least 3 persons per group -->
				<Dropdown
					v-if="competition?.tourType === TourType.GROUPS"
					placeholder="Select number of groups"
					:options="[2, 3, 4]"
					class="w-full"
				/>
			</div>
		</div>
		<Card
			style="width: 25em"
			:class="{
				active: competition?.tourType === TourType.KNOCKOUT,
			}"
			class="h-fit"
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

<template>
	<div class="w-full flex flex-row justify-content-around">
		<Card
			style="width: 25em"
			v-bind:class="{
				active: currentMode === TourType.GROUPS,
			}"
			@click="updateMode(TourType.GROUPS)"
		>
			<template #content>
				<div class="flex flex-row">
					<span class="material-symbols-outlined">grid_view</span>
					<b class="align-self-center">
						&nbsp
						{{ t("CompetitionSettings.groups") }}
					</b>
				</div>
			</template>
		</Card>
		<Card
			style="width: 25em"
			:class="{
				active: currentMode === TourType.KNOCKOUT,
			}"
			@click="updateMode(TourType.KNOCKOUT)"
		>
			<template #content>
				<div class="flex flex-row">
					<span class="material-symbols-outlined">groups</span>
					<b class="align-self-center">
						&nbsp
						{{ t("CompetitionSettings.knockout") }}</b
					>
				</div>
			</template>
		</Card>
	</div>
</template>

<script setup lang="ts">
import {
	Competition,
	competitionClientToServer,
	competitionFormToServer,
	CompetitionServer,
	TourType,
} from "@/interfaces/competition"

import { useI18n } from "vue-i18n"
import { ref } from "vue"
import { updateCompetition } from "@/backend/competition"
import { useToast } from "primevue/usetoast"
import { useRoute } from "vue-router"

// eslint-disable-next-line @typescript-eslint/no-unused-vars
const props = defineProps<{
	competition: Competition
}>()
const { t } = useI18n({ inheritLocale: true })
const toast = useToast()
const route = useRoute()

const currentMode = ref(props.competition.tourType)

function updateMode(mode: TourType) {
	const server_data: CompetitionServer = competitionClientToServer({
		id: props.competition.id,
		name: props.competition.name,
		description: props.competition.description,
		tourType: mode,
		mode: props.competition.mode,
		signUp: props.competition.signUp,
		playerA: props.competition.playerA,
		playerB: props.competition.playerB,
	})
	console.log(server_data)
	updateCompetition(server_data, <string>route.params.tourId, t, toast, {})
	currentMode.value = mode
}
</script>

<style scoped>
.active {
	border: 2px solid #b7bbff;
}
</style>

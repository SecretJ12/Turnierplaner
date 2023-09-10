<template>
	<div id="button">
		<Card>
			<template #header>
				<TabMenu
					v-model:activeIndex="activeTab"
					:model="compList"

					@tab-change="tabChange"
				/>
				<Steps
					:model="stepList"
					aria-label="Form Steps"
					:readonly="true"
					class="mt-5"
				/>
			</template>
			<template #content>
				<!-- TODO check if this can be replaced with router implementation -->
				<ViewEditPlayer
					v-if="route.params.step === 'editPlayers'"
					:competition="competitions[activeTab]"
					ref="editPlayer"
				></ViewEditPlayer>
				<ViewChooseMode
					v-else-if="
						route.params.step === 'selectMode' && competitions[activeTab]
					"
					:competition="competitions[activeTab]"
				></ViewChooseMode>
				<ViewAssignTeams
					v-else-if="route.params.step === 'assignTeams'"
					:competition="competitions[activeTab]"
				/>
				<ViewAssignMatches
					v-else-if="route.params.step === 'assignMatches'"
					:competition="competitions[activeTab]"
				/>
				<ViewScheduleMatches
					v-else-if="route.params.step === 'scheduleMatches'"
					:competition="competitions[activeTab]"
				/>
			</template>
			<template #footer>
				<div class="grid grid-nogutter justify-content-between">
					<Button
						label="Back"
						icon="pi pi-angle-left"
						@click="prevPage"
						:disabled="route.params.step === 'editPlayers'"
					/>
					<Button
						v-if="route.params.step === 'editPlayers'"
						:label="t('general.save')"
						@click="updatePlayers"
					>
					</Button>
					<Button
						v-if="route.params.step !== 'scheduleMatches'"
						label="Next"
						icon="pi pi-angle-right"
						icon-pos="right"
						@click="nextPage"
					/>
					<Button
						v-else
						label="Complete"
						icon="pi pi-check"
						icon-pos="right"
						class="p-button-success"
						@click="complete"
					/>
				</div>
			</template>
		</Card>
	</div>
</template>

<script setup lang="ts">
import ViewEditPlayer from "@/components/views/prepare/ViewEditPlayer.vue"
import ViewChooseMode from "@/components/views/prepare/ViewChooseMode.vue"
import ViewAssignTeams from "@/components/views/prepare/ViewAssignTeams.vue"
import ViewAssignMatches from "@/components/views/prepare/ViewAssignMatches.vue"
import ViewScheduleMatches from "@/components/views/prepare/ViewScheduleMatches.vue"
import axios from "axios"
import {
	Competition,
	CompetitionServer,
	competitionServerToClient,
} from "@/interfaces/competition"
import { useRoute, useRouter } from "vue-router"
import { ref } from "vue"
import { useI18n } from "vue-i18n"
import { useToast } from "primevue/usetoast"
import { TabMenuChangeEvent } from "primevue/tabmenu"

const { t } = useI18n({ inheritLocale: true })
const router = useRouter()
const route = useRoute()
const toast = useToast()

const competitions = ref<Competition[]>([])
const activeTab = ref<number>(0)
const compList = ref<{ label: string }[]>([])

// TODO add internalization
const stepNames = [
	"editPlayers",
	"selectMode",
	"assignTeams",
	"assignMatches",
	"scheduleMatches",
]

const stepList = ref([
	{
		label: "Edit Players",
		to: `/tournament/${route.params.tourId}/prepare/editPlayers/${route.params.compId}`,
	},
	{
		label: "Choose Mode",
		to: `/tournament/${route.params.tourId}/prepare/selectMode/${route.params.compId}`,
	},
	{
		label: "Assign Teams",
		to: `/tournament/${route.params.tourId}/prepare/assignTeams/${route.params.compId}`,
	},
	{
		label: "Assign Matches",
		to: `/tournament/${route.params.tourId}/prepare/assignMatches/${route.params.compId}`,
	},
	{
		label: "Schedule Matches",
		to: `/tournament/${route.params.tourId}/prepare/scheduleMatches/${route.params.compId}`,
	},
])

// TODO BUG steps doesnt show current step after refresh (solution: call prevPage() nextPage() or use router path without space!)
axios
	.get<CompetitionServer[]>(
		`/tournament/${route.params.tourId}/competition/prepare`,
	)
	.then((response) => {
		if (response.status === 200) {
			competitions.value = response.data.map(competitionServerToClient)
		} else {
			toast.add({
				severity: "error",
				summary: t("ViewCompetitions.loadingFailed"),
				life: 3000,
			})
		}
		for (const compElement of competitions.value) {
			compList.value.push({ label: compElement.name })
		}
	})
	.catch((error) => {
		toast.add({
			severity: "error",
			summary: t("ViewCompetitions.loadingFailed"),
			life: 3000,
		})
		console.log(error)
		router.push("/")
	})

const currentComp = ref<Competition | null>(
	competitions.value ? competitions.value[0] : null,
)

const editPlayer = ref<null | { save: Function }>(null)

function updatePlayers() {
	editPlayer.value?.save()
}

function stepToIndex(step: string): number {
	return stepNames.findIndex((s) => s === step)
}

function nextPage() {
	router.replace(
		`/tournament/${route.params.tourId}/prepare/${
			stepNames[stepToIndex(<string>route.params.step) + 1]
		}/${route.params.compId}`,
	)
}

function prevPage() {
	router.replace(
		`/tournament/${route.params.tourId}/prepare/${
			stepNames[stepToIndex(<string>route.params.step) - 1]
		}/${route.params.compId}`,
	)
}

function complete() {
	toast.add({
		severity: "success",
		summary: "Competition updated",
		detail: "The tournament has been updated.",
	})
}

function tabChange(event: TabMenuChangeEvent) {
	activeTab.value = event.index
	currentComp.value = competitions.value[event.index]
	router.replace(
		`/tournament/${route.params.tourId}/prepare/${route.params.step}/${
			competitions.value[event.index].name
		}`,
	)

	for (let i = 0; i < stepList.value.length; i++) {
		stepList.value[i].to = `/tournament/${route.params.tourId}/prepare/${
			stepNames[i]
		}/${competitions.value[event.index].name}`
	}
}
</script>

<style scoped>
#button {
	width: 100%;
	margin: 10px;
	display: flex;
	flex-wrap: wrap;
	flex-direction: row;
	justify-content: center;
}

::v-deep(b) {
	display: block;
}

::v-deep(.p-card-body) {
	padding: 2rem;
}
</style>
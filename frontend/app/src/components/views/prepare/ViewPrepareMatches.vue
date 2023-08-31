<template>
	<div id="prepare">
		<div>
			<div class="card">
				<Steps :model="stepList" aria-label="Form Steps" :exact="false" />
			</div>
			<TabMenu
				v-model:activeIndex="activeTab"
				:model="compList"
				@tab-change="tabChange"
			/>
			<Card>
				<template #title>
					<span v-if="route.params.step === 'editPlayers'">Edit Player</span>
					<span v-else-if="route.params.step === 'selectMode'"
						>Select Mode</span
					>
					<span v-else-if="route.params.step === 'assignTeams'"
						>Assign Teams</span
					>
					<span v-else-if="route.params.step === 'assignMatches'"
						>Assign Matches</span
					>
					<span v-else-if="route.params.step === 'scheduleMatches'"
						>Schedule Matches</span
					>
					- {{ currentComp?.name }}:
				</template>
				<template #content>
					<ViewEditPlayer
						v-if="route.params.step === 'editPlayers'"
						:competition="competitions[activeTab]"
					></ViewEditPlayer>
					<ViewChooseMode
						v-else-if="route.params.step === 'selectMode'"
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
						<i v-if="route.params.step === 'editPlayers'" />
						<Button
							v-else
							label="Back"
							icon="pi pi-angle-left"
							@click="prevPage"
						/>
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
import { ElMessage } from "element-plus"
import { router } from "@/main"
import { useRoute } from "vue-router"
import { ref } from "vue"
import { useI18n } from "vue-i18n"
import { useToast } from "primevue/usetoast"
import { TabMenuChangeEvent } from "primevue/tabmenu"

const { t } = useI18n({ inheritLocale: true })
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

await axios
	.get<CompetitionServer[]>(
		`/tournament/${route.params.tourId}/competition/list`,
	)
	.then((response) => {
		if (response.status === 200) {
			competitions.value = response.data.map(competitionServerToClient)
			checkComp()
		} else {
			ElMessage.error(t("ViewCompetitions.loadingFailed"))
		}
		for (const compElement of competitions.value) {
			compList.value.push({ label: compElement.name })
		}
	})
	.catch((error) => {
		ElMessage.error(t("ViewCompetitions.loadingFailed"))
		console.log(error)
		router.push("/")
	})

const currentComp = ref<Competition>(competitions.value[0])

const stepList = ref([
	{
		label: "Edit Players",
		to: `/tournament/${route.params.tourId}/prepare/editPlayers/${route.params.competition}`,
	},
	{
		label: "Choose Mode",
		to: `/tournament/${route.params.tourId}/prepare/selectMode/${route.params.competition}`,
	},
	{
		label: "Assign Teams",
		to: `/tournament/${route.params.tourId}/prepare/assignTeams/${route.params.competition}`,
	},
	{
		label: "Assign Matches",
		to: `/tournament/${route.params.tourId}/prepare/assignMatches/${route.params.competition}`,
	},
	{
		label: "Schedule Matches",
		to: `/tournament/${route.params.tourId}/prepare/scheduleMatches/${route.params.competition}`,
	},
])

function stepToIndex(step: string): number {
	return stepNames.findIndex((s) => s === step)
}

function checkComp() {
	let change = false
	let step = route.params.step
	let comp = route.params.competition
	if (!stepNames.find((s) => s === route.params.step)) {
		change = true
		step = "editPlayers"
	}
	if (!competitions.value.some((c) => c.name === route.params.competition)) {
		change = true
		comp = competitions.value[0].name
	}
	if (change)
		router.replace(`/tournament/${route.params.tourId}/prepare/${step}/${comp}`)
}

function nextPage() {
	router.replace(
		`/tournament/${route.params.tourId}/prepare/${
			stepNames[stepToIndex(<string>route.params.step) + 1]
		}/${route.params.competition}`,
	)
}

function prevPage() {
	router.replace(
		`/tournament/${route.params.tourId}/prepare/${
			stepNames[stepToIndex(<string>route.params.step) - 1]
		}/${route.params.competition}`,
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
#prepare {
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

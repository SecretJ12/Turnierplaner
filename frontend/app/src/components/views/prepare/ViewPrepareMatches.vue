<template>
	<div id="prepare">
		<el-tabs
			type="border-card"
			v-if="route.params.step === 'editPlayers'"
			class="step"
			v-model="route.params.competition"
			@tab-click="tabClick"
		>
			<el-tab-pane
				v-for="competition in competitions"
				:key="competition.id"
				:label="competition.name"
				:name="competition.name"
			>
				<ViewEditPlayer :competition="competition" />
			</el-tab-pane>
		</el-tabs>
		<el-tabs
			type="border-card"
			v-else-if="route.params.step === 'selectMode'"
			class="step"
			v-model="route.params.competition"
			@tab-click="tabClick"
		>
			<el-tab-pane
				v-for="competition in competitions"
				:key="competition.id"
				:label="competition.name"
				:name="competition.name"
			>
				<ViewChooseMode :competition="competition" />
			</el-tab-pane>
		</el-tabs>
		<el-tabs
			type="border-card"
			v-else-if="route.params.step === 'assignTeams'"
			class="step"
			v-model="route.params.competition"
			@tab-click="tabClick"
		>
			<el-tab-pane
				v-for="competition in competitions"
				:key="competition.id"
				:label="competition.name"
				:name="competition.name"
			>
				<ViewAssignTeams :competition="competition" />
			</el-tab-pane>
		</el-tabs>
		<el-tabs
			type="border-card"
			v-else-if="route.params.step === 'assignMatches'"
			class="step"
			v-model="route.params.competition"
			@tab-click="tabClick"
		>
			<el-tab-pane
				v-for="competition in competitions"
				:key="competition.id"
				:label="competition.name"
				:name="competition.name"
			>
				<ViewAssignMatches :competition="competition" />
			</el-tab-pane>
		</el-tabs>
		<el-tabs
			type="border-card"
			v-else-if="route.params.step === 'scheduleMatches'"
			class="step"
			v-model="route.params.competition"
			@tab-click="tabClick"
		>
			<el-tab-pane
				v-for="competition in competitions"
				:key="competition.id"
				:label="competition.name"
				:name="competition.name"
			>
				<ViewScheduleMatches :competition="competition" />
			</el-tab-pane>
		</el-tabs>

    <!-- TODO color depending on progress of step -->
		<el-space direction="vertical">
			<el-button @click="previous">{{ t('ViewPrepare.steps.previous') }}</el-button>
			<el-steps
				direction="vertical"
				:active="stepToIndex(<string>route.params.step)"
			>
				<el-step :title="t('ViewPrepare.steps.editPlayers')" />
				<el-step :title="t('ViewPrepare.steps.chooseMode')" />
				<el-step :title="t('ViewPrepare.steps.assignTeams')" />
				<el-step :title="t('ViewPrepare.steps.assignMatches')" />
				<el-step :title="t('ViewPrepare.steps.scheduleMatches')" />
			</el-steps>
			<el-button @click="next">{{ t('ViewPrepare.steps.next') }}</el-button>
		</el-space>
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

const { t } = useI18n({ inheritLocale: true })
const route = useRoute()

const competitions = ref<Competition[]>([])
axios
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
	})
	.catch((error) => {
		ElMessage.error(t("ViewCompetitions.loadingFailed"))
		console.log(error)
		router.push("/")
	})

const steps = [
	"editPlayers",
	"selectMode",
	"assignTeams",
	"assignMatches",
	"scheduleMatches",
]

function stepToIndex(step: string): number {
	return steps.findIndex((s) => s === step)
}

function checkComp() {
	let change = false
	let step = route.params.step
	let comp = route.params.competition
	if (!steps.find((s) => s === route.params.step)) {
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

function previous() {
	router.replace(
		`/tournament/${route.params.tourId}/prepare/${
			steps[Math.max(0, stepToIndex(<string>route.params.step) - 1)]
		}/${route.params.competition}`,
	)
}

function next() {
	router.replace(
		`/tournament/${route.params.tourId}/prepare/${
			steps[Math.min(4, stepToIndex(<string>route.params.step) + 1)]
		}/${route.params.competition}`,
	)
}

function tabClick(tab: { paneName: string }) {
	router.replace(
		`/tournament/${route.params.tourId}/prepare/${route.params.step}/${tab.paneName}`,
	)
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

.step {
	width: 70dvw;
	margin-right: 20px;
}
</style>
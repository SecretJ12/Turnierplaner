<template>
	<div id="prepare">
		<el-tabs tab-position="right" id="steps">
			<el-tab-pane label="1. Edit player">
				<el-tabs type="border-card">
					<el-tab-pane
						v-for="competition in competitions"
						:key="competition.id"
						:label="competition.name"
					>
						<ViewEditPlayer :competition="competition" />
					</el-tab-pane>
				</el-tabs>
			</el-tab-pane>
			<el-tab-pane label="2. Select mode">
				<el-tabs type="border-card">
					<el-tab-pane
						v-for="competition in competitions"
						:key="competition.id"
						:label="competition.name"
					>
						<ViewChooseMode :competition="competition" />
					</el-tab-pane>
				</el-tabs>
			</el-tab-pane>
			<el-tab-pane label="3. Assign teams">
				<el-tabs type="border-card">
					<el-tab-pane
						v-for="competition in competitions.filter(
							(c) => c.mode === Mode.DOUBLE && c.signUp === SignUp.INDIVIDUAL,
						)"
						:key="competition.id"
						:label="competition.name"
					>
						<ViewAssignTeams :competition="competition" />
					</el-tab-pane>
				</el-tabs>
			</el-tab-pane>
			<el-tab-pane label="4. Assign matches">
				<el-tabs type="border-card">
					<el-tab-pane
						v-for="competition in competitions"
						:key="competition.id"
						:label="competition.name"
					>
						<ViewAssignMatches :competition="competition" />
					</el-tab-pane>
				</el-tabs>
			</el-tab-pane>
			<el-tab-pane label="5. Schedule matches">
				<el-tabs type="border-card">
					<el-tab-pane
						v-for="competition in competitions"
						:key="competition.id"
						:label="competition.name"
					>
						<ViewScheduleMatches :competition="competition" />
					</el-tab-pane>
				</el-tabs>
			</el-tab-pane>
		</el-tabs>
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
	Mode,
	SignUp,
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
		if (response.status === 200)
			competitions.value = response.data.map(competitionServerToClient)
		else {
			ElMessage.error(t("ViewCompetitions.loadingFailed"))
		}
	})
	.catch((error) => {
		ElMessage.error(t("ViewCompetitions.loadingFailed"))
		console.log(error)
		router.push("/")
	})
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

#steps {
	width: 80dvw;
}
</style>
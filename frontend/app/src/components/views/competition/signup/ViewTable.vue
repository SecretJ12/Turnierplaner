<template>
  <!-- SINGLE -->
  <el-table v-if="props.competition.mode === Mode.SINGLE" :data="playersA"
            :empty-text="t('ViewCompetition.no_registration')" border stripe>
    <el-table-column :label="t('general.name')" prop="name" sortable="custom"/>
  </el-table>
  <!-- DOUBLE TOGETHER -->
  <el-table v-else-if="props.competition.signUp === SignUp.TOGETHER" :data="teams"
            :empty-text="t('ViewCompetition.no_registration')" border stripe>
    <el-table-column :label="t('ViewCompetition.playerA')" prop="playerA.name" sortable="custom"/>
    <el-table-column :label="t('ViewCompetition.playerB')" prop="playerB.name" sortable="custom"/>
  </el-table>
  <!-- DOUBLE INDIVIDUAL SAME -->
  <el-table v-else-if="!props.competition.playerB.different"
            :data="playersA" :empty-text="t('ViewCompetition.no_registration')" border stripe>
    <el-table-column :label="t('general.name')" prop="name" sortable="custom"/>
  </el-table>
  <!-- DOUBLE INDIVIDUAL DIFFERENT -->
  <el-row v-else :gutter="20" justify="space-between">
    <el-col :span="12">
      <el-table :data="playersA" :empty-text="t('ViewCompetition.no_registration')" border stripe>
        <el-table-column :label="t('ViewCompetition.playerA')"
                         prop="name" sortable="custom"/>
      </el-table>
    </el-col>
    <el-col :span="12">
      <el-table :data="playersB" :empty-text="t('ViewCompetition.no_registration')" border stripe>
        <el-table-column :label="t('ViewCompetition.playerB')" prop="name" sortable="custom"/>
      </el-table>
    </el-col>
  </el-row>
</template>

<script lang="ts" setup>
import axios from "axios"
import {ElMessage} from "element-plus"
import {ref, watch} from "vue"
import {useRoute} from "vue-router"
import {Competition, Mode, SignUp} from "@/interfaces/competition"
import {signedUpTeam, Team} from "@/interfaces/registration/team"
import {useI18n} from "vue-i18n"
import {signedUpPlayer} from "@/interfaces/player"

const {t} = useI18n({inheritLocale: true})

const props = defineProps<{
  update: number,
  competition: Competition
}>()


const route = useRoute()
const teams = ref<signedUpTeam[]>([])
const playersA = ref<signedUpPlayer[]>([])
const playersB = ref<signedUpPlayer[]>([])

watch(() => props.update, async () => {
	update()
})
update()

function update() {
	axios.get<Team[]>(`/tournament/${route.params.tourId}/competition/${route.params.compId}/signedUpTeams`)
		.then((response) => {
			if (props.competition.mode === Mode.SINGLE
            || (props.competition.mode === Mode.DOUBLE && props.competition.signUp === SignUp.INDIVIDUAL
                && !props.competition.playerB.different)) {
				playersA.value = response.data.filter((team) => team.playerA !== null).map((team) => {
					const player = team.playerA
					if (player === undefined)
						throw new Error("Player A is null")
					return {
						firstName: player.firstName,
						lastName: player.lastName,
						name: player.firstName + " " + player.lastName
					}
				})
			} else if (props.competition.mode === Mode.DOUBLE && props.competition.signUp === SignUp.INDIVIDUAL
            && props.competition.playerB.different) {
				playersA.value = response.data.filter((team) => team.playerA !== null).map((team) => {
					let player = team.playerA
					if (player === undefined)
						throw new Error("Player A is null")
					return {
						firstName: player.firstName,
						lastName: player.lastName,
						name: player.firstName + " " + player.lastName
					}
				})
				playersB.value = response.data.filter((team) => team.playerB !== null).map((team) => {
					let player = team.playerB
					if (player === undefined)
						throw new Error("Player B is null")
					return {
						firstName: player.firstName,
						lastName: player.lastName,
						name: player.firstName + " " + player.lastName
					}
				})
			} else if (props.competition.mode === Mode.DOUBLE && props.competition.signUp === SignUp.TOGETHER) {
				teams.value = response.data.map((team) => {
					if (team.playerA === undefined)
						throw new Error("Player A is null")
					if (team.playerB === undefined)
						throw new Error("Player B is null")
					return {
						playerA: {
							firstName: team.playerA.firstName,
							lastName: team.playerA.lastName,
							name: team.playerA.firstName + " " + team.playerA.lastName
						},
						playerB: {
							firstName: team.playerB.firstName,
							lastName: team.playerB.lastName,
							name: team.playerB.firstName + " " + team.playerB.lastName
						}
					}
				})
			} else {
				ElMessage.error("invalid mode")
			}
		})
		.catch((error) => {
			teams.value = []
			playersA.value = []
			playersB.value = []
			ElMessage.error(t("ViewCompetition.query_player_failed"))
			console.log(error)
		})
}
</script>

<style scoped>

</style>
<template>
	<!-- SINGLE -->
	<template v-if="props.competition.mode === Mode.SINGLE">
		<!-- Registration player A -->
		<div class="flex flex-column gap-2">
			<ViewConditions
				:begin-game-phase="props.beginGamePhase"
				:player="props.competition.playerA"
			/>
			<div class="flex justify-content-end gap-2">
				<div class="p-inputgroup">
					<AutoComplete
						v-model="playerSearchA"
						class="w-full"
						:placeholder="t('general.name')"
						:suggestions="suggestionsPlayerA"
						optionLabel="value"
						dataKey="id"
						@complete="queryPlayerA"
					/>
					<Button @click="signUpSingle">
						{{ t("general.signUp") }}
					</Button>
				</div>
			</div>
		</div>
	</template>
	<!-- DOUBLE TOGETHER -->
	<template v-else-if="props.competition.signUp === SignUp.TOGETHER">
		<div class="flex flex-column">
			<div class="grid">
				<div class="col-6">
					<ViewConditions
						:begin-game-phase="props.beginGamePhase"
						:player="props.competition.playerA"
					/>
				</div>
				<div class="col-6">
					<ViewConditions
						:begin-game-phase="props.beginGamePhase"
						:player="props.competition.playerB"
					/>
				</div>
			</div>
			<div class="grid">
				<div class="col-6">
					<el-autocomplete
						v-model="playerSearchA"
						:fetch-suggestions="queryPlayerA"
						:placeholder="t('ViewCompetition.playerA')"
						hide-loading
						style="width: 100%"
					/>
				</div>
				<div clasS="col-6">
					<el-autocomplete
						v-model="playerSearchB"
						:fetch-suggestions="queryPlayerB"
						:placeholder="t('ViewCompetition.playerB')"
						hide-loading
						style="width: 100%"
					/>
				</div>
			</div>
			<div class="grid">
				<div class="col-12">
					<el-button class="w-full" @click="signUpDoubleTog">
						{{ t("general.signUp") }}
					</el-button>
				</div>
			</div>
		</div>
	</template>
	<!-- DOUBLE INDIVIDUAL SAME -->
	<template v-else-if="!props.competition.playerB.different">
		<div class="flex flex-column gutter gap-2">
			<!-- Registration player A -->
			<ViewConditions
				:begin-game-phase="props.beginGamePhase"
				:player="props.competition.playerA"
			/>
			<div class="grid">
				<div class="col-8">
					<el-autocomplete
						v-model="playerSearchA"
						:fetch-suggestions="queryPlayerA"
						:placeholder="t('general.name')"
						hide-loading
						style="width: 100%"
					/>
				</div>
				<div class="col-4">
					<el-button style="width: 100%" @click="signUpDoubleIndSame">
						{{ t("general.signUp") }}
					</el-button>
				</div>
			</div>
		</div>
	</template>
	<!-- DOUBLE INDIVIDUAL DIFFERENT -->
	<template v-else>
		<div id="regDoubIndDif">
			<ViewConditions
				id="regDoubIndDifCondA"
				:begin-game-phase="props.beginGamePhase"
				:player="props.competition.playerA"
			/>
			<div id="regDoubIndDifRegA" class="grid">
				<div class="col-8">
					<el-autocomplete
						v-model="playerSearchA"
						:fetch-suggestions="queryPlayerA"
						:placeholder="t('ViewCompetition.playerA')"
						hide-loading
						style="width: 100%"
					/>
				</div>
				<div class="col-4">
					<el-button style="width: 100%" @click="signUpDoubleIndDifA">
						{{ t("general.signUp") }}
					</el-button>
				</div>
			</div>

			<ViewConditions
				id="regDoubIndDifCondB"
				:begin-game-phase="props.beginGamePhase"
				:player="props.competition.playerB"
			/>
			<div id="regDoubIndDifRegB" class="grid">
				<div class="col-8">
					<el-autocomplete
						v-model="playerSearchB"
						:fetch-suggestions="queryPlayerB"
						:placeholder="t('ViewCompetition.playerB')"
						hide-loading
						style="width: 100%"
					/>
				</div>
				<div class="col-4">
					<el-button style="width: 100%" @click="signUpDoubleIndDifB">
						{{ t("general.signUp") }}
					</el-button>
				</div>
			</div>
		</div>
	</template>
</template>

<script lang="ts" setup>
import { ref } from "vue"
import axios from "axios"
import { ElMessage } from "element-plus"
import ViewConditions from "@/components/views/competition/signup/ViewConditions.vue"
import { useRoute } from "vue-router"
import { Competition, Mode, Sex, SignUp } from "@/interfaces/competition"
import { Player, searchedPlayer } from "@/interfaces/player"
import { useI18n } from "vue-i18n"
import { Team } from "@/interfaces/registration/team"
import { AutoCompleteCompleteEvent } from "primevue/autocomplete"

const { t } = useI18n({ inheritLocale: true })

const route = useRoute()

const emit = defineEmits(["registered"])
const props = defineProps<{
	beginGamePhase: Date
	competition: Competition
}>()

interface player {
	id: string,
	firstName: string,
	lastName: string,
	value: string
}

const playerSearchA = ref("")
const suggestionsPlayerA = ref<player[]>([])
const playerSearchB = ref("")
let queriedPlayerB: searchedPlayer[] = []

function queryPlayerA(event: AutoCompleteCompleteEvent) {
	suggestionsPlayerA.value = suggestionsPlayerA.value.filter((item) => {
		return item.value.toLowerCase().includes(event.query.toLowerCase())
	})

	if (
		props.competition.playerA.hasMinAge &&
		props.competition.playerA.minAge === null
	) {
		console.log("Data invalid")
		return
	}
	// TODO put to backend file
	axios
		.get<Player[]>(
			`/player/find?search=${event.query}` +
				(props.competition.playerA.sex !== Sex.ANY
					? `&sex=${props.competition.playerA.sex}`
					: "") +
				(props.competition.playerA.hasMinAge &&
				props.competition.playerA.minAge !== null
					? `&minAge=${props.competition.playerA.minAge
							.toISOString()
							.slice(0, 10)}`
					: "") +
				(props.competition.playerA.hasMaxAge &&
				props.competition.playerA.maxAge !== null
					? `&minAge=${props.competition.playerA.maxAge
							.toISOString()
							.slice(0, 10)}`
					: ""),
		)
		.then((result) => {
			suggestionsPlayerA.value = result.data.map((item) => {
				return {
					id: item.id,
					firstName: item.firstName,
					lastName: item.lastName,
					value: item.firstName + " " + item.lastName,
				}
			})
		})
		.catch((error) => {
			//ElMessage.error(t("ViewCompetition.query_search_failed"))
			// TODO toast
			console.log(error)
		})
}

function queryPlayerB(search: string, callback: (player: Player[]) => void) {
	queriedPlayerB = queriedPlayerB.filter((item) => {
		return item.value.toLowerCase().includes(search.toLowerCase())
	})
	callback(queriedPlayerB)
	axios
		.get<Player[]>(
			`/player/find?search=${search}` +
				(props.competition.playerB.sex !== "ANY"
					? `&sex=${props.competition.playerB.sex}`
					: "") +
				(props.competition.playerB.hasMinAge &&
				props.competition.playerB.minAge !== null
					? `&minAge=${props.competition.playerB.minAge
							.toISOString()
							.slice(0, 10)}`
					: "") +
				(props.competition.playerB.hasMaxAge &&
				props.competition.playerB.maxAge !== null
					? `&minAge=${props.competition.playerB.maxAge
							.toISOString()
							.slice(0, 10)}`
					: ""),
		)
		.then((result) => {
			queriedPlayerB = result.data.map((item) => {
				return {
					id: item.id,
					firstName: item.firstName,
					lastName: item.lastName,
					value: item.firstName + " " + item.lastName,
				}
			})
			callback(queriedPlayerB)
		})
		.catch((error) => {
			ElMessage.error(t("ViewCompetition.query_search_failed"))
			console.log(error)
		})
}

function signUpPlayer(
	queriedPlayer: searchedPlayer[],
	playerSearch: string,
	playerA: boolean,
) {
	const validPlayers = queriedPlayer.filter((p) => {
		return p.value.includes(playerSearch)
	})
	if (validPlayers.length > 1) {
		ElMessage.error(t("Player.too_many_results"))
		return
	}
	if (validPlayers.length === 0) {
		ElMessage.error(t("Player.no_result"))
		return
	}
	const player = validPlayers[0]

	const form: Team = {}
	if (playerA)
		form["playerA"] = {
			firstName: player.firstName,
			lastName: player.lastName,
		}
	else
		form["playerB"] = {
			firstName: player.firstName,
			lastName: player.lastName,
		}

	axios
		.post(
			`/tournament/${route.params.tourId}/competition/${route.params.compId}/signUp`,
			form,
		)
		.then(() => {
			ElMessage.success(t("Player.register_success"))
			emit("registered", "")
		})
		.catch((error) => {
			if (error.response.status === 409)
				ElMessage.error(t("Player.already_exists"))
			else ElMessage.error(t("Player.register_failed"))
		})
}

function signUpSingle() {
	signUpPlayer(queriedPlayerA, playerSearchA.value, true)
}

function signUpDoubleIndSame() {
	signUpPlayer(queriedPlayerA, playerSearchA.value, true)
}

function signUpDoubleIndDifA() {
	signUpPlayer(queriedPlayerA, playerSearchA.value, true)
}

function signUpDoubleIndDifB() {
	signUpPlayer(queriedPlayerB, playerSearchB.value, false)
}

function signUpDoubleTog() {
	const validPlayersA = queriedPlayerA.filter((p) => {
		return p.value.includes(playerSearchA.value)
	})
	if (validPlayersA.length > 1) {
		ElMessage.error(t("Player.too_many_results"))
		return
	}
	if (validPlayersA.length === 0) {
		ElMessage.error(t("Player.no_result"))
		return
	}
	const playerA = validPlayersA[0]

	const validPlayersB = queriedPlayerB.filter((p) => {
		return p.value.includes(playerSearchB.value)
	})
	if (validPlayersB.length > 1) {
		ElMessage.error(t("Player.too_many_results"))
		return
	}
	if (validPlayersB.length === 0) {
		ElMessage.error(t("Player.no_result"))
		return
	}
	const playerB = validPlayersB[0]

	const form = {
		playerA: {
			firstName: playerA.firstName,
			lastName: playerA.lastName,
		},
		playerB: {
			firstName: playerB.firstName,
			lastName: playerB.lastName,
		},
	}

	axios
		.post(
			`/tournament/${route.params.tourId}/competition/${route.params.compId}/signUp`,
			form,
		)
		.then(() => {
			ElMessage.success(t("Player.register_success"))
			emit("registered", "")
		})
		.catch((error) => {
			if (error.response.status === 409)
				ElMessage.error(t("Player.already_exists"))
			else ElMessage.error(t("Player.register_failed"))
		})
}
</script>

<style scoped>
#regDoubIndDif {
	display: grid;
	grid-template-columns: 1fr 1rem 1fr;
	grid-template-rows: auto 0.5rem auto;
}

#regDoubIndDifCondA {
	grid-column: 1;
	grid-row: 1;
}

#regDoubIndDifRegA {
	grid-column: 1;
	grid-row: 3;
}

#regDoubIndDifCondB {
	grid-column: 3;
	grid-row: 1;
}

#regDoubIndDifRegB {
	grid-column: 3;
	grid-row: 3;
}

@media only screen and (max-width: 750px) {
	#regDoubIndDif {
		grid-template-columns: 1fr;
		grid-template-rows: auto auto auto auto;
	}

	#regDoubIndDifCondA {
		grid-column: 1;
		grid-row: 1;
	}

	#regDoubIndDifRegA {
		grid-column: 1;
		grid-row: 2;
		margin-bottom: 10px;
	}

	#regDoubIndDifCondB {
		grid-column: 1;
		grid-row: 3;
	}

	#regDoubIndDifRegB {
		grid-column: 1;
		grid-row: 4;
	}
}
</style>
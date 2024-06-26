import { Team, teamServerToClient } from "@/interfaces/team"
import { Player, playerServerToClient } from "@/interfaces/player"
import { ToastServiceMethods } from "primevue/toastservice"
import { useQuery } from "vue-query/esm"
import axios from "axios"
import { Mode, SignUp } from "@/interfaces/competition"
import { getCompetitionDetails } from "@/backend/competition"
import { RouteLocationNormalizedLoaded } from "vue-router"
import { computed } from "vue"

export function getSignedUp(
	route: RouteLocationNormalizedLoaded,
	t: (s: string) => string,
	toast: ToastServiceMethods,
) {
	const { data: competition } = getCompetitionDetails(route, t, toast, {})
	return useQuery(
		[
			"signedUp",
			computed(() => route.params.tourId),
			computed(() => route.params.compId),
		],
		async () => {
			return axios
				.get<
					Team[]
				>(`/tournament/${route.params.tourId}/competition/${route.params.compId}/signedUpTeams`)
				.then<TeamLists>((response) => {
					const teamLists: TeamLists = {
						playersA: [],
						playersB: [],
						teams: [],
					}
					if (!competition.value) throw new Error()

					if (
						competition.value.mode === Mode.SINGLE ||
						(competition.value.mode === Mode.DOUBLE &&
							competition.value.signUp === SignUp.INDIVIDUAL &&
							!competition.value.playerB.different)
					) {
						teamLists.playersA = response.data
							.filter((team) => team.playerA !== null)
							.map((team) => {
								const player = team.playerA
								if (player === null) throw new Error("Player A is null")
								return playerServerToClient(player)
							})
					} else if (
						competition.value.mode === Mode.DOUBLE &&
						competition.value.signUp === SignUp.INDIVIDUAL &&
						competition.value.playerB.different
					) {
						teamLists.playersA = response.data
							.filter((team) => team.playerA !== null)
							.map((team) => {
								const player = team.playerA
								if (player === null) throw new Error("Player A is null")
								return playerServerToClient(player)
							})
						teamLists.playersB = response.data
							.filter((team) => team.playerB !== null)
							.map((team) => {
								const player = team.playerB
								if (player === null) throw new Error("Player B is null")
								return playerServerToClient(player)
							})
					} else if (
						competition.value.mode === Mode.DOUBLE &&
						competition.value.signUp === SignUp.TOGETHER
					) {
						teamLists.teams = response.data.map((team) => {
							if (team.playerA === undefined)
								throw new Error("Player A is null")
							if (team.playerB === undefined)
								throw new Error("Player B is null")
							return teamServerToClient(team)
						})
					} else {
						toast.add({
							severity: "error",
							summary: t("ViewSignUp.invalidMode"),
							life: 3000,
						})
						throw new Error()
					}

					return teamLists
				})
		},
		{
			onError() {
				toast.add({
					severity: "error",
					summary: t("ViewCompetition.query_player_failed"),
					life: 3000,
				})
			},
			placeholderData: {
				playersA: [],
				playersB: [],
				teams: [],
			},
			enabled: computed(() => !!competition),
		},
	)
}

export function getSignedUpPrepare(
	route: RouteLocationNormalizedLoaded,
	t: (s: string) => string,
	toast: ToastServiceMethods,
) {
	return useQuery(
		[
			"signedUp",
			computed(() => route.params.tourId),
			computed(() => route.params.compId),
		],
		async () => {
			return axios
				.get<
					Team[]
				>(`/tournament/${route.params.tourId}/competition/${route.params.compId}/signedUpTeams`)
				.then<TeamLists>((response) => {
					const teamLists: TeamLists = {
						playersA: [],
						playersB: [],
						teams: [],
					}
					for (const team of response.data) {
						if (team.playerA && team.playerB) {
							teamLists.teams.push(teamServerToClient(team))
						} else if (team.playerA) {
							teamLists.playersA.push(playerServerToClient(team.playerA))
						} else if (team.playerB) {
							teamLists.playersB.push(playerServerToClient(team.playerB))
						}
					}
					return teamLists
				})
		},
		{
			onError() {
				toast.add({
					severity: "error",
					summary: t("ViewCompetition.query_player_failed"),
					life: 3000,
				})
			},
			placeholderData: {
				playersA: [],
				playersB: [],
				teams: [],
			},
		},
	)
}

export interface TeamLists {
	playersA: Player[]
	playersB: Player[]
	teams: Team[]
}

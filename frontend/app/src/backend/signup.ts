import { Team, TeamServer, teamServerToClient } from "@/interfaces/team"
import { Player, playerServerToClient } from "@/interfaces/player"
import { ToastServiceMethods } from "primevue/toastservice"
import { useQuery } from "vue-query/esm"
import axios from "axios"
import { Competition, Mode, SignUp } from "@/interfaces/competition"
import { getCompetitionDetails } from "@/backend/competition"
import { RouteLocationNormalizedLoaded } from "vue-router"
import { computed, Ref } from "vue"

export function getSignedUp(
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
					TeamServer[]
				>(`/tournament/${route.params.tourId}/competition/${route.params.compId}/signedUpTeams`)
				.then<Team[]>((response) => {
					return response.data.map(teamServerToClient)
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
			placeholderData: [],
		},
	)
}

export function getSignedUpSepByComp(
	route: RouteLocationNormalizedLoaded,
	t: (s: string) => string,
	toast: ToastServiceMethods,
) {
	const { data: competition } = getCompetitionDetails(route, t, toast, {})
	const signedUp = getSignedUp(route, t, toast)

	return {
		...signedUp,
		data: computed(() => separateByComp(competition, signedUp.data, t, toast)),
	}
}

export function getSignedUpSeparated(
	route: RouteLocationNormalizedLoaded,
	t: (s: string) => string,
	toast: ToastServiceMethods,
) {
	const signedUp = getSignedUp(route, t, toast)

	return {
		...signedUp,
		data: computed(() => separate(signedUp.data)),
	}
}

function separateByComp(
	competition: Ref<Competition> | Ref<undefined>,
	signedUp: Ref<Team[]> | Ref<undefined>,
	t: (s: string) => string,
	toast: ToastServiceMethods,
) {
	const teamLists: TeamLists = {
		playersA: [],
		playersB: [],
		teams: [],
	}

	if (!competition.value || !signedUp.value) return teamLists

	if (
		competition.value.mode === Mode.SINGLE ||
		(competition.value.mode === Mode.DOUBLE &&
			competition.value.signUp === SignUp.INDIVIDUAL &&
			!competition.value.playerB.different)
	) {
		teamLists.playersA = signedUp.value
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
		teamLists.playersA = signedUp.value
			.filter((team) => team.playerA !== null)
			.map((team) => {
				const player = team.playerA
				if (player === null) throw new Error("Player A is null")
				return playerServerToClient(player)
			})
		teamLists.playersB = signedUp.value
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
		teamLists.teams = signedUp.value.map((team) => {
			if (team.playerA === undefined) throw new Error("Player A is null")
			if (team.playerB === undefined) throw new Error("Player B is null")
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
}

function separate(signedUp: Ref<Team[]> | Ref<undefined>) {
	const teamLists: TeamLists = {
		playersA: [],
		playersB: [],
		teams: [],
	}
	if (!signedUp.value) return teamLists
	for (const team of signedUp.value) {
		if (team.playerA && team.playerB) {
			teamLists.teams.push(teamServerToClient(team))
		} else if (team.playerA) {
			teamLists.playersA.push(playerServerToClient(team.playerA))
		} else if (team.playerB) {
			teamLists.playersB.push(playerServerToClient(team.playerB))
		}
	}
	return teamLists
}

export interface TeamLists {
	playersA: Player[]
	playersB: Player[]
	teams: Team[]
}

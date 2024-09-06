import { computed, Ref } from "vue"
import axios from "axios"
import {
	Tournament,
	TournamentServer,
	tournamentServerToClient,
} from "@/interfaces/tournament"
import { ToastServiceMethods } from "primevue/toastservice"
import { RouteLocationNormalizedLoaded } from "vue-router"
import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query"
import { Court } from "@/interfaces/court"
import { MatchEventServer, matchServerToClient } from "@/interfaces/match"
import { CalEvent } from "@/components/views/prepare/scheduleMatches/ScheduleMatchesHelper"
import { CompType } from "@/interfaces/competition"
import { knockoutTitle } from "@/components/views/competition/knockoutSystem/KnockoutTitleGenerator"

export function getTournamentList(
	isLoggedIn: Ref<boolean>,
	t: (s: string) => string,
	toast: ToastServiceMethods,
) {
	return useQuery({
		queryKey: ["tournamentList", isLoggedIn],
		queryFn: async () => {
			return axios
				.get<TournamentServer[]>("/tournament/list")
				.then<Tournament[]>((response) => {
					return response.data.map(tournamentServerToClient)
				})
				.catch((error) => {
					toast.add({
						severity: "error",
						summary: t("ViewTournaments.loadingFailed"),
						detail: error,
						life: 3000,
					})
					console.log(error)
					throw error
				})
		},
	})
}

export function getTournamentDetails(
	route: RouteLocationNormalizedLoaded,
	t: (s: string) => string,
	toast: ToastServiceMethods,
) {
	return useQuery({
		queryKey: ["tournament", computed(() => route.params.tourId)],
		queryFn: async () => {
			return axios
				.get<TournamentServer>(`/tournament/${route.params.tourId}/details`)
				.then<Tournament>((response) => {
					return tournamentServerToClient(response.data)
				})
				.catch((error) => {
					toast.add({
						severity: "error",
						summary: t("ViewEditTournament.loadingDetailsFailed"),
						detail: error,
						life: 3000,
					})
					console.log(error)
					throw error
				})
		},
	})
}

export function useUpdateTournament(
	t: (s: string) => string,
	toast: ToastServiceMethods,
	handler: {
		suc?: () => void
		err?: () => void
	},
) {
	const queryClient = useQueryClient()
	return useMutation({
		mutationFn: (tournament: TournamentServer) =>
			axios.post("/tournament/update", tournament),
		onSuccess(__, tournament) {
			toast.add({
				severity: "success",
				summary: t("ViewEditTournament.tournamentUpdating"),
				detail: t("ViewEditTournament.tournamentUpdated"),
				life: 3000,
			})
			Promise.all([
				queryClient.invalidateQueries({
					queryKey: ["tournamentList"],
					refetchType: "all",
				}),
				queryClient.invalidateQueries({
					queryKey: ["tournament", tournament.name],
					refetchType: "all",
				}),
			]).then(() => {
				if (handler.suc) handler.suc()
			})
		},
		onError(error) {
			toast.add({
				severity: "error",
				summary: t("ViewEditTournament.tournamentUpdateFailed"),
				detail: error,
				life: 3000,
			})
			if (handler.err) handler.err()
		},
	})
}

export function useAddTournament(
	t: (s: string) => string,
	toast: ToastServiceMethods,
	handler: {
		suc?: () => void
		err?: () => void
	},
) {
	const queryClient = useQueryClient()
	return useMutation({
		mutationFn: (tournament: TournamentServer) =>
			axios.post("/tournament/add", tournament),

		onSuccess() {
			queryClient.invalidateQueries({
				queryKey: ["tournamentList"],
				refetchType: "all",
			})
			toast.add({
				severity: "success",
				summary: t("ViewCreateTournament.tournamentCreating"),
				detail: t("ViewCreateTournament.tournamentCreated"),
				life: 3000,
				closable: false,
			})
			if (handler.suc) handler.suc()
		},
		onError() {
			toast.add({
				severity: "error",
				summary: t("ViewCreateTournament.tournamentCreating"),
				detail: t("ViewCreateTournament.tournamentCreationFailed"),
				life: 3000,
				closable: false,
			})
			if (handler.err) handler.err()
		},
	})
}

export function getTournamentMatchEvents(
	route: RouteLocationNormalizedLoaded,
	t: (_: string) => string,
	from: Ref<Date | undefined>,
	to: Ref<Date | undefined>,
	courts: Ref<Court[]>,
	enabled: Ref<boolean>,
) {
	return useQuery({
		enabled,
		queryKey: [
			"tournamentMatches",
			computed(() => route.params.tourId),
			computed(() => route.params.compId),
			from,
			to,
			computed(() => courts.value.map((court) => court.name)),
		],
		queryFn: () => {
			if (!from.value || !to.value) return []
			return axios
				.get(`/tournament/${route.params.tourId}/matches`, {
					params: {
						from: from.value,
						to: to.value,
						courts:
							courts.value.length > 0
								? courts.value.map((c) => c.name).join(",")
								: undefined,
					},
				})
				.then<MatchEventServer[]>((data) => data.data)
				.then<CalEvent[]>((events) => {
					return events
						.filter((match) => match.compName !== route.params.compId)
						.map((matchServer) => {
							const match = matchServerToClient(matchServer)
							return {
								start: match.begin ?? new Date(),
								end: match.end ?? new Date(),
								split: match.court ?? "undefined court",
								data: {
									title: genTitle(matchServer, t),
									compName: matchServer.compName,
									...match,
								},
							}
						})
				})
		},
	})
}

function genTitle(match: MatchEventServer, t: (_: string) => string) {
	switch (match.type) {
		case CompType.GROUPS:
			return t("ViewGroupSystem.group") + " " + (match.number + 1)
		case CompType.KNOCKOUT:
			if (!match.isFinal) return t("ViewKnockout.thirdPlace")
			else return knockoutTitle(t)(match.number, match.total)
	}
}

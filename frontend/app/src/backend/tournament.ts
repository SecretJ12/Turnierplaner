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
import {
	AnnotatedMatch,
	AnnotatedMatchServer,
	matchServerToClient,
} from "@/interfaces/match"
import { CompType } from "@/interfaces/competition"
import { knockoutTitle } from "@/components/views/competition/knockoutSystem/KnockoutTitleGenerator"
import { v4 as uuidv4 } from "uuid"

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

export function getTournamentMatches(
	route: RouteLocationNormalizedLoaded,
	t: (_: string) => string,
	from: Ref<Date | undefined>,
	to: Ref<Date | undefined>,
	courts: Ref<Court[] | undefined>,
) {
	return useQuery({
		enabled: computed(() => !!from.value && !!to.value && !!courts.value),
		queryKey: [
			"tournamentMatches",
			computed(() => route.params.tourId),
			computed(() => route.params.compId),
			from,
			to,
			computed(() => courts.value?.map((court) => court.name)),
		],
		queryFn: () => {
			if (!from.value || !to.value || !courts.value) return []
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
				.then<AnnotatedMatchServer[]>((data) => data.data)
				.then<AnnotatedMatch[]>((matches) => {
					return matches.map((matchServer) => {
						const match = matchServerToClient(matchServer)
						return {
							compName: matchServer.compName,
							title: {
								type: matchServer.type,
								number: matchServer.number,
								isFinal: matchServer.isFinal,
								total: matchServer.total,
							},
							...match,
						}
					})
				})
		},
	})
}

export function getTournamentMatchEvents(
	route: RouteLocationNormalizedLoaded,
	t: (_: string) => string,
	from: Ref<Date | undefined>,
	to: Ref<Date | undefined>,
	courts: Ref<Court[] | undefined>,
) {
	const matches = getTournamentMatches(route, t, from, to, courts)

	return {
		...matches,
		data: computed(() => {
			if (!matches.data.value) return undefined
			return matches.data.value
				.filter((match) => match.compName !== route.params.compId)
				.map((match) => {
					return {
						id: match.id || uuidv4(),
						start: match.begin ?? new Date(),
						end: match.end ?? new Date(),
						split: match.court ?? "undefined court",
						data: match,
					}
				})
		}),
	}
}

export function genTitle(
	title: AnnotatedMatch["title"],
	t: (_: string) => string,
) {
	switch (title.type) {
		case CompType.GROUPS:
			return t("ViewGroupSystem.group") + " " + (title.number + 1)
		case CompType.KNOCKOUT:
			if (!title.isFinal) return t("ViewKnockout.thirdPlace")
			else return knockoutTitle(t)(title.number, title.total)
	}
}

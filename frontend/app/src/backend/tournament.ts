import { computed, Ref } from "vue"
import axios from "axios"
import {
	Tournament,
	TournamentServer,
	tournamentServerToClient,
} from "@/interfaces/tournament"
import { ToastServiceMethods } from "primevue/toastservice"
import { useQuery } from "vue-query/esm"
import { RouteLocationNormalizedLoaded } from "vue-router"
import { useMutation, useQueryClient } from "vue-query"

export function getTournamentList(
	isLoggedIn: Ref<boolean>,
	t: (s: string) => string,
	toast: ToastServiceMethods,
) {
	return useQuery(
		["tournamentList", isLoggedIn],
		async () => {
			return axios
				.get<TournamentServer[]>("/tournament/list")
				.then<Tournament[]>((response) => {
					return response.data.map(tournamentServerToClient)
				})
		},
		{
			onError(error) {
				toast.add({
					severity: "error",
					summary: t("ViewTournaments.loadingFailed"),
					detail: error,
					life: 3000,
				})
				console.log(error)
			},
		},
	)
}

export function getTournamentDetails(
	route: RouteLocationNormalizedLoaded,
	t: (s: string) => string,
	toast: ToastServiceMethods,
	handler: {
		suc?: () => void
		err?: () => void
	},
) {
	return useQuery(
		["tournament", computed(() => route.params.tourId)],
		async () => {
			return axios
				.get<TournamentServer>(`/tournament/${route.params.tourId}/details`)
				.then<Tournament>((response) => {
					return tournamentServerToClient(response.data)
				})
		},
		{
			onError(error) {
				toast.add({
					severity: "error",
					summary: t("ViewEditTournament.loadingDetailsFailed"),
					detail: error,
					life: 3000,
				})
				console.log(error)
				if (handler.err) handler.err()
			},
			onSuccess: handler.suc,
		},
	)
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
	return useMutation(
		(tournament: TournamentServer) =>
			axios.post("/tournament/update", tournament),
		{
			onSuccess(__, tournament) {
				toast.add({
					severity: "success",
					summary: t("ViewEditTournament.tournamentUpdating"),
					detail: t("ViewEditTournament.tournamentUpdated"),
					life: 3000,
				})
				queryClient.invalidateQueries(["tournamentList", true])
				queryClient.invalidateQueries(["tournamentList", false])
				queryClient.invalidateQueries(["tournament", tournament.name])
				if (handler.suc) handler.suc()
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
		},
	)
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
	return useMutation(
		(tournament: TournamentServer) => axios.post("/tournament/add", tournament),
		{
			onSuccess() {
				toast.add({
					severity: "success",
					summary: t("ViewCreateTournament.tournamentCreating"),
					detail: t("ViewCreateTournament.tournamentCreated"),
					life: 3000,
					closable: false,
				})
				queryClient.invalidateQueries(["tournamentList", true])
				queryClient.invalidateQueries(["tournamentList", false])
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
		},
	)
}

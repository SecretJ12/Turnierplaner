import { ToastServiceMethods } from "primevue/toastservice"
import { Ref } from "vue"
import axios from "axios"
import {
	CompetitionServer,
	competitionServerToClient,
} from "@/interfaces/competition"
import { RouteLocationNormalizedLoaded } from "vue-router"
import { useQuery } from "vue-query/esm"

export function getCompetitionsList(
	route: RouteLocationNormalizedLoaded,
	isLoggedIn: Ref<boolean>,
	t: (s: string) => string,
	toast: ToastServiceMethods,
	handler: {
		suc?: () => void
		err?: () => void
	},
) {
	return useQuery(
		["competitionList", route.params.tourId, isLoggedIn],
		async () => {
			return axios
				.get<
					CompetitionServer[]
				>(`/tournament/${route.params.tourId}/competition/list`)
				.then((response) => {
					return response.data.map(competitionServerToClient)
				})
		},
		{
			onSuccess() {
				if (handler.suc) handler.suc()
			},
			onError(error) {
				toast.add({
					severity: "error",
					summary: t("ViewCompetitions.loadingFailed"),
					detail: error,
					life: 3000,
				})
				console.log(error)
				if (handler.err) handler.err()
			},
		},
	)
}

export function getCompetitionDetails(
	route: RouteLocationNormalizedLoaded,
	t: (s: string) => string,
	toast: ToastServiceMethods,
	handler: {
		suc?: () => void
		err?: () => void
	},
) {
	return useQuery(
		["tournament", route.params.tourId, route.params.compId],
		async () => {
			return axios
				.get<CompetitionServer>(
					`/tournament/${route.params.tourId}/competition/${route.params.compId}/details`,
				)
				.then((response) => {
					return competitionServerToClient(response.data)
				})
		},
		{
			onSuccess() {
				if (handler.suc) handler.suc()
			},
			onError(error) {
				toast.add({
					severity: "error",
					summary: t("ViewEditCompetition.loadingDetailsFailed"),
					detail: error,
					life: 3000,
				})
				console.log(error)
				if (handler.err) handler.err()
			},
		},
	)
}

export function updateCompetition(
	competition: CompetitionServer,
	tourId: string,
	t: (s: string) => string,
	toast: ToastServiceMethods,
	handler: {
		suc?: () => void
		err?: () => void
	},
) {
	axios
		.post(`/tournament/${tourId}/competition/update`, competition)
		.then(() => {
			toast.add({
				severity: "success",
				summary: t("ViewEditCompetition.competitionUpdate"),
				detail: t("ViewEditCompetition.competitionUpdated"),
				life: 3000,
			})
			if (handler.suc) handler.suc()
		})
		.catch((error) => {
			toast.add({
				severity: "error",
				summary: t("ViewEditCompetition.tournamentUpdateFailed"),
				detail: error,
				life: 3000,
			})
			if (handler.err) handler.err()
		})
}

export function addCompetition(
	competition: CompetitionServer,
	tourId: string,
	t: (s: string) => string,
	toast: ToastServiceMethods,
	handler: {
		suc?: () => void
		err?: () => void
	},
) {
	axios
		.post(`/tournament/${tourId}/competition/add`, competition)
		.then(() => {
			toast.add({
				severity: "success",
				summary: t("ViewCreateCompetition.competitionCreation"),
				detail: t("ViewCreateCompetition.competitionCreated"),
				life: 3000,
				closable: false,
			})
			if (handler.suc) handler.suc()
		})
		.catch(() => {
			toast.add({
				severity: "error",
				summary: t("ViewCreateCompetition.competitionCreation"),
				detail: t("ViewCreateCompetition.creationFailed"),
				life: 3000,
				closable: false,
			})
			if (handler.err) handler.err()
		})
}

import { ToastServiceMethods } from "primevue/toastservice"
import { Ref, ref, watch } from "vue"
import axios from "axios"
import {
	Competition,
	CompetitionServer,
	competitionServerToClient,
} from "@/interfaces/competition"

export function getListCompetitions(
	tourId: string,
	isLoggedIn: Ref<boolean>,
	t: (s: string) => string,
	toast: ToastServiceMethods,
	handler: {
		suc?: () => void
		err?: () => void
	},
) {
	const competitions = ref<Competition[] | null>(null)

	watch(isLoggedIn, update)
	update()

	function update() {
		axios
			.get<CompetitionServer[]>(`/tournament/${tourId}/competition/list`)
			.then((response) => {
				competitions.value = response.data.map(competitionServerToClient)
				if (handler.suc) handler.suc()
			})
			.catch((error) => {
				toast.add({
					severity: "error",
					summary: t("ViewCompetitions.loadingFailed"),
					detail: error,
					life: 3000,
				})
				console.log(error)
				if (handler.err) handler.err()
			})
	}

	return competitions
}

export function getCompetitionDetails(
	tourId: string,
	compId: string,
	t: (s: string) => string,
	toast: ToastServiceMethods,
	handler: {
		suc?: () => void
		err?: () => void
	},
) {
	const competition = ref<Competition | null>(null)

	axios
		.get<CompetitionServer>(
			`/tournament/${tourId}/competition/${compId}/details`,
		)
		.then((response) => {
			competition.value = competitionServerToClient(response.data)
			if (handler.suc) handler.suc()
		})
		.catch((error) => {
			toast.add({
				severity: "error",
				summary: t("ViewEditCompetition.loadingDetailsFailed"),
				detail: error,
				life: 3000,
			})
			console.log(error)
			if (handler.err) handler.err()
		})

	return competition
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

import { Ref, ref, UnwrapRef, watch } from "vue"
import axios from "axios"
import {
	Tournament,
	TournamentForm,
	tournamentFormServerToClient,
	TournamentServer,
	tournamentServerToClient,
} from "@/interfaces/tournament"
import { ToastServiceMethods } from "primevue/toastservice"

export function getListTournaments(
	isLoggedIn: Ref<boolean>,
	t: (s: string) => string,
	toast: ToastServiceMethods,
) {
	const tournaments = ref<Tournament[]>([])

	watch(isLoggedIn, update)
	update()

	function update() {
		axios
			.get<TournamentServer[]>("/tournament/list")
			.then((response) => {
				tournaments.value = response.data.map(tournamentServerToClient)
			})
			.catch((error) => {
				toast.add({
					severity: "error",
					summary: t("ViewTournaments.loadingFailed"),
					detail: error,
					life: 3000,
				})
				console.log(error)
			})
	}

	return tournaments
}

function getTournamentDetails_abs<T>(
	tourId: string,
	t: (s: string) => string,
	toast: ToastServiceMethods,
	mapper: (arg0: TournamentServer) => UnwrapRef<T>,
	handler: {
		suc?: () => void
		err?: () => void
	},
) {
	const tournament = ref<T | null>(null)

	axios
		.get<TournamentServer>(`/tournament/${tourId}/details`)
		.then((response) => {
			tournament.value = mapper(response.data)
			if (handler.suc) handler.suc()
		})
		.catch((error) => {
			toast.add({
				severity: "error",
				summary: t("ViewEditTournament.loadingDetailsFailed"),
				detail: error,
				life: 3000,
			})
			console.log(error)
			if (handler.err) handler.err()
		})

	return tournament
}

export function getTournamentFormDetails(
	tourId: string,
	t: (s: string) => string,
	toast: ToastServiceMethods,
	handler: {
		suc?: () => void
		err?: () => void
	},
) {
	return getTournamentDetails_abs<TournamentForm>(
		tourId,
		t,
		toast,
		tournamentFormServerToClient,
		handler,
	)
}

export function getTournamentDetails(
	tourId: string,
	t: (s: string) => string,
	toast: ToastServiceMethods,
	handler: {
		suc?: () => void
		err?: () => void
	},
) {
	return getTournamentDetails_abs<Tournament>(
		tourId,
		t,
		toast,
		tournamentServerToClient,
		handler,
	)
}

export function updateTournament(
	tournament: TournamentServer,
	t: (s: string) => string,
	toast: ToastServiceMethods,
) {
	axios
		.post("/tournament/update", tournament)
		.then(() => {
			toast.add({
				severity: "success",
				summary: t("ViewEditTournament.tournamentUpdating"),
				detail: t("ViewEditTournament.tournamentUpdated"),
				life: 3000,
			})
		})
		.catch((error) => {
			toast.add({
				severity: "error",
				summary: t("ViewEditTournament.tournamentUpdateFailed"),
				detail: error,
				life: 3000,
			})
		})
}

export function addTournament(
	tournament: TournamentServer,
	t: (s: string) => string,
	toast: ToastServiceMethods,
	sucHandler: () => void,
) {
	axios
		.post("/tournament/add", tournament)
		.then(() => {
			toast.add({
				severity: "success",
				summary: t("ViewCreateTournament.tournamentCreating"),
				detail: t("ViewCreateTournament.tournamentCreated"),
				life: 3000,
				closable: false,
			})
			sucHandler()
		})
		.catch(() => {
			toast.add({
				severity: "error",
				summary: t("ViewCreateTournament.tournamentCreating"),
				detail: t("ViewCreateTournament.tournamentCreationFailed"),
				life: 3000,
				closable: false,
			})
		})
}

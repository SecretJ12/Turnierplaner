export interface TournamentForm {
	id?: null | string
	name: string
	visible: boolean
	description: string
	registration_phase: Date[] | undefined
	game_phase: Date[] | undefined
}

export const TournamentFormDefault: TournamentForm = {
	id: null,
	name: "",
	visible: false,
	description: "",
	registration_phase: [new Date(), new Date()],
	game_phase: [new Date(), new Date()],
}

export interface TournamentServer {
	id?: null | string
	name: string
	visible: boolean
	description: string
	beginRegistration: Date
	endRegistration: Date
	beginGamePhase: Date
	endGamePhase: Date
}

export interface Tournament {
	id?: null | string
	name: string
	visible: boolean
	description: string
	registration_phase: {
		begin: Date
		end: Date
	}
	game_phase: {
		begin: Date
		end: Date
	}
}

export function tournamentFormClientToServer(
	tournament: TournamentForm,
): TournamentServer {
	if (tournament.registration_phase === undefined) {
		console.error("Registration phase is null")
		throw new Error("Registration phase is null")
	}
	if (tournament.game_phase === undefined) {
		console.error("Game phase is null")
		throw new Error("Game phase is null")
	}
	return {
		id: tournament.id,
		name: tournament.name,
		visible: tournament.visible,
		description: tournament.description,
		beginRegistration: tournament.registration_phase[0],
		endRegistration: tournament.registration_phase[1],
		beginGamePhase: tournament.game_phase[0],
		endGamePhase: tournament.game_phase[1],
	}
}

export function tournamentFormServerToClient(
	tournament: TournamentServer,
): TournamentForm {
	return {
		id: tournament.id,
		visible: tournament.visible,
		name: tournament.name,
		description: tournament.description,
		registration_phase: [
			new Date(tournament.beginRegistration),
			new Date(tournament.endRegistration),
		],
		game_phase: [
			new Date(tournament.beginGamePhase),
			new Date(tournament.endGamePhase),
		],
	}
}

export function tournamentServerToClient(
	tournament: TournamentServer,
): Tournament {
	return {
		id: tournament.id,
		visible: tournament.visible,
		name: tournament.name,
		description: tournament.description,
		registration_phase: {
			begin: new Date(tournament.beginRegistration),
			end: new Date(tournament.endRegistration),
		},
		game_phase: {
			begin: new Date(tournament.beginGamePhase),
			end: new Date(tournament.endGamePhase),
		},
	}
}

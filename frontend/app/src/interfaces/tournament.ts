export interface Tournament {
    id?: null | string,
    name: string,
    visible: boolean,
    description: string,
    registration_phase: Date[] | null,
    game_phase: Date[] | null
}
export interface TournamentServer {
    id?: null | string,
    name: string,
    visible: boolean
    description: string,
    beginRegistration: Date,
    endRegistration: Date,
    beginGamePhase: Date,
    endGamePhase: Date,
}

export function tournamentClientToServer(tournament: Tournament): TournamentServer {
    if (tournament.registration_phase === null)
        throw new Error();
    if (tournament.game_phase === null)
        throw new Error();
    return {
        id: tournament.id,
        name: tournament.name,
        visible: tournament.visible,
        description: tournament.description,
        beginRegistration: tournament.registration_phase[0],
        endRegistration: tournament.registration_phase[1],
        beginGamePhase: tournament.game_phase[0],
        endGamePhase: tournament.game_phase[1]
    }
}

export function tournamentServerToClient(tournament: TournamentServer): Tournament {
    return {
        id: tournament.id,
        visible: tournament.visible,
        name: tournament.name,
        description: tournament.description,
        registration_phase: [new Date(tournament.beginRegistration), new Date(tournament.endRegistration)],
        game_phase: [new Date(tournament.beginGamePhase), new Date(tournament.endGamePhase)]
    }
}
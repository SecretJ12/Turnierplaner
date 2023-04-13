
export interface Competition {
    id?: null | string,
    name: string,
    visible: boolean,
    description: string,
    registration_phase: Date[],
    game_phase: Date[]
}
export interface CompetitionServer {
    id?: null | string,
    name: string,
    visible: boolean
    description: string,
    beginRegistration: Date,
    endRegistration: Date,
    beginGamePhase: Date,
    endGamePhase: Date,
}

export function competitionClientToServer(tournament: Competition): CompetitionServer {
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

export function competitionServerToClient(tournament: CompetitionServer): Competition {
    return {
        id: tournament.id,
        visible: tournament.visible,
        name: tournament.name,
        description: tournament.description,
        registration_phase: [tournament.beginRegistration, tournament.endRegistration],
        game_phase: [tournament.beginGamePhase, tournament.endGamePhase]
    }
}
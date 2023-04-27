export interface Game {
    scoreA: number,
    scoreB: number
}

export interface Set {
    scoreA: number,
    scoreB: number
}

export interface Match {
    court: string,
    begin: Date,
    end: Date,
    finished: boolean,
    winner: boolean | null,
    teamA: Team | null,
    teamB: Team | null,
    sets?: Array<Set>,
    curGame?: Game
}

export interface MatchServer {
    jUserSetList: Array<Set>;
    court: string,
    begin: string,
    end: string,
    finished: boolean,
    winner: boolean | null,
    teamA: string | null,
    teamB: string | null
    // TOOD add result
}

export interface Team {
    id: string
    playerA: {
        id: string,
        firstName: string,
        lastName: string
    },
    playerB?: {
        id: string,
        firstName: string,
        lastName: string
    }
}

export function matchServerToClient(match: MatchServer, teams: Map<string, Team>): Match {
    let teamA = null
    if (match.teamA !== null) {
        teamA = teams.get(match.teamA)
        if (teamA === undefined) {
            console.error("Team A is undefined")
            throw new Error("Team A is undefined")
        }
    }
    let teamB = null
    if (match.teamB !== null) {
        teamB = teams.get(match.teamB)
        if (teamB === undefined) {
            console.error("Team B is undefined")
            throw new Error("Team B is undefined")
        }
    }

    let setList: Set[] = []
    if (match.jUserSetList !== null){
        setList = match.jUserSetList
    }

    return {
        court: match.court,
        begin: new Date(match.begin),
        end: new Date(match.end),
        finished: match.finished,
        winner: match.winner,
        teamA: teamA,
        teamB: teamB,
        sets: setList
    }
}
import {Match, MatchServer, matchServerToClient, Team} from "@/interfaces/match"

export interface KnockoutSystem {
    teams: Team[],
    finale: KnockoutMatch,
    thirdPlace: KnockoutMatch
}

export interface KnockoutSystemServer {
    teams: Team[],
    finale: KnockoutMatchServer,
    thirdPlace: KnockoutMatchServer
}

export interface KnockoutMatch extends Match {
    prevMatch?: {
        winner: boolean,
        a: KnockoutMatch,
        b: KnockoutMatch
    }
}

export interface KnockoutMatchServer extends MatchServer {
    winningPlayer: boolean,
    previousA: KnockoutMatchServer | null,
    previousB: KnockoutMatchServer | null
}

export function knockoutSystemServerToClient(knockoutSystem: KnockoutSystemServer): KnockoutSystem {
    const teams = new Map<string, Team>()
    knockoutSystem.teams.forEach(team => {
        teams.set(team.id, team)
    })
    return {
        teams: knockoutSystem.teams,
        finale: knockoutMatchServerToClient(knockoutSystem.finale, teams),
        thirdPlace: knockoutMatchServerToClient(knockoutSystem.thirdPlace, teams)
    }
}

function knockoutMatchServerToClient(matchServer: KnockoutMatchServer, teams: Map<string, Team>): KnockoutMatch {
    const match: KnockoutMatch = matchServerToClient(matchServer, teams)
    if (matchServer.previousA !== null && matchServer.previousB !== null) {
        match.prevMatch = {
            winner: matchServer.winningPlayer,
            a: knockoutMatchServerToClient(matchServer.previousA, teams),
            b: knockoutMatchServerToClient(matchServer.previousB, teams)
        }
    }
    return match
}
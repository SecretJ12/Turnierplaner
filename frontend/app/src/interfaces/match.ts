import { Player, PlayerServer, playerServerToClient } from "@/interfaces/player"

export interface Game {
	scoreA: number
	scoreB: number
}

export interface Set {
	index: number
	scoreA: number
	scoreB: number
}

export interface Match {
	court: string
	begin: Date
	end: Date
	finished: boolean
	winner: boolean | null
	teamA: Team | null
	teamB: Team | null
	sets: Array<Set> | null
	curGame: Game | null
}

export interface MatchServer {
	court: string
	begin: string
	end: string
	finished: boolean
	winner: boolean | null
	teamA: string | null
	teamB: string | null
	// TOOD add result
	sets: Array<Set>
}

export interface Team {
	id: string
	playerA: Player | null
	playerB: Player | null
}

export interface TeamServer {
	id: string
	playerA: PlayerServer | null
	playerB: PlayerServer | null
}

export function teamServerToClient(team: TeamServer): Team {
	return {
		id: team.id,
		playerA: team.playerA ? playerServerToClient(team.playerA) : null,
		playerB: team.playerB ? playerServerToClient(team.playerB) : null,
	}
}

export interface TeamArray {
	id: string
	playerA: Player[]
	playerB: Player[]
}

export function teamArrayServerToClient(team: TeamServer) {
	return {
		id: team.id,
		playerA: team.playerA ? [playerServerToClient(team.playerA)] : [],
		playerB: team.playerB ? [playerServerToClient(team.playerB)] : [],
	}
}

export function teamArrayClientToServer(team: TeamArray) {
	return {
		id: team.id,
		playerA:
			team.playerA.length > 0 ? playerServerToClient(team.playerA[0]) : null,
		playerB:
			team.playerB.length > 0 ? playerServerToClient(team.playerB[0]) : null,
	}
}

export function matchServerToClient(
	match: MatchServer,
	teams: Map<string, Team>,
): Match {
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

	let sets: Set[] = []
	if (match.sets !== null) {
		sets = match.sets
	}

	return {
		court: match.court,
		begin: new Date(match.begin),
		end: new Date(match.end),
		finished: match.finished,
		winner: match.winner,
		teamA: teamA,
		teamB: teamB,
		sets: sets,
		curGame: null,
	}
}

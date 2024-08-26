import { Team } from "@/interfaces/team"

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
	id?: string
	court: string | null
	begin: Date | null
	end: Date | null
	finished: boolean
	winner: boolean | null
	teamA: Team | null
	teamB: Team | null
	sets: Array<Set> | null
	curGame: Game | null
}

export interface MatchServer {
	id?: string
	court: string | undefined
	begin: string | undefined
	end: string | undefined
	finished: boolean | undefined
	winner: boolean | undefined
	teamA: string | null
	teamB: string | null
	// TODO add result
	sets: Array<Set>
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
		id: match.id,
		court: match.court ? match.court : null,
		begin: match.begin ? new Date(match.begin) : null,
		end: match.end ? new Date(match.end) : null,
		finished: !!match.finished,
		winner: match.winner ? match.winner : null,
		teamA: teamA,
		teamB: teamB,
		sets: sets,
		curGame: null,
	}
}

export function matchClientToServer(match: Match): MatchServer {
	return {
		id: match.id,
		court: match.court ?? undefined,
		begin: match.begin?.toISOString() ?? undefined,
		end: match.end?.toISOString() ?? undefined,
		finished: match.finished,
		winner: match.winner ?? undefined,
		teamA: match.teamA?.id ?? null,
		teamB: match.teamB?.id ?? null,
		sets: match.sets ?? [],
	}
}

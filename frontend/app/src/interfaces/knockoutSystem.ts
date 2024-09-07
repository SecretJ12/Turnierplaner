import { Match, MatchServer, matchServerToClient } from "@/interfaces/match"
import { teamClientToServer } from "@/interfaces/team"

export interface KnockoutSystem {
	finale: KnockoutMatch | null
	thirdPlace: KnockoutMatch | null
}

export interface KnockoutSystemServer {
	finale: KnockoutMatchServer
	thirdPlace: KnockoutMatchServer
}

export interface KnockoutMatch extends Match {
	prevMatch?: {
		winner: boolean
		a: KnockoutMatch
		b: KnockoutMatch
	}
}

export interface KnockoutMatchServer extends MatchServer {
	winningPlayer: boolean
	previousA: KnockoutMatchServer | undefined
	previousB: KnockoutMatchServer | undefined
}

export function knockoutSystemServerToClient(
	knockoutSystem: KnockoutSystemServer,
): KnockoutSystem {
	return {
		finale: knockoutSystem.finale
			? knockoutMatchServerToClient(knockoutSystem.finale)
			: null,
		thirdPlace: knockoutSystem.thirdPlace
			? knockoutMatchServerToClient(knockoutSystem.thirdPlace)
			: null,
	}
}

function knockoutMatchServerToClient(
	matchServer: KnockoutMatchServer,
): KnockoutMatch {
	const match: KnockoutMatch = matchServerToClient(matchServer)
	if (matchServer.previousA && matchServer.previousB) {
		match.prevMatch = {
			winner: matchServer.winningPlayer,
			a: knockoutMatchServerToClient(matchServer.previousA),
			b: knockoutMatchServerToClient(matchServer.previousB),
		}
	}
	return match
}

export function knockoutMatchClientToServer(
	match: KnockoutMatch,
): KnockoutMatchServer {
	return {
		court: match.court ? match.court : undefined,
		begin: match.begin ? match.begin.toString() : undefined,
		end: match.end ? match.end.toISOString() : undefined,
		finished: match.finished,
		winner: match.winner ? match.winner : undefined,
		teamA: match.teamA === null ? null : teamClientToServer(match.teamA),
		teamB: match.teamB === null ? null : teamClientToServer(match.teamB),
		sets: match.sets ? match.sets : [],

		winningPlayer: match.prevMatch?.winner ?? false,

		previousA: match.prevMatch?.a
			? knockoutMatchClientToServer(match.prevMatch.a)
			: undefined,
		previousB: match.prevMatch?.b
			? knockoutMatchClientToServer(match.prevMatch.b)
			: undefined,
	}
}

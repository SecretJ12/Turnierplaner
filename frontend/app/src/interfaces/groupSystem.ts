import { Match, MatchServer, matchServerToClient } from "@/interfaces/match"
import { KnockoutMatch } from "@/interfaces/knockoutSystem"
import { Team } from "@/interfaces/team"

export interface GroupSystem {
	groups: Group[]
	finale: GroupMatch | null
	thirdPlace: GroupMatch | null
}

export interface Group {
	index: number
	teams: Team[]
	matches: Match[]
}

export interface GroupSystemServer {
	groups: GroupServer[]
	finale: GroupMatchServer
	thirdPlace: GroupMatchServer
}

export interface GroupServer {
	index: number
	matches: MatchServer[]
}

export interface GroupMatchServer extends MatchServer {
	pos?: number
	groupA?: number
	groupB?: number
	winningPlayer?: boolean
	previousA?: GroupMatchServer
	previousB?: GroupMatchServer
}

export interface GroupMatch extends KnockoutMatch {
	prevGroups?: {
		pos: number
		a: number
		b: number
	}
}

export function groupSystemServerToClient(
	groupSystem: GroupSystemServer,
): GroupSystem {
	return {
		groups: groupSystem.groups.map((group) => groupServerToClient(group)),
		finale: groupSystem.finale
			? groupMatchServerToClient(groupSystem.finale)
			: null,
		thirdPlace: groupSystem.thirdPlace
			? groupMatchServerToClient(groupSystem.thirdPlace)
			: null,
	}
}

function uniqueBy<T>(arr: T[], compareFn: (a: T, b: T) => boolean): T[] {
	return arr.filter(
		(item, index, self) =>
			index === self.findIndex((other) => compareFn(item, other)),
	)
}

function groupServerToClient(group: GroupServer): Group {
	const matches = group.matches.map((match) => matchServerToClient(match))
	const teams: Team[] = []
	matches.filter((m) => {
		if (m.teamA) teams.push(m.teamA)
		if (m.teamB) teams.push(m.teamB)
	})
	return {
		index: group.index,
		matches,
		teams: uniqueBy(teams, (t1, t2) => t1.id === t2.id),
	}
}

function groupMatchServerToClient(matchServer: GroupMatchServer): GroupMatch {
	const match: GroupMatch = matchServerToClient(matchServer)
	if (!!matchServer.pos && !!matchServer.groupA && !!matchServer.groupB) {
		if (
			!!matchServer.winningPlayer ||
			!!matchServer.previousA ||
			!!matchServer.previousB
		) {
			console.error("Invalid state")
			throw new Error("Invalid state")
		}
		match.prevGroups = {
			pos: matchServer.pos,
			a: matchServer.groupA,
			b: matchServer.groupB,
		}
	}
	if (
		!!matchServer.winningPlayer &&
		!!matchServer.previousA &&
		!!matchServer.previousB
	) {
		if (!!matchServer.pos || !!matchServer.groupA || !!matchServer.groupB) {
			console.error("Invalid state")
			throw new Error("Invalid state")
		}
		match.prevMatch = {
			winner: matchServer.winningPlayer,
			a: groupMatchServerToClient(matchServer.previousA),
			b: groupMatchServerToClient(matchServer.previousB),
		}
	}
	return match
}

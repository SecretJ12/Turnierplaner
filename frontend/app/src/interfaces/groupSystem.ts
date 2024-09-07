import { Match, MatchServer, matchServerToClient } from "@/interfaces/match"
import { KnockoutMatch } from "@/interfaces/knockoutSystem"

export interface GroupSystem {
	groups: Group[]
	finale: GroupMatch
	thirdPlace: GroupMatch | null
}

export interface Group {
	index: number
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
		finale: groupMatchServerToClient(groupSystem.finale),
		thirdPlace: groupSystem.thirdPlace
			? groupMatchServerToClient(groupSystem.thirdPlace)
			: null,
	}
}

function groupServerToClient(group: GroupServer): Group {
	return {
		index: group.index,
		matches: group.matches.map((match) => matchServerToClient(match)),
	}
}

function groupMatchServerToClient(matchServer: GroupMatchServer): GroupMatch {
	const match: GroupMatch = matchServerToClient(matchServer)
	if (
		matchServer.pos !== undefined &&
		matchServer.groupA !== undefined &&
		matchServer.groupB !== undefined
	) {
		if (
			matchServer.winningPlayer !== undefined ||
			matchServer.previousA !== undefined ||
			matchServer.previousB !== undefined
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
		matchServer.winningPlayer !== undefined &&
		matchServer.previousA !== undefined &&
		matchServer.previousB !== undefined
	) {
		if (
			matchServer.pos !== undefined ||
			matchServer.groupA !== undefined ||
			matchServer.groupB !== undefined
		) {
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

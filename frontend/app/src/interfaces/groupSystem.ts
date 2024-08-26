import { Match, MatchServer, matchServerToClient } from "@/interfaces/match"
import { Team } from "@/interfaces/team"
import { KnockoutMatch } from "@/interfaces/knockoutSystem"

export interface GroupSystem {
	teams: Team[]
	groups: Group[]
	finale: GroupMatch
	thirdPlace: GroupMatch | null
}

export interface Group {
	index: number
	teams: Team[]
	matches: Match[]
}

export interface GroupSystemServer {
	teams: Team[]
	groups: GroupServer[]
	finale: GroupMatchServer
	thirdPlace: GroupMatchServer
}

export interface GroupServer {
	index: number
	teams: string[]
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
	const teams = new Map<string, Team>()
	groupSystem.teams.forEach((team) => {
		if (!team.id) {
			console.error("Team without id:", team)
			return
		}
		teams.set(team.id, team)
	})

	return {
		teams: groupSystem.teams,
		groups: groupSystem.groups.map((group) =>
			groupServerToClient(group, teams),
		),
		finale: groupMatchServerToClient(groupSystem.finale, teams),
		thirdPlace: groupSystem.thirdPlace
			? groupMatchServerToClient(groupSystem.thirdPlace, teams)
			: null,
	}
}

function groupServerToClient(
	group: GroupServer,
	teams: Map<string, Team>,
): Group {
	return {
		index: group.index,
		teams: group.teams.map((team) => {
			const t = teams.get(team)
			if (t === undefined) {
				console.error("Team is undefined")
				throw new Error("Team is undefined")
			}
			return t
		}),
		matches: group.matches.map((match) => matchServerToClient(match, teams)),
	}
}

function groupMatchServerToClient(
	matchServer: GroupMatchServer,
	teams: Map<string, Team>,
): GroupMatch {
	const match: GroupMatch = matchServerToClient(matchServer, teams)
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
			a: groupMatchServerToClient(matchServer.previousA, teams),
			b: groupMatchServerToClient(matchServer.previousB, teams),
		}
	}
	return match
}

import { Match, MatchServer, matchServerToClient } from "@/interfaces/match"
import { Team, TeamServer, teamServerToClient } from "@/interfaces/team"
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
	teams: TeamServer[]
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
	const teams = groupSystem.teams.map((team) => teamServerToClient(team))
	const teamMap = new Map<string, Team>()
	teams.forEach((team) => {
		if (!team.id) {
			console.error("Team without id:", team)
			return
		}
		teamMap.set(team.id, team)
	})

	return {
		teams: teams,
		groups: groupSystem.groups.map((group) =>
			groupServerToClient(group, teamMap),
		),
		finale: groupMatchServerToClient(groupSystem.finale, teamMap),
		thirdPlace: groupSystem.thirdPlace
			? groupMatchServerToClient(groupSystem.thirdPlace, teamMap)
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

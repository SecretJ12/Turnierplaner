import {
	Player,
	playerClientToServer,
	PlayerServer,
	playerServerToClient,
} from "@/interfaces/player"

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

export function teamClientToServer(team: Team): TeamServer {
	return {
		id: team.id,
		playerA: team.playerA ? playerClientToServer(team.playerA) : null,
		playerB: team.playerB ? playerClientToServer(team.playerB) : null,
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

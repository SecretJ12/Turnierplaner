import { Player, signedUpPlayer } from "@/interfaces/player"

// TODO unify with team from match
export interface Team {
	playerA?: Player
	playerB?: Player
}

export interface signedUpTeam {
	playerA: signedUpPlayer
	playerB?: signedUpPlayer
}

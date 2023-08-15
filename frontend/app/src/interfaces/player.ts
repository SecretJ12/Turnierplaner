export interface Player {
	id: string
	firstName: string
	lastName: string
}

export interface searchedPlayer extends Player {
	value: string
}

export interface signedUpPlayer {
	firstName: string
	lastName: string
	name: string
}

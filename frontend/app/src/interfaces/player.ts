export interface PlayerServer {
	id: string
	firstName: string
	lastName: string
}

export interface Player extends PlayerServer {
	name: string
}

export function playerServerToClient(player: PlayerServer): Player {
	return {
		id: player.id,
		firstName: player.firstName,
		lastName: player.lastName,
		get name() {
			return `${this.firstName} ${this.lastName}`
		},
	}
}

export interface searchedPlayer extends Player {
	value: string
}

// TODO get rid of signedUpPlayer
export interface signedUpPlayer {
	firstName: string
	lastName: string
	name: string
}

export enum Sex {
	MALE = "MALE",
	FEMALE = "FEMALE",
}

export interface PlayerRegistration {
	firstName: string
	lastName: string
	sex: Sex
	birthdate: Date
	email: string
	phone: string
}

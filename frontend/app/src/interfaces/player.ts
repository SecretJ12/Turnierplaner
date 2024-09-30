export interface PlayerServer {
	id?: string
	firstName: string
	lastName: string
	sex?: Sex
}

export interface Player extends PlayerServer {
	name: string
}

export function playerServerToClient(player: PlayerServer): Player {
	return {
		id: player.id,
		firstName: player.firstName,
		lastName: player.lastName,
		sex: player.sex,
		get name() {
			return `${this.firstName} ${this.lastName}`
		},
	}
}

export function playerClientToServer(player: Player): PlayerServer {
	return {
		id: player.id,
		firstName: player.firstName,
		lastName: player.lastName,
	}
}

export enum Sex {
	MALE = "MALE",
	FEMALE = "FEMALE",
}

export interface PlayerRegistration {
	firstName: string
	lastName: string
	sex: Sex
	birthday: Date
	email: string
	phone: string
}

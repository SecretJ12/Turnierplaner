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

export interface TeamArray {
	id: string
	playerA: [searchedPlayer]
	playerB: [searchedPlayer]
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

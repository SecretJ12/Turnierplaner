export enum courttype {
	HARD,
	CLAY,
	GRASS,
}

export interface Court {
	name: string
	type: courttype
}

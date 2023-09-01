export enum Sex {
	MALE = "MALE",
	FEMALE = "FEMALE",
	ANY = "ANY",
}

export enum TourType {
	KNOCKOUT = "KNOCKOUT",
	GROUPS = "GROUPS",
}

export enum Mode {
	SINGLE = "SINGLE",
	DOUBLE = "DOUBLE",
}

export enum SignUp {
	INDIVIDUAL = "INDIVIDUAL",
	TOGETHER = "TOGETHER",
}

export interface Competition {
	id?: null | string
	name: string
	description: string
	tourType: TourType
	mode: Mode
	signUp: SignUp
	playerA: settingsPlayer
	playerB: settingsPlayerB
}

export interface CompetitionServer {
	id?: null | string
	name: string
	description: string
	type: TourType
	mode: Mode
	signUp: SignUp
	playerA: {
		sex: Sex
		hasMinAge: boolean
		minAge: string | null
		hasMaxAge: boolean
		maxAge: string | null
	}
	playerB: {
		different: boolean
		sex: Sex
		hasMinAge: boolean
		minAge: string | null
		hasMaxAge: boolean
		maxAge: string | null
	}
}

export interface settingsPlayer {
	sex: Sex
	hasMinAge: boolean
	minAge: Date | null
	hasMaxAge: boolean
	maxAge: Date | null
}

export interface competitionForm {
	name: string
	description: string
	tourType: { name: TourType }
	mode: { name: Mode }
	signUp: { name: SignUp }
	playerA_Sex: { name: Sex }
	playerA_hasMinAge: boolean
	playerA_MinAge: Date | undefined
	playerA_hasMaxAge: boolean
	playerA_MaxAge: Date | undefined
	playerB_Different: boolean
	playerB_Sex: { name: Sex }
	playerB_hasMinAge: boolean
	playerB_MinAge: Date | undefined
	playerB_hasMaxAge: boolean
	playerB_MaxAge: Date | undefined
}

export interface settingsPlayerB extends settingsPlayer {
	different: boolean
}

export function competitionFormToServer(
	form: competitionForm,
	id: string | null,
): CompetitionServer {
	return {
		id: id,
		name: form.name,
		description: form.description,
		type: form.tourType.name,
		mode: form.mode.name,
		signUp: form.signUp.name,
		playerA: {
			sex: form.playerA_Sex.name,
			hasMinAge: form.playerA_hasMinAge,
			minAge: form.playerA_MinAge ? dateToJson(form.playerA_MinAge) : null,
			hasMaxAge: form.playerA_hasMaxAge,
			maxAge: form.playerA_MaxAge ? dateToJson(form.playerA_MaxAge) : null,
		},
		playerB: {
			different: form.playerB_Different,
			sex: form.playerB_Sex.name,
			hasMinAge: form.playerB_hasMinAge,
			minAge: form.playerB_MinAge ? dateToJson(form.playerB_MinAge) : null,
			hasMaxAge: form.playerB_hasMaxAge,
			maxAge: form.playerA_MaxAge ? dateToJson(form.playerA_MaxAge) : null,
		},
	}
}

export function competitionServerToClient(
	competition: CompetitionServer,
): Competition {
	return {
		id: competition.id,
		name: competition.name,
		description: competition.description,
		tourType: competition.type,
		mode: competition.mode,
		signUp: competition.signUp,
		playerA: {
			sex: competition.playerA.sex,
			hasMinAge: competition.playerA.hasMinAge,
			minAge: competition.playerA.minAge
				? new Date(competition.playerA.minAge)
				: null,
			hasMaxAge: competition.playerA.hasMaxAge,
			maxAge: competition.playerA.maxAge
				? new Date(competition.playerA.maxAge)
				: null,
		},
		playerB: {
			different: competition.playerB.different,
			sex: competition.playerB.sex,
			hasMinAge: competition.playerB.hasMinAge,
			minAge: competition.playerB.minAge
				? new Date(competition.playerB.minAge)
				: null,
			hasMaxAge: competition.playerB.hasMaxAge,
			maxAge: competition.playerB.maxAge
				? new Date(competition.playerB.maxAge)
				: null,
		},
	}
}

function dateToJson(d: Date): string {
	return `${d.getFullYear()}-${d.getMonth() < 9 ? "0" : ""}${
		d.getMonth() + 1
	}-${d.getDate() < 10 ? "0" : ""}${d.getDate()}`
}

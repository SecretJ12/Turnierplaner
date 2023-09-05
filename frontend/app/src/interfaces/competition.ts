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

export const CompetitionDefault: Competition = {
	id: null,
	name: "",
	description: "",
	tourType: TourType.KNOCKOUT,
	mode: Mode.SINGLE,
	signUp: SignUp.INDIVIDUAL,
	playerA: {
		sex: Sex.ANY,
		hasMinAge: false,
		minAge: null,
		hasMaxAge: false,
		maxAge: null,
	},
	playerB: {
		different: false,
		sex: Sex.ANY,
		hasMinAge: false,
		minAge: null,
		hasMaxAge: false,
		maxAge: null,
	},
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

export interface CompetitionForm {
	name: string
	description: string
	tourType: TourType
	mode: Mode
	signUp: SignUp
	playerA_Sex: Sex
	playerA_hasMinAge: boolean
	playerA_minAge: Date | undefined
	playerA_hasMaxAge: boolean
	playerA_maxAge: Date | undefined
	playerB_Different: boolean
	playerB_Sex: Sex
	playerB_hasMinAge: boolean
	playerB_minAge: Date | undefined
	playerB_hasMaxAge: boolean
	playerB_maxAge: Date | undefined
}

export interface settingsPlayerB extends settingsPlayer {
	different: boolean
}

export function competitionFormToServer(
	form: CompetitionForm,
	id: string | null,
): CompetitionServer {
	return {
		id: id,
		name: form.name,
		description: form.description,
		type: form.tourType,
		mode: form.mode,
		signUp: form.signUp,
		playerA: {
			sex: form.playerA_Sex,
			hasMinAge: form.playerA_hasMinAge,
			minAge: form.playerA_minAge ? dateToJson(form.playerA_minAge) : null,
			hasMaxAge: form.playerA_hasMaxAge,
			maxAge: form.playerA_maxAge ? dateToJson(form.playerA_maxAge) : null,
		},
		playerB: {
			different: form.playerB_Different,
			sex: form.playerB_Sex,
			hasMinAge: form.playerB_hasMinAge,
			minAge: form.playerB_minAge ? dateToJson(form.playerB_minAge) : null,
			hasMaxAge: form.playerB_hasMaxAge,
			maxAge: form.playerA_maxAge ? dateToJson(form.playerA_maxAge) : null,
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

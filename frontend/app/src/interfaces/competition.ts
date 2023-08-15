export enum Sex {
    MALE = "MALE", FEMALE = "FEMALE", ANY = "ANY"
}

export enum TourType {
    KNOCKOUT = "KNOCKOUT", GROUPS = "GROUPS"
}

export enum Mode {
    SINGLE = "SINGLE", DOUBLE = "DOUBLE"
}

export enum SignUp {
    INDIVIDUAL = "INDIVIDUAL", TOGETHER = "TOGETHER"
}

export interface Competition {
    id?: null | string,
    name: string,
    description: string,
    tourType: TourType,
    mode: Mode,
    signUp: SignUp,
    playerA: settingsPlayer,
    playerB: settingsPlayerB
}

export interface CompetitionServer {
    id?: null | string,
    name: string,
    description: string,
    type: TourType,
    mode: Mode,
    signUp: SignUp,
    playerA: {
        sex: Sex,
        hasMinAge: boolean,
        minAge: string,
        hasMaxAge: boolean,
        maxAge: string
    },
    playerB: {
        different: boolean,
        sex: Sex,
        hasMinAge: boolean,
        minAge: string,
        hasMaxAge: boolean,
        maxAge: string
    }
}

export interface settingsPlayer {
    sex: Sex,
    hasMinAge: boolean,
    minAge: Date | null,
    hasMaxAge: boolean,
    maxAge: Date | null
}

export interface settingsPlayerB extends settingsPlayer {
    different: boolean
}

export function competitionClientToServer(competition: Competition): CompetitionServer {
	if (competition.playerA.minAge === null) {
		console.error("Player A minAge is null")
		throw new Error("Player A minAge is null")
	}
	if (competition.playerA.maxAge === null) {
		console.error("Player A maxAge is null")
		throw new Error("Player A maxAge is null")
	}
	if (competition.playerB.minAge === null) {
		console.error("Player B minAge is null")
		throw new Error("Player B minAge is null")
	}
	if (competition.playerB.maxAge === null) {
		console.error("Player B maxAge is null")
		throw new Error("Player B maxAge is null")
	}
	return {
		id: competition.id,
		name: competition.name,
		description: competition.description,
		type: competition.tourType,
		mode: competition.mode,
		signUp: competition.signUp,
		playerA: {
			sex: competition.playerA.sex,
			hasMinAge: competition.playerA.hasMinAge,
			minAge: dateToJson(competition.playerA.minAge),
			hasMaxAge: competition.playerA.hasMaxAge,
			maxAge: dateToJson(competition.playerA.maxAge)
		},
		playerB: {
			different: competition.playerB.different,
			sex: competition.playerB.sex,
			hasMinAge: competition.playerB.hasMinAge,
			minAge: dateToJson(competition.playerB.minAge),
			hasMaxAge: competition.playerB.hasMaxAge,
			maxAge: dateToJson(competition.playerB.maxAge)
		}
	}
}

export function competitionServerToClient(competition: CompetitionServer): Competition {
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
			minAge: new Date(competition.playerA.minAge),
			hasMaxAge: competition.playerA.hasMaxAge,
			maxAge: new Date(competition.playerA.maxAge)
		},
		playerB: {
			different: competition.playerB.different,
			sex: competition.playerB.sex,
			hasMinAge: competition.playerB.hasMinAge,
			minAge: new Date(competition.playerB.minAge),
			hasMaxAge: competition.playerB.hasMaxAge,
			maxAge: new Date(competition.playerB.maxAge)
		}
	}
}

function dateToJson(d: Date): string {
	return `${d.getFullYear()}-${d.getMonth() < 9 ? "0" : ""}${d.getMonth() + 1}-${d.getDate() < 10 ? "0" : ""}${d.getDate()}`
}
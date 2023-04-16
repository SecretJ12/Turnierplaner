export interface Team {
    playerA?: {
        firstName: string,
        lastName: string
    },
    playerB?: {
        firstName: string,
        lastName: string
    }
}

export interface signedUpTeam {
    playerA: {
        firstName: string,
        lastName: string,
        name: string
    },
    playerB: {
        firstName: string,
        lastName: string,
        name: string
    }
}
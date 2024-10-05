export enum Role {
	USER = "USER",
	REPORTER = "REPORTER",
	DIRECTOR = "DIRECTOR",
	ADMIN = "ADMIN",
}

export interface User {
	id: string
	username: string
	role: Role
}

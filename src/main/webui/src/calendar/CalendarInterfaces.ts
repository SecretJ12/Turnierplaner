export enum View {
	day = "day",
	week = "week",
	month = "month",
	year = "year",
	years = "years",
}

export interface DaySplit {
	id: string
	label: string
	class: string
}

export interface CalEvent<T> {
	id: string
	start: Date
	end: Date
	split: string
	draggable?: boolean
	resizable?: boolean
	deletable?: boolean
	class?: string
	changed?: boolean
	secondary?: boolean
	data: T
}

import { Ref } from "vue"
import { auth } from "@/security/AuthService"
import axios from "axios"
import { useQuery } from "@tanstack/vue-query"

export function getIsDirector(isLoggedIn: Ref<boolean>) {
	return useQuery({
		queryKey: ["can_create", isLoggedIn],
		queryFn: fetchIsDirector,
		placeholderData: false,
		staleTime: 0,
	})
}

async function fetchIsDirector() {
	const user = await auth.getUser()
	if (user === null) return false

	return await axios
		.get<boolean>("/config/isDirector")
		.then((response) => {
			return response.data
		})
		.catch(() => {
			return false
		})
}

export function getCanEdit(tourId: string, isLoggedIn: Ref<boolean>) {
	return useQuery({
		queryKey: ["can_edit", tourId, isLoggedIn],
		queryFn: () => fetchCanEdit(tourId),
		placeholderData: false,
		staleTime: 0,
	})
}

async function fetchCanEdit(tourId: string) {
	const user = await auth.getUser()
	if (user === null) return false

	return await axios
		.get<boolean>(`/tournament/${tourId}/competition/canEdit`)
		.then((response) => {
			return response.data
		})
		.catch(() => {
			return false
		})
}

export function getIsAdmin(isLoggedIn: Ref<boolean>) {
	return useQuery({
		queryKey: ["isAdmin", isLoggedIn],
		queryFn: () => fetchIsAdmin(),
		placeholderData: false,
		staleTime: 0,
	})
}

async function fetchIsAdmin() {
	const user = await auth.getUser()
	if (user === null) return false

	return await axios
		.get<boolean>(`/config/isAdmin`)
		.then((response) => {
			return response.data
		})
		.catch(() => {
			return false
		})
}

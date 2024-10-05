import { Ref } from "vue"
import { auth } from "@/security/AuthService"
import axios from "axios"
import { useQuery } from "@tanstack/vue-query"

export function getCanEdit(tourId: string, isLoggedIn: Ref<boolean>) {
	return useQuery({
		queryKey: ["can_edit", tourId, isLoggedIn],
		queryFn: async () => {
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
		},
		placeholderData: false,
		staleTime: 0,
	})
}

export function getIsAdmin(isLoggedIn: Ref<boolean>) {
	return useQuery({
		queryKey: ["isAdmin", isLoggedIn],
		queryFn: async () => {
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
		},
		placeholderData: false,
		staleTime: 0,
	})
}

export function getIsDirector(isLoggedIn: Ref<boolean>) {
	return useQuery({
		queryKey: ["isDirector", isLoggedIn],
		queryFn: async () => {
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
		},
		placeholderData: false,
		staleTime: 0,
	})
}

export function getIsReporter(isLoggedIn: Ref<boolean>) {
	return useQuery({
		queryKey: ["isReporter", isLoggedIn],
		queryFn: async () => {
			const user = await auth.getUser()
			if (user === null) return false

			return await axios
				.get<boolean>(`/config/isReporter`)
				.then((response) => {
					return response.data
				})
				.catch(() => {
					return false
				})
		},
		placeholderData: false,
		staleTime: 0,
	})
}

import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query"
import axios from "axios"
import { Config } from "@/interfaces/config"
import { Ref } from "vue"

export function getConfig(isLoggedIn: Ref<boolean>) {
	return useQuery({
		queryKey: ["config", isLoggedIn],
		queryFn: async () =>
			axios.get("/config/load").then<Config>((response) => response.data),
	})
}

export function useSaveLanguage(isLoggedIn: Ref<boolean>) {
	return useMutation({
		mutationFn: async (language: string) => {
			if (isLoggedIn)
				return axios.post("/config/save", {
					language: language,
				})
		},
	})
}

export function useSaveDefault(isLoggedIn: Ref<boolean>) {
	const queryClient = useQueryClient()
	return useMutation({
		mutationFn: async (language: string) => {
			if (isLoggedIn)
				return axios.post("/config/saveDefaultLanguage", {
					language,
				})
		},
		onSuccess() {
			queryClient.invalidateQueries({
				queryKey: ["config", isLoggedIn.value],
				refetchType: "all",
			})
		},
	})
}

export function useSaveTitle(isLoggedIn: Ref<boolean>) {
	const queryClient = useQueryClient()
	return useMutation({
		mutationFn: async (title: string) => {
			if (isLoggedIn)
				return axios.post("/config/saveTitle", {
					title,
				})
		},
		onSuccess() {
			queryClient.invalidateQueries({
				queryKey: ["config", isLoggedIn.value],
				refetchType: "all",
			})
		},
	})
}

export function useSaveIsAdminVerificationNeeded(isLoggedIn: Ref<boolean>) {
	const queryClient = useQueryClient()
	return useMutation({
		mutationFn: async (adminVerificationNeeded: boolean) => {
			if (isLoggedIn)
				return axios.post("/config/saveIsAdminNeeded", {
					adminVerificationNeeded,
				})
		},
		onSuccess() {
			queryClient.invalidateQueries({
				queryKey: ["config", isLoggedIn.value],
				refetchType: "all",
			})
		},
	})
}

import axios from "axios"
import { Court } from "@/interfaces/court"
import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query"

export function getCourts() {
	return useQuery({
		queryKey: ["courts"],
		queryFn: () =>
			axios.get(`/court`).then<Court[]>((response) => response.data),
	})
}

export function useCreateCourt() {
	const queryClient = useQueryClient()
	return useMutation({
		mutationFn: (court: Court) => axios.put(`/court`, court),
		onSuccess() {
			queryClient.invalidateQueries({
				queryKey: ["courts"],
				refetchType: "all",
			})
		},
	})
}

export function useUpdateCourt() {
	const queryClient = useQueryClient()
	return useMutation({
		mutationFn: (court: Court) => axios.post(`/court`, court),
		onSuccess() {
			queryClient.invalidateQueries({
				queryKey: ["courts"],
				refetchType: "all",
			})
		},
	})
}

export function useDeleteCourt() {
	const queryClient = useQueryClient()
	return useMutation({
		mutationFn: (court: Court) => axios.delete(`/court/${court.name}`),
		onSuccess() {
			queryClient.invalidateQueries({
				queryKey: ["courts"],
				refetchType: "all",
			})
		},
	})
}

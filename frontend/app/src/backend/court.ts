import axios from "axios"
import { Court } from "@/interfaces/court"
import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query"
import { Tournament } from "@/interfaces/tournament"
import { RouteLocationNormalizedLoaded } from "vue-router"
import { computed, Ref } from "vue"

export function getCourts() {
	return useQuery({
		queryKey: ["courts"],
		queryFn: () =>
			axios.get(`/court`).then<Court[]>((response) => response.data),
	})
}

export function courtComp(a: Court, b: Court) {
	return a.name.localeCompare(b.name)
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

export function getTournamentCourts(route: RouteLocationNormalizedLoaded) {
	return useQuery({
		queryKey: ["tournamentCourts", computed(() => route.params.tourId)],
		queryFn: () =>
			axios
				.get(`/tournament/${route.params.tourId}/courts`)
				.then<Court[]>((response) => response.data)
				.then<Court[]>((data) => data.sort(courtComp)),
	})
}

export function useUpdateTournamentCourts(
	tournament: Ref<Tournament> | Ref<undefined>,
) {
	const queryClient = useQueryClient()
	return useMutation({
		mutationFn: (courts: Court[]) => {
			if (!tournament.value) throw Error("Tournament is null")
			return axios.post(
				`/tournament/${tournament.value.name}/updateCourts`,
				courts,
			)
		},
		onSuccess() {
			if (tournament.value)
				queryClient.invalidateQueries({
					queryKey: ["tournamentCourts", tournament.value.name],
					refetchType: "all",
				})
		},
	})
}

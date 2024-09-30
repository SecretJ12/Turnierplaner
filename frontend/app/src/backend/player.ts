import axios from "axios"
import {
	Player,
	playerClientToServer,
	PlayerServer,
	playerServerToClient,
} from "@/interfaces/player"
import { ToastServiceMethods } from "primevue/toastservice"
import { computed, Ref } from "vue"
import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query"
import { RouteLocationNormalizedLoaded } from "vue-router"

export function findCompPlayers(
	search: Ref<string>,
	route: RouteLocationNormalizedLoaded,
	playerB: boolean,
	t: (s: string) => string,
	toast: ToastServiceMethods,
) {
	return useQuery({
		queryKey: [
			"searchPlayer",
			search,
			computed(() => route.params.tourId),
			computed(() => route.params.compId),
		],
		queryFn: async () => {
			return axios
				.get<PlayerServer[]>(
					`/player/compFind/${route.params.tourId}/${route.params.compId}?search=${search.value.toLowerCase()}` +
						`playerB=${playerB}`,
				)
				.then<Player[]>((result) => result.data.map(playerServerToClient))
				.catch((error) => {
					toast.add({
						severity: "error",
						summary: t("ViewCompetition.query_search_failed"),
						detail: error,
						life: 3000,
					})
					console.log(error)
					throw error
				})
		},
	})
}

export function findPlayers(
	search: Ref<string>,
	page: Ref<number>,
	pageSize: Ref<number>,
	t: (s: string) => string,
	toast: ToastServiceMethods,
) {
	return useQuery({
		queryKey: ["searchPlayer", search],
		queryFn: async () => {
			return axios
				.get<PlayerServer[]>(
					`/player/find?search=${search.value.toLowerCase()}&page=${page.value}&pageSize=${pageSize.value}`,
				)
				.then<Player[]>((result) => result.data.map(playerServerToClient))
				.catch((error) => {
					toast.add({
						severity: "error",
						summary: t("ViewCompetition.query_search_failed"),
						detail: error,
						life: 3000,
					})
					console.log(error)
					throw error
				})
		},
	})
}

export function getUnverified(
	t: (s: string) => string,
	toast: ToastServiceMethods,
) {
	return useQuery({
		queryKey: ["unverifiedPlayer"],
		queryFn: async () => {
			return axios
				.get<PlayerServer[]>(`/player/listUnverified`)
				.then<Player[]>((result) => result.data.map(playerServerToClient))
				.catch((error) => {
					toast.add({
						severity: "error",
						summary: t("ViewCompetition.query_search_failed"),
						detail: error,
						life: 3000,
					})
					console.log(error)
					throw error
				})
		},
	})
}

export function useAdminVerify() {
	const queryClient = useQueryClient()
	return useMutation({
		mutationFn: async (player: Player) =>
			axios.post(`player/adminVerify`, playerClientToServer(player)),
		onSuccess() {
			queryClient.invalidateQueries({
				queryKey: ["unverifiedPlayer"],
			})
		},
	})
}

export function useDeletePlayer() {
	const queryClient = useQueryClient()
	return useMutation({
		mutationFn: async (player: Player) =>
			axios.post(`player/delete`, playerClientToServer(player)),
		onSuccess() {
			queryClient.invalidateQueries({
				queryKey: ["unverifiedPlayer"],
			})
		},
	})
}

export function getPlayer(
	route: RouteLocationNormalizedLoaded,
	t: (s: string) => string,
	toast: ToastServiceMethods,
) {
	return useQuery({
		queryKey: ["playerDetails", computed(() => route.params.playerId)],
		queryFn: async () => {
			return axios
				.get<PlayerServer>(`/player/${route.params.playerId}/details`)
				.then<Player>((result) => playerServerToClient(result.data))
				.catch((error) => {
					toast.add({
						severity: "error",
						summary: t("Player.player_not_found"),
						detail: error,
						life: 3000,
					})
					console.log(error)
					throw error
				})
		},
	})
}

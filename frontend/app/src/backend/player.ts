import { Competition, Sex } from "@/interfaces/competition"
import axios from "axios"
import {
	Player,
	playerClientToServer,
	PlayerServer,
	playerServerToClient,
} from "@/interfaces/player"
import { ToastServiceMethods } from "primevue/toastservice"
import { computed, Ref } from "vue"
import { useMutation, useQuery } from "@tanstack/vue-query"
import { RouteLocationNormalizedLoaded } from "vue-router"

export interface searchPlayer {
	sex: Sex
	hasMinAge: boolean
	minAge: Date | null
	hasMaxAge: boolean
	maxAge: Date | null
}

export function extractSearchPlayer(
	competition: Ref<Competition> | Ref<undefined>,
	playerB: boolean,
): Ref<searchPlayer> {
	return computed(() => {
		if (!competition.value) {
			return {
				sex: Sex.ANY,
				hasMinAge: false,
				minAge: null,
				hasMaxAge: false,
				maxAge: null,
			}
		}

		const player = playerB
			? competition.value.playerB
			: competition.value.playerA
		return {
			sex: player.sex,
			hasMinAge: player.hasMinAge,
			minAge: player.minAge,
			hasMaxAge: player.hasMaxAge,
			maxAge: player.maxAge,
		}
	})
}

export function findPlayers(
	search: Ref<string>,
	searchPlayer: Ref<searchPlayer>,
	t: (s: string) => string,
	toast: ToastServiceMethods,
) {
	return useQuery({
		queryKey: ["searchPlayer", search, searchPlayer],
		queryFn: async () => {
			return axios
				.get<PlayerServer[]>(
					`/player/find?search=${search.value.toLowerCase()}` +
						(searchPlayer.value.sex !== Sex.ANY
							? `&sex=${searchPlayer.value.sex}`
							: "") +
						(searchPlayer.value.hasMinAge && searchPlayer.value.minAge !== null
							? `&minAge=${searchPlayer.value.minAge.toISOString().slice(0, 10)}`
							: "") +
						(searchPlayer.value.hasMaxAge && searchPlayer.value.maxAge !== null
							? `&maxAge=${searchPlayer.value.maxAge.toISOString().slice(0, 10)}`
							: ""),
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
	return useMutation({
		mutationFn: async (player: Player) =>
			axios.post(`player/adminVerify`, playerClientToServer(player)),
	})
}

export function useDeletePlayer() {
	return useMutation({
		mutationFn: async (player: Player) =>
			axios.post(`player/delete`, playerClientToServer(player)),
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

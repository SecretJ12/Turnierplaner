import { Competition, Sex } from "@/interfaces/competition"
import { useQuery } from "vue-query/esm"
import axios from "axios"
import { Player, playerServerToClient } from "@/interfaces/player"
import { ToastServiceMethods } from "primevue/toastservice"
import { computed, Ref } from "vue"

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

export function getPlayer(
	search: Ref<string>,
	searchPlayer: Ref<searchPlayer>,
	t: (s: string) => string,
	toast: ToastServiceMethods,
) {
	return useQuery(
		[search, searchPlayer],
		async () => {
			return axios
				.get<
					Player[]
				>(`/player/find?search=${search.value.toLowerCase()}` + (searchPlayer.value.sex !== Sex.ANY ? `&sex=${searchPlayer.value.sex}` : "") + (searchPlayer.value.hasMinAge && searchPlayer.value.minAge !== null ? `&minAge=${searchPlayer.value.minAge.toISOString().slice(0, 10)}` : "") + (searchPlayer.value.hasMaxAge && searchPlayer.value.maxAge !== null ? `&minAge=${searchPlayer.value.maxAge.toISOString().slice(0, 10)}` : ""))
				.then((result) => {
					return result.data.map(playerServerToClient)
				})
		},
		{
			onError(error) {
				toast.add({
					severity: "error",
					summary: t("ViewCompetition.query_search_failed"),
					detail: error,
					life: 3000,
				})
				console.log(error)
			},
			keepPreviousData: true,
		},
	)
}

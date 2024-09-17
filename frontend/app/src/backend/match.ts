import { useMutation, useQuery } from "@tanstack/vue-query"
import {
	AnnotatedMatch,
	AnnotatedMatchServer,
	annotatedMatchServerToClient,
	Match,
	matchClientToServer,
} from "@/interfaces/match"
import axios from "axios"
import { RouteLocationNormalizedLoaded } from "vue-router"
import { ToastServiceMethods } from "primevue/toastservice"
import { computed, Ref } from "vue"

export function useUpdateMatches(
	route: RouteLocationNormalizedLoaded,
	t: (s: string) => string,
	toast: ToastServiceMethods,
) {
	return useMutation({
		mutationFn: (matches: Match[]) =>
			axios.post(
				`/tournament/${<string>route.params.tourId}/competition/${<string>route.params.compId}/match`,
				matches.map(matchClientToServer),
			),
		onSuccess() {
			toast.add({
				severity: "success",
				summary: t("general.success"),
				detail: t("general.saved"),
				life: 3000,
			})
		},
		onError(error) {
			toast.add({
				severity: "error",
				summary: t("general.failure"),
				detail: t("general.error_saving"),
				life: 3000,
			})
			console.log(error)
		},
	})
}

export function getFilteredMatches(
	route: RouteLocationNormalizedLoaded,
	t: (_: string) => string,
	from: Ref<Date | undefined>,
	to: Ref<Date | undefined>,
) {
	return useQuery({
		enabled: computed(
			() => (!!from.value && !!to.value) || !!route.params.playerId,
		),
		queryKey: [
			"tournamentMatches",
			computed(() => route.params.tourId),
			computed(() => route.params.compId),
			computed(() => route.params.playerId),
			from,
			to,
		],
		queryFn: () => {
			if (!from.value || !to.value) return []
			return axios
				.get(`matches`, {
					params: {
						tour: route.params.tourId,
						comp: route.params.compId,
						player: route.params.playerId,
						from: from.value,
						to: to.value,
					},
				})
				.then<AnnotatedMatchServer[]>((data) => data.data)
				.then<AnnotatedMatch[]>((matches) => {
					return matches.map(annotatedMatchServerToClient)
				})
		},
	})
}

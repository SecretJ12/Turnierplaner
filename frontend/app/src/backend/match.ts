import { useMutation } from "@tanstack/vue-query"
import { Match, matchClientToServer } from "@/interfaces/match"
import axios from "axios"
import { RouteLocationNormalizedLoaded } from "vue-router"
import { ToastServiceMethods } from "primevue/toastservice"

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
			// TODO internalization
			toast.add({
				severity: "success",
				summary: "Scheduling saved",
				detail: "saved all the matches",
				life: 3000,
			})
		},
		onError(error) {
			// TODO internalization
			toast.add({
				severity: "error",
				summary: "Saving failed",
				detail: "error saving matches",
				life: 3000,
			})
			console.log(error)
		},
	})
}

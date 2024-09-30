import { useMutation, useQueryClient } from "@tanstack/vue-query"
import { Set } from "@/interfaces/match"
import axios from "axios"
import { RouteLocationNormalizedLoaded } from "vue-router"
import { ToastServiceMethods } from "primevue/toastservice"

export function useUpdateSet(
	route: RouteLocationNormalizedLoaded,
	t: (s: string) => string,
	toast: ToastServiceMethods,
) {
	const queryClient = useQueryClient()
	return useMutation({
		mutationFn: (data: { sets: Set[]; matchId: string }) =>
			axios
				.post(
					`/tournament/${<string>route.params.tourId}/competition/${data.matchId}/set`,
					data.sets,
				)
				.then(() => {
					queryClient.invalidateQueries({
						queryKey: ["knockout", route.params.tourId, route.params.compId],
						refetchType: "all",
					})
				})
				.then(() => {
					queryClient.invalidateQueries({
						queryKey: ["group", route.params.tourId, route.params.compId],
						refetchType: "all",
					})
				}),
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

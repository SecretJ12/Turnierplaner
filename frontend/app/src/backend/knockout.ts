import { RouteLocationNormalizedLoaded } from "vue-router"
import { computed } from "vue"
import axios from "axios"
import {
	KnockoutMatch,
	knockoutMatchClientToServer,
	KnockoutSystemServer,
	knockoutSystemServerToClient,
} from "@/interfaces/knockoutSystem"
import { ToastServiceMethods } from "primevue/toastservice"
import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query"

export function getKnockout(route: RouteLocationNormalizedLoaded) {
	return useQuery({
		queryKey: [
			"knockout",
			computed(() => route.params.tourId),
			computed(() => route.params.compId),
		],
		queryFn: async () =>
			axios
				.get<KnockoutSystemServer>(
					`tournament/${route.params.tourId}/competition/${route.params.compId}/knockoutMatches`,
				)
				.then((response) => knockoutSystemServerToClient(response.data)),
	})
}

export function useInitKnockout(
	route: RouteLocationNormalizedLoaded,
	t: (s: string) => string,
	toast: ToastServiceMethods,
) {
	const queryClient = useQueryClient()
	return useMutation({
		mutationFn: async (tree: KnockoutMatch) =>
			axios
				.post<boolean>(
					`/tournament/${route.params.tourId}/competition/${route.params.compId}/initKnockout`,
					knockoutMatchClientToServer(tree),
				)
				.then(() => {
					queryClient.invalidateQueries({
						queryKey: ["knockout", route.params.tourId, route.params.compId],
						refetchType: "all",
					})
					toast.add({
						severity: "success",
						summary: t("general.success"),
						detail: t("general.saved"),
						life: 3000,
						closable: false,
					})
				})
				.catch(() => {
					toast.add({
						severity: "error",
						summary: t("general.failure"),
						detail: t("general.save_failed"),
						life: 3000,
						closable: false,
					})
				}),
	})
}

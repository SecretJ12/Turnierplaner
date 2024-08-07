import { RouteLocationNormalizedLoaded } from "vue-router"
import { useQuery } from "vue-query/esm"
import { computed } from "vue"
import axios from "axios"
import {
	KnockoutMatch,
	knockoutMatchClientToServer,
	KnockoutSystemServer,
	knockoutSystemServerToClient,
} from "@/interfaces/knockoutSystem"
import { ToastServiceMethods } from "primevue/toastservice"
import { useMutation, useQueryClient } from "vue-query"

export function getKnockout(route: RouteLocationNormalizedLoaded) {
	return useQuery(
		[
			"knockout",
			computed(() => route.params.tourId),
			computed(() => route.params.compId),
		],
		async () =>
			axios
				.get<KnockoutSystemServer>(
					`tournament/${route.params.tourId}/competition/${route.params.compId}/knockoutMatches`,
				)
				.then((response) => knockoutSystemServerToClient(response.data)),
	)
}

export function useInitKnockout(
	route: RouteLocationNormalizedLoaded,
	t: (s: string) => string,
	toast: ToastServiceMethods,
) {
	const queryClient = useQueryClient()
	return useMutation(async (tree: KnockoutMatch) => {
		return axios
			.post<boolean>(
				`/tournament/${route.params.tourId}/competition/${route.params.compId}/initKnockout`,
				knockoutMatchClientToServer(tree),
			)
			.then(() => {
				queryClient.invalidateQueries([
					"signedUp",
					route.params.tourId,
					route.params.compId,
				])
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
			})
	})
}

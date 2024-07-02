import { RouteLocationNormalizedLoaded } from "vue-router"
import { useQuery } from "vue-query/esm"
import { computed } from "vue"
import axios from "axios"
import {
	KnockoutSystemServer,
	knockoutSystemServerToClient,
} from "@/interfaces/knockoutSystem"

export function getKnockout(route: RouteLocationNormalizedLoaded) {
	return useQuery(
		[
			"group",
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

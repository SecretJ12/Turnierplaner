import { RouteLocationNormalizedLoaded } from "vue-router"
import { useQuery } from "vue-query/esm"
import { computed } from "vue"
import axios from "axios"
import {
	GroupSystemServer,
	groupSystemServerToClient,
} from "@/interfaces/groupSystem"

export function getGroup(route: RouteLocationNormalizedLoaded) {
	return useQuery(
		[
			"group",
			computed(() => route.params.tourId),
			computed(() => route.params.compId),
		],
		async () =>
			axios
				.get<GroupSystemServer>(
					`tournament/${route.params.tourId}/competition/${route.params.compId}/groupMatches`,
				)
				.then((response) => groupSystemServerToClient(response.data)),
	)
}

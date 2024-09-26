import { useQuery } from "@tanstack/vue-query"
import axios from "axios"
import { Config } from "@/interfaces/config"

export function getConfig() {
	return useQuery({
		queryKey: ["config", "default"],
		queryFn: async () =>
			axios.get("/config/default").then<Config>((response) => response.data),
		staleTime: 0,
	})
}

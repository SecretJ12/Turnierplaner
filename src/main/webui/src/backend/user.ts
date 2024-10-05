import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query"
import axios from "axios"
import { User } from "@/interfaces/user"

export function getUsers() {
	return useQuery({
		queryKey: ["users"],
		queryFn: async () =>
			axios.get<User[]>(`/user/list`).then<User[]>((result) => result.data),
	})
}

export function useUpdateUser() {
	const queryClient = useQueryClient()
	return useMutation({
		mutationFn: (user: User) => axios.post(`/user/updateUser`, user),
		onSuccess() {
			queryClient.invalidateQueries({
				queryKey: ["users"],
			})
		},
	})
}

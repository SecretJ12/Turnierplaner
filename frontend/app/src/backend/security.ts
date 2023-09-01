import { Ref, ref, watch } from "vue"
import { auth } from "@/security/AuthService"
import axios from "axios"

export function getCanCreate(isLoggedIn: Ref<boolean>) {
	const canCreate = ref<boolean>(false)

	watch(isLoggedIn, update)
	update()

	function update() {
		auth.getUser().then((user) => {
			if (user !== null) {
				axios
					.get<boolean>("/tournament/canCreate")
					.then((response) => {
						canCreate.value = response.data
					})
					.catch(() => {
						canCreate.value = false
					})
			}
		})
	}

	return canCreate
}

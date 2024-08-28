<template>
	<div id="headerRight">
		<Dropdown v-model="$i18n.locale" :options="$i18n.availableLocales" />

		<span
			v-if="!isLoggedIn"
			class="material-icons clickable bigger"
			@click="login"
			>login</span
		>
		<template v-else>
			<span v-if="windowWidth > 600">{{ currentUser }}</span>
			<span class="material-icons clickable bigger" @click="logout"
				>logout</span
			>
		</template>
	</div>
</template>

<script lang="ts" setup>
import { auth } from "@/security/AuthService"
import { inject, ref, watch } from "vue"

const currentUser = ref<string>("")
const isLoggedIn = inject("loggedIn", ref(false))

let windowWidth = ref(window.innerWidth)
window.addEventListener("resize", () => {
	windowWidth.value = window.innerWidth
})

watch(isLoggedIn, async () => {
	auth.getUser().then((user) => {
		if (user !== null && user.profile.preferred_username) {
			currentUser.value = user.profile.preferred_username
		}
	})
})

function login() {
	auth.login()
}

function logout() {
	auth.logout()
}
</script>

<script lang="ts">
export default {
	name: "LocaleChanger",
	data() {
		return { langs: ["en", "en"] }
	},
}
</script>

<style>
#headerRight {
	width: fit-content;
	padding-right: 10px;
	display: flex;
	align-items: center;
	align-content: flex-end;
	flex-direction: row;
	justify-content: flex-end;
}

#login > span {
	text-align: center;
	margin-right: 20px;
	display: flex;
	justify-content: center;
}

@media only screen and (max-width: 500px) {
	#headerRight {
		padding-right: 0;
	}

	#headerRight > * {
		margin-right: 10px;
	}
}

#headerRight > * {
	margin-right: 20px;
	flex-grow: 0;
	flex-shrink: 1;
}

.clickable {
	cursor: pointer;
}

.bigger {
	font-size: 2.5rem !important;
	font-weight: bold !important;
}
</style>

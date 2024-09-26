<template>
	<div id="headerRight">
		<Button v-if="isLoggedIn" rounded outlined size="small" @click="settings">
			<template #icon>
				<span class="material-symbols-outlined">settings</span>
			</template>
		</Button>
		<Dropdown v-model="locale" :options="availableLocales" />

		<span
			v-if="!isLoggedIn"
			class="material-symbols-outlined clickable bigger"
			@click="login"
			>login</span
		>
		<template v-else>
			<span v-if="windowWidth > 600">{{ currentUser }}</span>
			<span class="material-symbols-outlined clickable bigger" @click="logout"
				>logout</span
			>
		</template>
	</div>
</template>

<script lang="ts" setup>
import { auth } from "@/security/AuthService"
import { inject, ref, watch } from "vue"
import { router } from "@/main"
import { useI18n } from "vue-i18n"

const currentUser = ref<string>("")
const isLoggedIn = inject("loggedIn", ref(false))
const { locale, availableLocales } = useI18n()

function settings() {
	router.push({
		name: "Settings",
	})
}

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

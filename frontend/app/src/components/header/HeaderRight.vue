<template>
	<div class="pr-3 flex flex-row align-items-center gap-2 lg:gap-3">
		<Button v-if="isLoggedIn" rounded outlined size="small" @click="settings">
			<template #icon>
				<span class="material-symbols-outlined">settings</span>
			</template>
		</Button>
		<Dropdown
			v-model="locale"
			:options="availableLocales"
			@change="(event) => saveLanguage(event.value)"
		/>

		<span
			v-if="!isLoggedIn"
			class="material-symbols-outlined cursor-pointer bigger"
			@click="login"
		>
			login
		</span>
		<div
			v-else
			class="flex flex-column-reverse lg:flex-row align-items-center lg:gap-3"
		>
			<span>{{ currentUser }}</span>
			<span
				class="material-symbols-outlined cursor-pointer bigger"
				@click="logout"
			>
				logout
			</span>
		</div>
	</div>
</template>

<script lang="ts" setup>
import { auth } from "@/security/AuthService"
import { inject, ref, watch } from "vue"
import { router } from "@/main"
import { useI18n } from "vue-i18n"
import { useSaveLanguage } from "@/backend/config"

const currentUser = ref<string>("")
const isLoggedIn = inject("loggedIn", ref(false))
const { locale, availableLocales } = useI18n()

const { mutate: saveLanguage } = useSaveLanguage(isLoggedIn)

function settings() {
	router.push({
		name: "Settings",
	})
}

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
.bigger {
	font-size: 2.5rem !important;
	font-weight: bold !important;
}
</style>

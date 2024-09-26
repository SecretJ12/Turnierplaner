<script lang="ts" setup>
import HeadContent from "@/components/header/HeadContent.vue"
import { provide, ref, watch } from "vue"
import { access_token, auth } from "@/security/AuthService"
import { getConfig } from "@/backend/config"
import { useI18n } from "vue-i18n"

let aside = false

const silentLoginCompleted = ref(false)
auth
	.silentLogin()
	.then(() => {
		silentLoginCompleted.value = true
	})
	.catch(() => {
		silentLoginCompleted.value = true
		console.log("Error logging in")
	})

const loggedIn = ref(false)
provide("loggedIn", loggedIn)
auth.addUserLoadedListener(() => {
	auth.getUser().then((user) => {
		if (user !== null) {
			access_token.value = user.access_token
			loggedIn.value = true
		} else {
			access_token.value = null
		}
	})
})
auth.addUserUnloadedListener(() => {
	access_token.value = null
	loggedIn.value = false
})

const { data: config } = getConfig(loggedIn)
const { locale } = useI18n()

watch(
	config,
	async () => {
		console.log("update settings")
		if (config.value) {
			console.log("update language: ", config.value.language)
			locale.value = config.value.language
		}
	},
	{ immediate: true },
)
</script>

<template>
	<HeadContent />

	<div v-if="silentLoginCompleted" id="body">
		<router-view />
		<aside v-if="aside">
			<h2>Aside content</h2>
		</aside>
	</div>
	<Toast />
</template>

<style>
body {
	margin: 0;
	padding: 0 !important;
}
</style>

<style scoped>
#body {
	margin-top: 20px;
	display: flex;
}

main {
	flex-grow: 1;
}

aside {
	height: 100px;
	width: 400px;
	background-color: blue;
}
</style>

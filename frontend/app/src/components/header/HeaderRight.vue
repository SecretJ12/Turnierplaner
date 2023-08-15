<template>
	<div id="headerRight">
		<el-select
			id="language-select"
			v-model="$i18n.locale"
			fit-input-width
			placeholder="ab"
		>
			<el-option
				v-for="locale in $i18n.availableLocales"
				:key="locale"
				:label="locale.toLocaleString()"
				:value="locale"
			/>
		</el-select>
		<font-awesome-icon
			v-if="!isLoggedIn"
			id="colorIcon"
			:icon="['fas', 'right-to-bracket']"
			class="fa-2x clickable"
			@click="login"
		/>

		<span v-if="isLoggedIn && windowWidth > 600">{{ currentUser }}</span>
		<font-awesome-icon
			v-if="isLoggedIn"
			id="colorIcon"
			:icon="['fas', 'right-from-bracket']"
			class="fa-2x clickable"
			@click="logout"
		/>
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
	name: "locale-changer",
	data() {
		return { langs: ["en", "en"] }
	},
}
</script>

<style>
#language-select {
	width: 20px;
	margin-right: 0;
}

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

#colorIcon {
	color: #044154;
}

.clickable {
	cursor: pointer;
}
</style>

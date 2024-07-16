<template>
	<div id="headerLeft" @click="home">
		<span class="material-icons" style="font-size: 3rem">sports_tennis</span>
		<h1 id="colorHeadLine">{{ t("title") }}</h1>
	</div>
</template>

<script lang="ts" setup>
import { router } from "@/main"
import { useI18n } from "vue-i18n"

const { t } = useI18n({ inheritLocale: true })

function home() {
	router.push({ name: "Tournaments" })
}
</script>

<style scoped>
#headerLeft {
	padding-left: 10px;
	display: flex;
	flex-direction: row;
	align-items: center;
	cursor: pointer;
}

#colorHeadLine {
	color: #044154;
}

#headerLeft > * {
	margin-left: 20px;
}

h1 {
	font-weight: 900;
}

@media only screen and (max-width: 750px) {
	h1 {
		font-size: 1em;
	}
}
</style>

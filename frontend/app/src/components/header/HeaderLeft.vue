<template>
	<div id="header" class="flex flex-row pl-3 align-items-center gap-3">
		<span
			class="material-symbols-outlined cursor-pointer"
			style="font-size: 3rem"
			@click="toHome"
			>sports_tennis</span
		>
		<div class="flex flex-row align-items-baseline gap-3">
			<h1 id="colorHeadLine" class="m-0 cursor-pointer title" @click="toHome">
				{{ config?.name ? t(config.name) : t("title") }}
			</h1>
			<h2 class="m-0 cursor-pointer" @click="toTournament">
				{{ route.params.tourId }}
			</h2>
			<h3 class="m-0 cursor-pointer" @click="toCompetition">
				{{ route.params.compId }}
			</h3>
		</div>
	</div>
</template>

<script lang="ts" setup>
import { router } from "@/main"
import { useI18n } from "vue-i18n"
import { useRoute } from "vue-router"
import { getConfig } from "@/backend/config"

const route = useRoute()
const { t } = useI18n()

const { data: config } = getConfig()

function toHome() {
	router.push({ name: "Tournaments" })
}

function toTournament() {
	router.push({
		name: "Competitions",
	})
}

function toCompetition() {
	router.push({
		name: "Competition",
	})
}
</script>

<style scoped>
#colorHeadLine {
	color: #044154;
}

h1 {
	font-weight: 900;
}

h1,
h2,
h3 {
	line-height: 1;
}

@media only screen and (max-width: 750px) {
	h1 {
		font-size: 1em;
	}
}

span:hover,
h1:hover,
h2:hover,
h3:hover {
	filter: drop-shadow(0 0 6px #808080);
}
</style>

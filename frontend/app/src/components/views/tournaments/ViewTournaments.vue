<template>
	<div id="tournaments">
		<template v-if="tournaments === null">
			<Skeleton v-for="i in Array(5)" :key="i" class="w-23rem h-12rem" />
		</template>
		<item
			v-for="tournament in tournaments"
			v-else
			:key="tournament.name"
			:can-create="canCreate"
			:tournament="tournament"
			@selected="selected"
			@settings="settingsItem"
		/>
		<AddItem
			v-if="canCreate"
			:title="t('ViewTournaments.add_title')"
			:content="t('ViewTournaments.add_content')"
			@selected="addTournament"
		/>
	</div>
</template>

<script lang="ts" setup>
import { inject, ref } from "vue"
import Item from "../../items/ItemTournament.vue"
import AddItem from "../../items/ItemAdd.vue"
import { router } from "@/main"
import { useI18n } from "vue-i18n"
import { getCanCreate } from "@/backend/security"
import { getListTournaments } from "@/backend/tournament"
import { useToast } from "primevue/usetoast"

const { t } = useI18n({ inheritLocale: true })
const toast = useToast()

const isLoggedIn = inject("loggedIn", ref(false))
const canCreate = getCanCreate(isLoggedIn)
const tournaments = getListTournaments(isLoggedIn, t, toast)

function selected(tournament: string) {
	router.push({
		name: "Competitions",
		params: { tourId: tournament },
	})
}

function settingsItem(tournament: string) {
	router.push({
		name: "Edit tournament",
		params: { tourId: tournament },
	})
}

function addTournament() {
	router.push({ name: "Create tournament" })
}
</script>

<style scoped>
#tournaments {
	width: calc(100% - 20px);
	margin: 10px;
	display: flex;
	flex-wrap: wrap;
	flex-direction: row;
	justify-content: center;
}

#tournaments > * {
	margin: 0 10px 20px 10px;
}
</style>

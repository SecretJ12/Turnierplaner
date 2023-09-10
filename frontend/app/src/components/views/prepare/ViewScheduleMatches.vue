<template>
	Schedule matches: {{ competition?.name }}

	<div class="grid grid-nogutter justify-content-between mt-4">
		<Button label="Back" icon="pi pi-angle-left" @click="prevPage" />
		<!-- TODO add @click -->
		<Button
			v-if="route.params.step === 'editPlayers'"
			:label="t('general.save')"
		>
		</Button>
		<Button
			v-else
			label="Complete"
			icon="pi pi-check"
			icon-pos="right"
			class="p-button-success"
			@click="complete"
		/>
	</div>
</template>

<script setup lang="ts">
import { getCompetitionDetails } from "@/backend/competition"
import { useRoute, useRouter } from "vue-router"
import { useToast } from "primevue/usetoast"
import { useI18n } from "vue-i18n"

const route = useRoute()
const router = useRouter()
const toast = useToast()
const { t } = useI18n({ inheritLocale: true })

const competition = getCompetitionDetails(route, t, toast, {
	suc: () => {
		if (competition.value === null) return
	},
})

function prevPage() {
	router.replace({
		name: "assignMatches",
		params: { tourId: route.params.tourId, compId: route.params.compId },
	})
}

function complete() {
	// TODO
	toast.add({
		severity: "success",
		summary: "Competition updated",
		detail: "TODO",
		life: 3000
	})
}
</script>

<style scoped></style>
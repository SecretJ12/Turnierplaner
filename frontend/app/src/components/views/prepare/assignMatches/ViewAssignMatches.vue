<template>
	<div class="flex flex-column gap-1">
		<Transition v-if="competition">
			<Groups v-if="competition.tourType === TourType.GROUPS" ref="groupsRef" />
			<Knockout v-else ref="knockoutRef" />
		</Transition>

		<div class="grid grid-nogutter justify-content-between mt-4">
			<Button
				:label="t('general.back')"
				icon="pi pi-angle-left"
				@click="prevPage"
			/>
			<Button
				:label="t('general.save')"
				severity="success"
				:disabled="groupsRef?.disabled"
				@click="save"
			></Button>
			<Button
				v-if="route.params.step !== 'scheduleMatches'"
				:label="t('general.next')"
				icon="pi pi-angle-right"
				icon-pos="right"
				@click="nextPage"
			/>
		</div>
	</div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from "vue-router"
import { useI18n } from "vue-i18n"
import { useToast } from "primevue/usetoast"
import { TourType } from "@/interfaces/competition"
import Groups from "@/components/views/prepare/assignMatches/AssignMatchesGroups.vue"
import AssignMatchesGroups from "@/components/views/prepare/assignMatches/AssignMatchesGroups.vue"
import Knockout from "@/components/views/prepare/assignMatches/AssignMatchesKnockout.vue"
import AssignMatchesKnockout from "@/components/views/prepare/assignMatches/AssignMatchesKnockout.vue"
import { ref } from "vue"
import { getCompetitionDetails } from "@/backend/competition"

const route = useRoute()
const router = useRouter()
const toast = useToast()
const { t } = useI18n({ inheritLocale: true })

const knockoutRef = ref<InstanceType<typeof AssignMatchesKnockout> | null>(null)
const groupsRef = ref<InstanceType<typeof AssignMatchesGroups> | null>(null)

const { data: competition } = getCompetitionDetails(route, t, toast, {
	suc: () => {
		if (competition.value === null) return
	},
})

function prevPage() {
	router.replace({
		name: "editTeams",
		params: { tourId: route.params.tourId, compId: route.params.compId },
	})
}

function nextPage() {
	router.replace({
		name: "scheduleMatches",
		params: { tourId: route.params.tourId, compId: route.params.compId },
	})
}

function save() {
	if (competition.value?.tourType === TourType.GROUPS) {
		if (groupsRef.value === null) return
		groupsRef.value.save()
	} else {
		if (knockoutRef.value === null) return
		knockoutRef.value.save()
	}
}
</script>

<style scoped>
.v-enter-active,
.v-leave-active {
	transition: opacity 0.2s ease;
	width: 100%;
}

.v-enter-active {
	transition-delay: 0.2s;
}

.v-enter-from,
.v-leave-to {
	opacity: 0;
}
</style>

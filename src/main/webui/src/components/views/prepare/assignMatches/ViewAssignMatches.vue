<template>
	<div class="flex flex-column gap-1">
		<Transition v-if="competition">
			<Groups
				v-if="competition.tourType === TourType.GROUPS"
				ref="groupsRef"
				v-model:is-updating="isUpdating"
			/>
			<Knockout v-else ref="knockoutRef" v-model:is-updating="isUpdating" />
		</Transition>
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

const isUpdating = defineModel<boolean>("isUpdating", { default: false })
const knockoutRef = ref<InstanceType<typeof AssignMatchesKnockout> | null>(null)
const groupsRef = ref<InstanceType<typeof AssignMatchesGroups> | null>(null)

const { data: competition } = getCompetitionDetails(route, t, toast)

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

function reset() {
	if (competition.value?.tourType === TourType.GROUPS) {
		if (groupsRef.value === null) return
		groupsRef.value.reload()
	} else {
		if (knockoutRef.value === null) return
		knockoutRef.value.reload()
	}
}

defineExpose({ prevPage, save, reset, nextPage })
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

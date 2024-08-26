<template>
	<DraggablePanel
		id="match"
		:component-data="{
			tag: 'div',
			name: props.isUpdating || isUpdating ? 'playerList' : 'default',
			type: 'transition',
		}"
		group="match"
		:list="matches"
		:tag="TransitionGroup"
		class="flex flex-row flex-wrap gap-2 border-3 min-player-height border-round border-dashed"
		item-key="id"
		style="box-sizing: content-box; width: calc(100% - 6px)"
	>
		<template #default="{ item }">
			<div
				v-if="item"
				class="bg-primary-400 text-black border-round select-none cursor-pointer pl-3 pr-3 h-3rem inline text-50 flex align-items-center justify-content-center"
			>
				<span>{{ item.title }}</span>
			</div>
		</template>
	</DraggablePanel>
</template>

<script setup lang="ts">
import { computed, TransitionGroup, watch } from "vue"
import DraggablePanel from "@/draggable/DraggablePanel.vue"
import { getKnockout } from "@/backend/knockout"
import { useRoute } from "vue-router"
import { Match } from "@/interfaces/match"
import { getCompetitionDetails } from "@/backend/competition"
import { useI18n } from "vue-i18n"
import { useToast } from "primevue/usetoast"
import { getGroup } from "@/backend/group"
import { TourType } from "@/interfaces/competition"
import {
	EventMatch,
	extractGroupMatches,
	extractKnockoutMatches,
} from "@/components/views/prepare/scheduleMatches/ScheduleMatchesHelper"

const route = useRoute()
const { t } = useI18n({ inheritLocale: true })
const toast = useToast()

const { data: competition } = getCompetitionDetails(route, t, toast)
const { data: knockout } = getKnockout(
	route,
	computed(() => competition.value?.tourType === TourType.KNOCKOUT),
)
const { data: groups } = getGroup(
	route,
	computed(() => competition.value?.tourType === TourType.GROUPS),
)

const matches = defineModel<EventMatch[]>({ default: [] })
watch(
	[knockout, groups],
	() => {
		matches.value.splice(0, matches.value.length)
		if (competition.value?.tourType === TourType.KNOCKOUT && knockout.value) {
			extractKnockoutMatches(knockout.value, t, addMatch)
		} else if (
			competition.value?.tourType === TourType.GROUPS &&
			groups.value
		) {
			extractGroupMatches(groups.value, t, addMatch)
		}
	},
	{
		immediate: true,
	},
)

function addMatch(match: Match, title: string) {
	if (!match.begin)
		matches.value.push({
			title,
			...match,
		})
}

const props = withDefaults(
	defineProps<{
		isUpdating: boolean
	}>(),
	{
		isUpdating: false,
	},
)
</script>

<style scoped></style>

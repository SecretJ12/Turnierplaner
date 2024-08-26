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
				<span>{{ item.id }}</span>
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
import { KnockoutMatch } from "@/interfaces/knockoutSystem"
import { GroupMatch } from "@/interfaces/groupSystem"

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

const matches = defineModel<Match[]>({ default: [] })
watch(
	[knockout, groups],
	() => {
		matches.value.splice(0, matches.value.length)
		if (competition.value?.tourType === TourType.KNOCKOUT && knockout.value) {
			const queue: KnockoutMatch[] = []
			if (knockout.value?.finale) queue.push(knockout.value.finale)
			if (knockout.value?.thirdPlace) queue.push(knockout.value.thirdPlace)
			while (queue.length > 0) {
				matches.value.push(queue[0])
				if (queue[0].prevMatch) {
					queue.push(queue[0].prevMatch.a)
					queue.push(queue[0].prevMatch.b)
				}
				queue.splice(0, 1)
			}
		} else if (
			competition.value?.tourType === TourType.GROUPS &&
			groups.value
		) {
			const queue: GroupMatch[] = []
			if (groups.value?.finale) queue.push(groups.value.finale)
			if (groups.value?.thirdPlace) queue.push(groups.value.thirdPlace)
			while (queue.length > 0) {
				matches.value.push(queue[0])
				if (queue[0].prevMatch) {
					queue.push(queue[0].prevMatch.a)
					queue.push(queue[0].prevMatch.b)
				}
				queue.splice(0, 1)
			}
			groups.value?.groups.forEach((group) =>
				matches.value.push(...group.matches),
			)
		}
	},
	{ immediate: true },
)

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

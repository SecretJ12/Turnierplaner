<template>
	<vue-cal
		v-if="tournament"
		ref="vuecal"
		:key="calid"
		:selected-date="tournament.game_phase.begin"
		style="height: 1000px"
		active-view="day"
		:min-date="tournament.game_phase.begin"
		:max-date="tournament.game_phase.end"
		:disable-views="['years', 'year', 'month']"
		:locale="$i18n.locale"
		:time-from="8 * 60"
		:time-to="24 * 60"
		:split-days="splitDays"
		:sticky-split-labels="true"
		:min-event-width="80"
		:min-split-width="250"
		:min-cell-width="250"
		:time-cell-height="100"
		:snap-to-time="15"
		:editable-events="!!props.courts.length"
		:events="events"
		@event-drop="onEventDrop"
		@event-change="onEventChange"
		@view-change="onViewChange"
	>
		<template #event="{ event }">
			<EventMatch :match="event.data" :competition="competition" />
		</template>
	</vue-cal>
</template>

<script setup lang="ts">
// @ts-expect-error vue-cal does not have proper typescript support
import VueCal from "vue-cal"
import "vue-cal/dist/vuecal.css"
import { Court } from "@/interfaces/court"
import { computed, ref, watch } from "vue"
import {
	getTournamentDetails,
	getTournamentMatchEvents,
} from "@/backend/tournament"
import { useRoute } from "vue-router"
import { useToast } from "primevue/usetoast"
import { useI18n } from "vue-i18n"
import { getCompetitionDetails } from "@/backend/competition"
import { getKnockout } from "@/backend/knockout"
import { CompType } from "@/interfaces/competition"
import { getGroup } from "@/backend/group"
import { Match } from "@/interfaces/match"
import {
	CalEvent,
	extractGroupMatches,
	extractKnockoutMatches,
} from "@/components/views/prepare/scheduleMatches/ScheduleMatchesHelper"
import EventMatch from "@/components/views/prepare/scheduleMatches/MatchEvent.vue"

const calid = ref<number>(0)
const vuecal = ref()

function reload() {
	calid.value++
}

const emit = defineEmits<{ removeId: [id: string] }>()
const props = defineProps<{
	courts: Court[]
}>()
watch(() => props.courts, reload)

const { t } = useI18n({ inheritLocale: true })
const route = useRoute()
const toast = useToast()
const { data: tournament } = getTournamentDetails(route, t, toast)
const { data: competition } = getCompetitionDetails(route, t, toast)
const { data: knockout } = getKnockout(
	route,
	computed(() => competition.value?.tourType === CompType.KNOCKOUT),
)
const { data: groups } = getGroup(
	route,
	computed(() => competition.value?.tourType === CompType.GROUPS),
)

watch([vuecal, tournament], () => {
	if (!vuecal.value || !tournament.value) return
	// needed to always trigger the onViewChange
	vuecal.value.previous()
	vuecal.value.switchView("day", tournament.value.game_phase.begin)
})

const curStart = ref<Date | undefined>()
const curEnd = ref<Date | undefined>()
const { data: exMatches } = getTournamentMatchEvents(
	route,
	t,
	curStart,
	curEnd,
	computed(() => props.courts),
	computed(() => !!curStart.value && !!curEnd.value && !!props.courts),
)

const events = defineModel<CalEvent[]>({ default: [] })
watch(
	[knockout, groups],
	() => {
		events.value.splice(0, events.value.length)
		if (competition.value?.tourType === CompType.KNOCKOUT && knockout.value) {
			extractKnockoutMatches(knockout.value, t, addMatch)
		} else if (
			competition.value?.tourType === CompType.GROUPS &&
			groups.value
		) {
			extractGroupMatches(groups.value, t, addMatch)
		}

		addExisting()
	},
	{ immediate: true },
)

watch(exMatches, addExisting)

function addExisting() {
	for (let i = events.value.length - 1; i >= 0; i--)
		if (events.value[i].draggable === false) events.value.splice(i, 1)

	if (exMatches.value) {
		exMatches.value.forEach((match) => {
			events.value.push({
				draggable: false,
				resizable: false,
				deletable: false,
				class: "extern",
				...match,
			})
		})
	}
}

// on onViewChange: load events from begin to end for courts
// -> display as unchangeable event
function onViewChange({
	startDate,
	endDate,
}: {
	startDate: Date
	endDate: Date
}) {
	curStart.value = startDate
	curEnd.value = endDate
}

function addMatch(match: Match, title: string) {
	if (match.begin && match.end && match.court)
		events.value.push({
			start: match.begin,
			end: match.end,
			split: match.court,
			data: {
				title,
				compName: <string>route.params.compId,
				...match,
			},
		})
}

function onEventDrop({
	event,
	originalEvent,
	external,
}: {
	event: CalEvent
	originalEvent: CalEvent
	external: boolean
}) {
	if (external) {
		if (originalEvent.data.id) emit("removeId", originalEvent.data.id)
		else throw "Match id is missing"

		event.end = new Date(event.start)
		event.end.setHours(event.end.getHours() + 1)
		event.end.setMinutes(event.end.getMinutes() + 30)
		events.value.push({
			start: event.start,
			end: event.end,
			split: event.split,
			data: {
				...originalEvent.data,
				court: event.split,
			},
		})
	}
}

function onEventChange({
	event,
	originalEvent,
}: {
	event: CalEvent
	originalEvent: CalEvent
}) {
	if (!originalEvent) return

	events.value.splice(
		events.value.findIndex((e) => e.data.id === originalEvent.data.id),
		1,
	)
	events.value.push({
		start: event.start,
		end: event.end,
		split: event.split,
		data: {
			...originalEvent.data,
			begin: event.start,
			end: event.end,
			court: event.split,
		},
	})
}

const splitDays = computed(() => {
	return props.courts.map((court, index) => {
		return {
			id: court.name,
			label: court.name,
			class: "court" + (index + 1),
		}
	})
})
</script>

<style>
.court1 {
	background-color: rgba(221, 238, 255, 0.5);
	border-width: 2px 0;
}

.court2 {
	background-color: rgba(255, 232, 251, 0.5);
	border-width: 2px 0;
}

.court3 {
	background-color: rgba(221, 255, 239, 0.5);
	border-width: 2px 0;
}

.court4 {
	background-color: rgba(255, 250, 196, 0.5);
	border-width: 2px 0;
}

.court5 {
	background-color: rgba(255, 206, 178, 0.5);
	border-width: 2px 0;
}

.court6 {
	background-color: rgba(0, 0, 0, 0.1);
	border-width: 2px 0;
}

.vuecal__no-event {
	display: none;
}

.vuecal__event {
	background-color: rgba(164, 230, 210, 0.9);
}

.vuecal__event.extern {
	background-color: #fd9c42b9 !important;
}

.vuecal__cell--disabled {
	background-color: #a0a0a0 !important; /* Light grey background */
	color: #a0a0a0 !important; /* Grey text */
}
</style>

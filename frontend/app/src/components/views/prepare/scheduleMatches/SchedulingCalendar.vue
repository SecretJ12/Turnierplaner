<template>
	<vue-cal
		v-if="tournament"
		:key="calid"
		active-view="week"
		:disable-views="['years', 'year', 'month']"
		:locale="$i18n.locale"
		:time-from="8 * 60"
		:time-to="20 * 60"
		:split-days="splitDays"
		:sticky-split-labels="true"
		:min-event-width="80"
		:min-split-width="200"
		:snap-to-time="15"
		:editable-events="!!props.courts.length"
		:events="events"
		@event-drop="onEventDrop"
		@event-change="onEventChange"
	>
		<template #event="{ event }">
			<div class="w-full flex flex-column p-1 text-left">
				<div>{{ event.data.title }}</div>
				<div class="grid">
					<div class="col-6" style="font-size: 0.5rem">
						<template v-if="event.data.teamA">
							{{ event.data.teamA.playerA.name }}<br />
							<template v-if="event.data.teamA.playerB">
								{{ event.data.teamA.playerB.name }}
							</template>
						</template>
						<template v-else> N.A. </template>
					</div>
					<div class="col-6" style="font-size: 0.5rem">
						<template v-if="event.data.teamB">
							{{ event.data.teamB.playerA.name }}<br />
							<template v-if="event.data.teamB.playerB">
								{{ event.data.teamB.playerB.name }}
							</template>
						</template>
						<template v-else> N.A. </template>
					</div>
				</div>
			</div>
		</template>
	</vue-cal>
</template>

<script setup lang="ts">
// @ts-expect-error vue-cal does not have proper typescript support
import VueCal from "vue-cal"
import "vue-cal/dist/vuecal.css"
import { Court } from "@/interfaces/court"
import { computed, ref, watch } from "vue"
import { getTournamentDetails } from "@/backend/tournament"
import { useRoute } from "vue-router"
import { useToast } from "primevue/usetoast"
import { useI18n } from "vue-i18n"
import { getCompetitionDetails } from "@/backend/competition"
import { getKnockout } from "@/backend/knockout"
import { TourType } from "@/interfaces/competition"
import { getGroup } from "@/backend/group"
import { Match } from "@/interfaces/match"
import {
	EventMatch,
	extractGroupMatches,
	extractKnockoutMatches,
} from "@/components/views/prepare/scheduleMatches/ScheduleMatchesHelper"

const calid = ref<number>(0)
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
	computed(() => competition.value?.tourType === TourType.KNOCKOUT),
)
const { data: groups } = getGroup(
	route,
	computed(() => competition.value?.tourType === TourType.GROUPS),
)

const events = ref<
	{
		start: Date
		end: Date
		split: string
		data: EventMatch
	}[]
>([])
watch(
	[knockout, groups],
	() => {
		if (events.value.length) return

		events.value.splice(0, events.value.length)
		if (competition.value?.tourType === TourType.KNOCKOUT && knockout.value) {
			extractKnockoutMatches(knockout.value, t, addMatch)
		} else if (
			competition.value?.tourType === TourType.GROUPS &&
			groups.value
		) {
			extractGroupMatches(groups.value, t, addMatch)
		}
	},
	{ immediate: true },
)

function addMatch(match: Match, title: string) {
	if (match.begin && match.end && match.court)
		events.value.push({
			start: match.begin,
			end: match.end,
			split: match.court,
			data: {
				title,
				...match,
			},
		})
}

interface Event {
	start: Date
	end: Date
	split: string
	data: EventMatch
}

function onEventDrop({
	event,
	originalEvent,
	external,
}: {
	event: Event
	originalEvent: Event
	external: boolean
}) {
	if (external) {
		if (originalEvent.data.id) emit("removeId", originalEvent.data.id)
		else throw "Match id is missing"

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
	event: Event
	originalEvent: Event
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
</style>

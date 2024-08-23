<template>
	<vue-cal
		active-view="week"
		hide-view-selector
		:disable-views="['years', 'year', 'month', 'day']"
		:locale="$i18n.locale"
		:time-from="8 * 60"
		:time-to="20 * 60"
		:split-days="splitDays"
		:sticky-split-labels="true"
		:min-split-width="150"
		:snap-to-time="15"
		editable-events
		@event-drop="onEventDrop"
	/>
</template>

<script setup lang="ts">
import { KnockoutMatch } from "@/interfaces/knockoutSystem"
// @ts-expect-error vue-cal does not have proper typescript support
import VueCal from "vue-cal"
import "vue-cal/dist/vuecal.css"
import { Court } from "@/interfaces/court"
import { computed } from "vue"

const emit = defineEmits<{ removeId: [id: string] }>()

function onEventDrop({
	originalEvent,
	external,
}: {
	originalEvent: { match: KnockoutMatch }
	external: boolean
}) {
	if (external) {
		if (originalEvent.match.id) emit("removeId", originalEvent.match.id)
		else throw "Match id is missing"
	}
}

const props = defineProps<{
	courts: Court[]
}>()

const splitDays = computed(() => {
	return props.courts.map((court, index) => {
		return {
			id: index + 1,
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
</style>

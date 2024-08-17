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
		snapToTime="15"
	/>
</template>

<script setup lang="ts">
// @ts-ignore
import VueCal from "vue-cal"
import "vue-cal/dist/vuecal.css"

const splitDays = [
	{ id: 1, label: "Platz 1", class: "court1" },
	{ id: 2, label: "Platz 2", class: "court2" },
	{ id: 3, label: "Platz 3", class: "court3" },
	{ id: 4, label: "Platz 4", class: "court4" },
	{ id: 5, label: "Platz 5", class: "court5" },
	{ id: 6, label: "Platz 6", class: "court6" },
]
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
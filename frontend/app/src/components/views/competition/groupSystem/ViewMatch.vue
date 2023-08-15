<template>
	<p v-if="props.match.begin > new Date()">
		{{ $t("ViewGroupSystem.start") }}<br />
		{{ props.match.begin.toLocaleString(t("lang"), dateOptions) }}
	</p>
	<!--  Game started already, but no results yet-->
	<p
		v-else-if="
			props.match.begin <= new Date() &&
			(!props.match.sets || props.match.sets.length == 0)
		"
	>
		0-0
	</p>
	<div v-else-if="props.order">
		<div v-for="set in props.match.sets" :key="set.index">
			{{ set.scoreA }} - {{ set.scoreB }}
		</div>
	</div>
	<div v-else>
		<div v-for="set in props.match.sets" :key="set.index">
			{{ set.scoreB }} - {{ set.scoreA }}
		</div>
	</div>
</template>

<script setup lang="ts">
import { Match } from "@/interfaces/match"
import { useI18n } from "vue-i18n"

const { t } = useI18n({ inheritLocale: true })

// TODO fix warning
const props = withDefaults(
	defineProps<{
		match: Match
		order?: boolean
	}>(),
	{
		order: true,
	},
)

const dateOptions: Intl.DateTimeFormatOptions = {
	year: "2-digit",
	month: "numeric",
	day: "numeric",
	hour: "numeric",
	minute: "numeric",
}
</script>

<style scoped>
p {
	text-align: center;
	margin: 0;
}
</style>

<template>
	<p v-if="!props.match.finished">
		{{ $t("ViewGroupSystem.start") }}<br />
		<template v-if="props.match.begin">
			{{ props.match.begin.toLocaleString(t("lang"), dateOptions) }}
		</template>
		<template v-else> --- </template>
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

const { t } = useI18n()

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

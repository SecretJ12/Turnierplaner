<template>
	<ViewMatch
		:teamA="props.teamA"
		:teamB="props.teamB"
		:sets="props.sets"
		:finished="props.finished"
		:winner="props.winner"
		:single="props.single"
	>
		<template #teamA-name>
			{{
				props.teamA?.playerA
					? `${props.teamA.playerA.lastName}, ${props.teamA.playerA.firstName}`
					: ""
			}}
			<template v-if="!props.single">
				<br />
				{{
					props.teamA?.playerB
						? `${props.teamA.playerB.lastName}, ${props.teamA.playerB.firstName}`
						: ""
				}}
			</template>
		</template>

		<template #teamB-name>
			{{
				props.teamB?.playerA
					? `${props.teamB.playerA.lastName}, ${props.teamB.playerA.firstName}`
					: ""
			}}
			<template v-if="!props.single">
				<br />
				{{
					props.teamB?.playerB
						? `${props.teamB.playerB.lastName}, ${props.teamB.playerB.firstName}`
						: ""
				}}
			</template>
		</template>
	</ViewMatch>
	<div
		id="date"
		:class="{
			dateSingles: props.single,
			dateDoubles: !props.single,
		}"
	>
		{{ props.match.begin?.toLocaleString(t("lang"), dateOptions) || "" }}
	</div>
</template>

<script setup lang="ts">
import ViewMatch from "@/components/views/competition/knockoutSystem/ViewMatch.vue"
import { KnockoutMatch } from "@/interfaces/knockoutSystem"
import { Mode } from "@/interfaces/competition"
import { Team } from "@/interfaces/team"
import { Set } from "@/interfaces/match"
import { useI18n } from "vue-i18n"

const { t } = useI18n({ inheritLocale: true })

const props = defineProps<{
	match: KnockoutMatch
	mode: Mode
	teamA: Team | null
	teamB: Team | null
	sets: Array<Set> | null
	single: boolean
	finished: boolean
	winner: boolean | null
}>()

const dateOptions: Intl.DateTimeFormatOptions = {
	year: "2-digit",
	month: "numeric",
	day: "numeric",
	hour: "numeric",
	minute: "numeric",
}
</script>

<style scoped>
#date {
	text-align: right;
	padding-right: 20px;
	font-style: italic;
}

.dateSingles {
	height: 20px;
}

.dateDoubles {
	height: 28px;
}
</style>

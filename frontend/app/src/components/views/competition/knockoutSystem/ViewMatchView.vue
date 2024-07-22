<template>
	<ViewMatch
		:sets="props.sets"
		:finished="props.finished"
		:winner="props.winner"
		:single="props.single"
	>
		<template #teamA-name>
			<td class="name">
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
			</td>
		</template>

		<template #teamB-name>
			<td class="name">
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
			</td>
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
import ViewMatch from "@/components/views/competition/knockoutSystem/ViewMatch_old.vue"
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
	padding-right: 1rem;
	font-style: italic;
}

.dateSingles {
	height: 1rem;
}

.dateDoubles {
	height: 1rem;
}

.name {
	text-align: left;
	padding-left: 20px;
}
</style>

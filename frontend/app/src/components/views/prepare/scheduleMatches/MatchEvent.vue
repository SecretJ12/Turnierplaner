<template>
	<div class="w-full flex flex-column p-1 text-left">
		<strong>{{ genTitle(props.match.title, t) }}</strong>
		<i>
			{{ props.match.compName }}
		</i>
		<ul class="mt-1 pt-0 pl-4">
			<li v-if="props.match.teamA">
				<template v-if="props.match.teamA.playerA">
					{{ props.match.teamA.playerA.name }}
					<template v-if="props.match.teamA.playerB">
						<br />
						{{ props.match.teamA.playerB.name }}
					</template>
				</template>
			</li>
			<li v-if="props.match.teamB">
				<template v-if="props.match.teamB.playerA">
					{{ props.match.teamB.playerA.name }}
					<template v-if="props.match.teamB.playerB">
						<br />
						{{ props.match.teamB.playerB.name }}
					</template>
				</template>
			</li>
		</ul>
	</div>
</template>

<script setup lang="ts">
import { Competition } from "@/interfaces/competition"
import { AnnotatedMatch } from "@/interfaces/match"
import { genTitle } from "@/backend/tournament"
import { useI18n } from "vue-i18n"

const { t } = useI18n({ inheritLocale: true })

const props = defineProps<{
	match: AnnotatedMatch
	competition?: Competition
}>()
</script>

<style scoped></style>

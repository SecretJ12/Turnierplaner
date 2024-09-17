<template>
	<div class="w-full flex flex-column p-1 text-left">
		<strong>{{ genTitle(props.match.title, t) }}</strong>
		<i>
			{{ props.match.compName }}
		</i>
		<ul class="mt-1 pt-0 pl-4">
			<li v-if="props.match.teamA">
				<template v-if="props.match.teamA.playerA">
					<ViewPlayerName :player="props.match.teamA.playerA" />
					<template v-if="props.match.teamA.playerB">
						<br />
						<ViewPlayerName :player="props.match.teamA.playerB" />
					</template>
				</template>
			</li>
			<li v-if="props.match.teamB">
				<template v-if="props.match.teamB.playerA">
					<ViewPlayerName :player="props.match.teamB.playerA" />
					<template v-if="props.match.teamB.playerB">
						<br />
						<ViewPlayerName :player="props.match.teamB.playerB" />
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
import ViewPlayerName from "@/components/views/player/ViewPlayerName.vue"

const { t } = useI18n({ inheritLocale: true })

const props = defineProps<{
	match: AnnotatedMatch
	competition?: Competition
}>()
</script>

<style scoped></style>

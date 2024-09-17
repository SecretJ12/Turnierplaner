<template>
	<ViewPlayerName :player="props.team?.playerA" :inverted="props.inverted" />
	<template v-if="props.team?.playerB">
		<br />
		<ViewPlayerName :player="props.team?.playerB" :inverted="props.inverted" />
	</template>
</template>

<script setup lang="ts">
import { Team } from "@/interfaces/team"
import ViewPlayerName from "@/components/links/LinkPlayerName.vue"

const props = withDefaults(
	defineProps<{
		team?: Team | null
		inverted?: boolean
	}>(),
	{
		team: null,
		inverted: false,
	},
)
</script>

<style scoped></style>

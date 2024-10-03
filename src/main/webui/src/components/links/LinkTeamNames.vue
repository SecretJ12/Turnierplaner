<template>
	<ViewPlayerName
		:player="props.team?.playerA"
		:inverted="props.inverted"
		:short="props.short"
	/>
	<template v-if="props.team?.playerB">
		<br />
		<ViewPlayerName
			:player="props.team?.playerB"
			:inverted="props.inverted"
			:short="props.short"
		/>
	</template>
</template>

<script setup lang="ts">
import { Team } from "@/interfaces/team"
import ViewPlayerName from "@/components/links/LinkPlayerName.vue"

const props = withDefaults(
	defineProps<{
		team?: Team | null
		inverted?: boolean
		short?: boolean
	}>(),
	{
		team: null,
		inverted: false,
		short: false,
	},
)
</script>

<style scoped></style>

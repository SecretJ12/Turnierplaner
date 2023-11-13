<template>
	<div class="w-10rem">
		<div
			id="top"
			class="border-3 border-dashed h-3rem border-round-top content-box"
		>
			<PlayerBox v-if="team.playerA" :name="team.playerA.name"> </PlayerBox>
		</div>
		<div
			:class="{
				bottomSame: !props.different,
				bottomDifferent: props.different,
			}"
			class="border-3 border-top-none border-dashed h-3rem border-round-bottom content-box"
		>
			<PlayerBox
				v-if="team.playerB"
				:name="team.playerB.name"
				:secondary="different"
			>
			</PlayerBox>
		</div>
	</div>
</template>

<script lang="ts" setup>
import { Team } from "@/interfaces/match"
import PlayerBox from "@/components/views/prepare/editTeamsRefactored/PlayerBox.vue"

const props = defineProps<{
	different: boolean
	team: Team
}>()
</script>

<style scoped>
.content-box {
	box-sizing: content-box;
}

#top {
	border-left-color: var(--primary-400);
	border-top-color: var(--primary-400);
	border-right-color: var(--primary-400);
}

.bottomSame {
	border-left-color: var(--primary-400);
	border-bottom-color: var(--primary-400);
	border-right-color: var(--primary-400);
}

.bottomDifferent {
	border-left-color: var(--yellow-500);
	border-bottom-color: var(--yellow-500);
	border-right-color: var(--yellow-500);
}
</style>
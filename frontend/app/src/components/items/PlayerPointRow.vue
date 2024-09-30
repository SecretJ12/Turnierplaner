<template>
	<div class="grid">
		<div class="col-fixed" style="width: 300px">
			<div class="text-center p-3 border-round-sm bg-primary font-bold">
				{{ props.team?.playerA?.firstName }}
				{{ props.team?.playerA?.lastName }}
				{{ props.team?.playerB?.firstName }}
				{{ props.team?.playerB?.lastName }}
			</div>
		</div>
		<div
			v-for="(p, i) in gamePoints"
			v-if="gamePoints"
			class="col-fixed"
			style="width: 140px"
		>
			<InputNumber
				v-model="gamePoints[i]"
				:max="100"
				:min="0"
				:pt="{
					input: { root: { style: 'width: 120px', class: 'text-center' } },
				}"
				class="p-inputnumber-input"
				input-style-class="p-inputnumber"
				input-id="minmax-buttons"
				mode="decimal"
				style-class="p-inputnumber"
			/>
		</div>
	</div>
</template>

<script setup lang="ts">
import { Team } from "@/interfaces/team"

const gamePoints = defineModel("gamePoints", { type: Array<number> })
const props = defineProps<{
	team: Team | undefined | null
}>()
</script>

<style scoped></style>

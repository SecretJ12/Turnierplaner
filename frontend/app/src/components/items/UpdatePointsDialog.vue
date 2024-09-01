<template>
	<Dialog
		v-model:visible="visible"
		:style="{ width: '50rem' }"
		header="Update Score"
		modal
	>
		<span class="text-surface-500 dark:text-surface-400 block mb-2">{{
			t("general.update_score")
		}}</span>
		<div class="grid">
			<div class="col-fixed" style="width: 300px"></div>
			<div
				class="col-fixed flex justify-content-center flex-wrap"
				style="width: 140px"
				v-for="n in numberSets"
			>
				<RadioButton
					v-model="selectedSet"
					:inputId="n.toString()"
					name="sets"
					:value="(n - 1).toString()"
				/>
				<label class="ml-2" :for="n.toString()">Set {{ n }}</label>
			</div>
		</div>
		<PlayerPointRow
			:team="currentMatch?.teamA"
			:game-points="team1GamePoints"
			v-if="visible"
		/>
		<PlayerPointRow
			:team="currentMatch?.teamB"
			:game-points="team2GamePoints"
			v-if="visible"
		/>
		<divider />
		<div class="grid">
			<div class="col" v-for="n in 7">
				<Button
					:label="'6:' + (n - 1)"
					@click="updatePoints(6, n - 1)"
					v-if="n - 1 <= 4"
				/>
				<Button
					:label="'7:' + (n - 1)"
					@click="updatePoints(7, n - 1)"
					v-else
				/>
			</div>
		</div>
		<div class="grid">
			<div class="col" v-for="n in 7">
				<Button
					:label="n - 1 + ':6'"
					@click="updatePoints(n - 1, 6)"
					v-if="n - 1 <= 4"
				/>
				<Button :label="n - 1 + ':7'" @click="updatePoints(n - 1, 7)" v-else />
			</div>
		</div>
		<divider />
		<div class="flex justify-content-end flex-wrap gap-2">
			<Button
				label="Cancel"
				severity="secondary"
				type="button"
				@click="savePoints"
			></Button>
			<Button label="Save" type="button" @click="visible = false"></Button>
		</div>
	</Dialog>
</template>
<script lang="ts" setup>
import { Match } from "@/interfaces/match"
import { ref } from "vue"
import { useI18n } from "vue-i18n"
import PlayerPointRow from "@/components/items/PlayerPointRow.vue"
import { NumberSets } from "@/interfaces/competition"

const props = defineProps<{
	numberSets: NumberSets
}>()

const numberSets = props.numberSets ? 3 : 5

const { t } = useI18n({ inheritLocale: true })
const currentMatch = ref<Match | null>(null)
const visible = ref(false)
const selectedSet = ref("0")

const team1GamePoints = ref<number[]>([])
const team2GamePoints = ref<number[]>([])

const showPopUp = function (match: Match) {
	currentMatch.value = match
	team1GamePoints.value = match.sets?.map((set) => set.scoreA) ?? []
	team2GamePoints.value = match.sets?.map((set) => set.scoreB) ?? []
	console.log(team1GamePoints.value)
	visible.value = true
}

const savePoints = function () {
	visible.value = false
}

const updatePoints = function (point1: number, point2: number) {
	team1GamePoints.value[Number(selectedSet.value)] = point1
	team2GamePoints.value[Number(selectedSet.value)] = point2
}

defineExpose({
	showPopUp,
})
</script>

<style scoped>
.p-inputnumber input {
	width: 100%;
}

.p-inputnumber-input input {
	width: 100%;
}
</style>

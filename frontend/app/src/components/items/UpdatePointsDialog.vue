<template>
	<Dialog
		v-model:visible="visible"
		:style="[
			props.numberSets === NumberSets.THREE
				? { width: '50rem' }
				: { width: '70rem' },
		]"
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
			v-model:game-points="team1GamePoints"
			v-if="visible"
		/>
		<PlayerPointRow
			:team="currentMatch?.teamB"
			v-model:game-points="team2GamePoints"
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
			<Button label="Save" type="button" @click="savePoints"></Button>
		</div>
	</Dialog>
</template>
<script lang="ts" setup>
import { Match } from "@/interfaces/match"
import { computed, ref } from "vue"
import { useI18n } from "vue-i18n"
import PlayerPointRow from "@/components/items/PlayerPointRow.vue"
import { NumberSets } from "@/interfaces/competition"
import { useUpdateSet } from "@/backend/set"
import { useRoute } from "vue-router"
import { useToast } from "primevue/usetoast"

const { t } = useI18n({ inheritLocale: true })
const toast = useToast()

const route = useRoute()

const props = defineProps<{
	numberSets: NumberSets
}>()

const numberSets = computed(() =>
	props.numberSets === NumberSets.THREE ? 3 : 5,
)

const currentMatch = ref<Match | null>(null)
const visible = ref(false)
const selectedSet = ref("0")

const team1GamePoints = ref<number[]>([])
const team2GamePoints = ref<number[]>([])

const showPopUp = function (match: Match) {
	currentMatch.value = match
	team1GamePoints.value = match.sets?.map((set) => set.scoreA) ?? []
	team2GamePoints.value = match.sets?.map((set) => set.scoreB) ?? []
	// fill the rest up with 0
	for (let i = team1GamePoints.value.length; i < numberSets.value; i++) {
		team1GamePoints.value.push(0)
		team2GamePoints.value.push(0)
	}
	console.log(props.numberSets)
	console.log(numberSets.value)
	visible.value = true
}
const { mutate: updateSet } = useUpdateSet(route, t, toast)

const savePoints = function () {
	if (!currentMatch.value || !currentMatch.value.id) {
		console.log("No match")
		return
	}
	console.log("saving..")
	const sets = getAllChangedSets()
	console.log(sets)
	if (sets.length > 0)
		updateSet({
			matchId: currentMatch.value!.id,
			sets: sets,
		})
	visible.value = false
}

// return list of sets that contain all set that differ from the beginning
const getAllChangedSets = function () {
	let changedSets = []
	for (let i = 0; i < numberSets.value; i++) {
		const a = currentMatch.value?.sets?.[i].scoreA
		const b = currentMatch.value?.sets?.[i].scoreB
		if (
			(team1GamePoints.value[i] != 0 && team1GamePoints.value[i] !== a) ||
			(team2GamePoints.value[i] != 0 && team2GamePoints.value[i] !== b)
		) {
			changedSets.push({
				index: i,
				scoreA: team1GamePoints.value[i],
				scoreB: team2GamePoints.value[i],
			})
		}
	}
	return changedSets
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

<template>
	<div class="flex flex-column gap-2">
		<ViewConditions
			:beginGamePhase="props.tournament.game_phase.begin"
			:player="props.competition.playerA"
		/>
		<div class="p-inputgroup">
			<Dropdown
				v-model="selectedPlayer"
				:options="suggestionsPlayer"
				:loading="isLoading"
				:autoFilterFocus="true"
				:filterPlaceholder="t('ViewCompetition.searchPlayer')"
				:placeholder="t('ViewCompetition.selectPlayer')"
				option-label="value"
				dataKey="id"
				filter
				@filter="queryPlayer"
			>
				<template #empty>
					{{ t("ViewSignUp.atLeastOneLetter") }}
				</template>
				<template #emptyfilter>
					{{ t("ViewSignUp.noPlayerFound") }}
				</template>
			</Dropdown>
			<Button @click="addPlayer">
				{{ t("general.signUp") }}
			</Button>
		</div>
	</div>
</template>

<script setup lang="ts">
import ViewConditions from "@/components/views/competition/signup/ViewConditions.vue";
import { useI18n } from "vue-i18n";
import { DropdownFilterEvent } from "primevue/dropdown";
import axios from "axios";
import { Player, searchedPlayer } from "@/interfaces/player";
import { Competition, Sex } from "@/interfaces/competition";
import { Tournament } from "@/interfaces/tournament";
import { ref } from "vue";
import { useToast } from "primevue/usetoast";

const { t } = useI18n({ inheritLocale: true })
const toast = useToast()

const props = defineProps<{
	tournament: Tournament
	competition: Competition
}>()
const emit = defineEmits(['addPlayer'])

const selectedPlayer = ref<searchedPlayer | null>(null)
const suggestionsPlayer = ref<searchedPlayer[]>([])
const isLoading = ref<boolean>(false)
function queryPlayer(event: DropdownFilterEvent) {
	isLoading.value = true
	suggestionsPlayer.value = suggestionsPlayer.value.filter((item) => {
		return item.value.toLowerCase().includes(event.value.toLowerCase())
	})

	if (
		props.competition.playerA.hasMinAge &&
		props.competition.playerA.minAge === null
	) {
		console.log("Data invalid")
		return
	}
	// TODO put to backend file
	axios
		.get<Player[]>(
			`/player/find?search=${event.value}` +
			(props.competition.playerA.sex !== Sex.ANY
				? `&sex=${props.competition.playerA.sex}`
				: "") +
			(props.competition.playerA.hasMinAge &&
			props.competition.playerA.minAge !== null
				? `&minAge=${props.competition.playerA.minAge
					.toISOString()
					.slice(0, 10)}`
				: "") +
			(props.competition.playerA.hasMaxAge &&
			props.competition.playerA.maxAge !== null
				? `&minAge=${props.competition.playerA.maxAge
					.toISOString()
					.slice(0, 10)}`
				: ""),
		)
		.then((result) => {
			// TODO avoid races
			suggestionsPlayer.value = result.data.map((item) => {
				return {
					id: item.id,
					firstName: item.firstName,
					lastName: item.lastName,
					value: item.firstName + " " + item.lastName,
				}
			})
		})
		.catch((error) => {
			toast.add({
				severity: "error",
				summary: t("ViewCompetition.query_search_failed"),
				detail: error,
				life: 3000,
			})
			console.log(error)
		})
		.finally(() => {
			isLoading.value = false
		})
}

function addPlayer() {
	if (selectedPlayer.value)
		emit("addPlayer", {
			firstName: selectedPlayer.value.firstName,
			lastName: selectedPlayer.value.lastName,
			name: `${selectedPlayer.value.firstName} ${selectedPlayer.value.lastName}`
		})
}
</script>

<style scoped>
</style>
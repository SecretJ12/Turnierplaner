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
				option-label="name"
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
			<SplitButton
				:label="t('general.signUp')"
				@click="addPlayer"
				:model="items"
			/>
		</div>
		<Dialog
			v-model:visible="showPopUp"
			modal
			maximizable
			header="SpielerRegistrierung"
			:style="{ width: '70vw' }"
		>
			<ViewPlayerRegistrationPopUp
				ref="submitButton"
				@registered="registerPlayer"
			/>
			<template #footer>
				<Button
					:label="t('general.save')"
					icon="pi pi-check"
					@click="submitButton?.onSubmit()"
					autofocus
				/>
			</template>
		</Dialog>
	</div>
</template>

<script setup lang="ts">
import ViewConditions from "@/components/views/competition/signup/ViewConditions.vue"
import { useI18n } from "vue-i18n"
import { DropdownFilterEvent } from "primevue/dropdown"
import axios from "axios"
import { Player } from "@/interfaces/player"
import { Competition, Sex } from "@/interfaces/competition"
import { Tournament } from "@/interfaces/tournament"
import { ref } from "vue"
import { useToast } from "primevue/usetoast"
import Button from "primevue/button"
import ViewPlayerRegistrationPopUp from "@/components/views/player/ViewPlayerRegistrationPopUp.vue"

const { t } = useI18n({ inheritLocale: true })
const toast = useToast()

const props = defineProps<{
	tournament: Tournament
	competition: Competition
}>()
const emit = defineEmits(["addPlayer"])

const selectedPlayer = ref<Player | null>(null)
const suggestionsPlayer = ref<Player[]>([])
const isLoading = ref<boolean>(false)

const showPopUp = ref<boolean>(false)
const submitButton = ref<{ onSubmit: Function } | null>(null)

const items = [
	{
		label: "Register",
		icon: "pi pi-check",
		command: () => {
			showPopUp.value = true
		},
	},
]
function queryPlayer(event: DropdownFilterEvent) {
	isLoading.value = true
	suggestionsPlayer.value = suggestionsPlayer.value.filter((item) => {
		return item.name.toLowerCase().includes(event.value.toLowerCase())
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
					name: item.firstName + " " + item.lastName,
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
	if (selectedPlayer.value) emit("addPlayer", selectedPlayer.value)
}
function registerPlayer(player: Player) {
	showPopUp.value = false
	emit("addPlayer", player)
}
</script>

<style scoped></style>

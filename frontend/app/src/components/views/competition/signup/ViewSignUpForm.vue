<template>
	<template v-if="competition">
		<!-- SINGLE -->
		<template v-if="competition.mode === Mode.SINGLE">
			<!-- Registration player A -->
			<div class="flex flex-column gap-2">
				<ViewConditions />
				<div class="p-inputgroup">
					<Dropdown
						v-model="selectedPlayerA"
						:options="suggestionsPlayerA"
						:loading="loadingA"
						:auto-filter-focus="true"
						:filter-placeholder="t('ViewCompetition.searchPlayer')"
						:placeholder="t('ViewCompetition.selectPlayer')"
						option-label="name"
						data-key="id"
						filter
						@filter="queryPlayerA"
					>
						<template #empty>
							{{ t("ViewSignUp.atLeastOneLetter") }}
						</template>
						<template #emptyfilter>
							{{ t("ViewSignUp.noPlayerFound") }}
						</template>
					</Dropdown>
					<Button @click="signUpSingle">
						{{ t("general.signUp") }}
					</Button>
				</div>
			</div>
		</template>
		<!-- DOUBLE TOGETHER -->
		<template v-else-if="competition.signUp === SignUp.TOGETHER">
			<div class="flex flex-column">
				<div class="grid">
					<div class="col-6">
						<ViewConditions />
					</div>
					<div class="col-6">
						<ViewConditions :second="true" />
					</div>
				</div>
				<div class="grid">
					<div class="col-6">
						<Dropdown
							v-model="selectedPlayerA"
							class="w-full"
							:options="suggestionsPlayerA"
							:auto-filter-focus="true"
							:filter-placeholder="t('ViewCompetition.searchPlayer')"
							:placeholder="t('ViewCompetition.selectPlayer')"
							:loading="loadingA"
							option-label="name"
							data-key="id"
							filter
							@filter="queryPlayerA"
						>
							<template #empty>
								{{ t("ViewSignUp.atLeastOneLetter") }}
							</template>
							<template #emptyfilter>
								{{ t("ViewSignUp.noPlayerFound") }}
							</template>
						</Dropdown>
					</div>
					<div clasS="col-6">
						<Dropdown
							v-model="selectedPlayerB"
							class="w-full"
							:options="suggestionsPlayerB"
							:auto-filter-focus="true"
							:filter-placeholder="t('ViewCompetition.searchPlayer')"
							:placeholder="t('ViewCompetition.selectPlayer')"
							:loading="loadingB"
							option-label="name"
							data-key="id"
							filter
							@filter="queryPlayerB"
						>
							<template #empty>
								{{ t("ViewSignUp.atLeastOneLetter") }}
							</template>
							<template #emptyfilter>
								{{ t("ViewSignUp.noPlayerFound") }}
							</template>
						</Dropdown>
					</div>
				</div>
				<Button class="w-full" @click="signUpDoubleTog">
					{{ t("general.signUp") }}
				</Button>
			</div>
		</template>
		<!-- DOUBLE INDIVIDUAL SAME -->
		<template v-else-if="!competition.playerB.different">
			<div class="flex flex-column gutter gap-2">
				<!-- Registration player A -->
				<ViewConditions />
				<div class="p-inputgroup">
					<Dropdown
						v-model="selectedPlayerA"
						:options="suggestionsPlayerA"
						:auto-filter-focus="true"
						:filter-placeholder="t('ViewCompetition.searchPlayer')"
						:placeholder="t('ViewCompetition.selectPlayer')"
						:loading="loadingA"
						option-label="name"
						data-key="id"
						filter
						@filter="queryPlayerA"
					>
						<template #empty>
							{{ t("ViewSignUp.atLeastOneLetter") }}
						</template>
						<template #emptyfilter>
							{{ t("ViewSignUp.noPlayerFound") }}
						</template>
					</Dropdown>
					<Button @click="signUpDoubleIndSame">
						{{ t("general.signUp") }}
					</Button>
				</div>
			</div>
		</template>
		<!-- DOUBLE INDIVIDUAL DIFFERENT -->
		<template v-else>
			<div id="regDoubIndDif">
				<ViewConditions id="regDoubIndDifCondA" />
				<div id="regDoubIndDifRegA" class="p-inputgroup">
					<Dropdown
						v-model="selectedPlayerA"
						:options="suggestionsPlayerA"
						:auto-filter-focus="true"
						:filter-placeholder="t('ViewCompetition.selectPlayer')"
						:placeholder="t('ViewCompetition.selectPlayer')"
						:loading="loadingA"
						option-label="name"
						data-key="id"
						filter
						@filter="queryPlayerA"
					>
						<template #empty>
							{{ t("ViewSignUp.atLeastOneLetter") }}
						</template>
						<template #emptyfilter>
							{{ t("ViewSignUp.noPlayerFound") }}
						</template>
					</Dropdown>
					<Button @click="signUpDoubleIndDifA">
						{{ t("general.signUp") }}
					</Button>
				</div>

				<ViewConditions id="regDoubIndDifCondB" :second="true" />
				<div id="regDoubIndDifRegB" class="p-inputgroup">
					<Dropdown
						v-model="selectedPlayerB"
						:options="suggestionsPlayerB"
						:auto-filter-focus="true"
						:filter-placeholder="t('ViewCompetition.searchPlayer')"
						:placeholder="t('ViewCompetition.selectPlayer')"
						:loading="loadingB"
						option-label="name"
						data-key="id"
						filter
						@filter="queryPlayerB"
					>
						<template #empty>
							{{ t("ViewSignUp.atLeastOneLetter") }}
						</template>
						<template #emptyfilter>
							{{ t("ViewSignUp.noPlayerFound") }}
						</template>
					</Dropdown>
					<Button @click="signUpDoubleIndDifB">
						{{ t("general.signUp") }}
					</Button>
				</div>
			</div>
		</template>
	</template>
</template>

<script lang="ts" setup>
import { ref } from "vue"
import axios from "axios"
import ViewConditions from "@/components/views/competition/signup/ViewConditions.vue"
import { useRoute } from "vue-router"
import { Mode, SignUp } from "@/interfaces/competition"
import { Player, playerClientToServer } from "@/interfaces/player"
import { useI18n } from "vue-i18n"
import { DropdownFilterEvent } from "primevue/dropdown"
import { useToast } from "primevue/usetoast"
import { TeamServer } from "@/interfaces/team"
import { getCompetitionDetails } from "@/backend/competition"
import { useQueryClient } from "vue-query"
import { extractSearchPlayer, getPlayer } from "@/backend/player"

const { t } = useI18n({ inheritLocale: true })
const toast = useToast()

const route = useRoute()
const queryClient = useQueryClient()

const { data: competition } = getCompetitionDetails(route, t, toast, {})

const selectedPlayerA = ref<Player | null>(null)
const searchA = ref<string>("")
const { data: suggestionsPlayerA, isFetching: loadingA } = getPlayer(
	searchA,
	extractSearchPlayer(competition, false),
	t,
	toast,
)
const selectedPlayerB = ref<Player | null>(null)
const searchB = ref<string>("")
const { data: suggestionsPlayerB, isFetching: loadingB } = getPlayer(
	searchB,
	extractSearchPlayer(competition, true),
	t,
	toast,
)

function queryPlayerA(event: DropdownFilterEvent) {
	searchA.value = event.value
}

function queryPlayerB(event: DropdownFilterEvent) {
	searchB.value = event.value
}

function signUpPlayer(playerA: boolean) {
	const form: TeamServer = {
		playerA: null,
		playerB: null,
	}
	if (playerA) {
		if (!selectedPlayerA.value) {
			toast.add({
				severity: "error",
				summary: t("ViewSignUp.noPlayerSelected"),
				detail: t("ViewSignUp.selectPlayerFirst"),
				life: 3000,
			})
			return
		}
		form.playerA = playerClientToServer(selectedPlayerA.value)
	} else {
		if (!selectedPlayerB.value) {
			toast.add({
				severity: "error",
				summary: t("ViewSignUp.noPlayerSelected"),
				detail: t("ViewSignUp.selectPlayerFirst"),
				life: 3000,
			})
			return
		}
		form.playerB = playerClientToServer(selectedPlayerB.value)
	}

	axios
		.post(
			`/tournament/${route.params.tourId}/competition/${route.params.compId}/signUp`,
			form,
		)
		.then(() => {
			toast.add({
				severity: "success",
				summary: t("Player.register_success"),
				life: 3000,
			})
			queryClient.invalidateQueries([
				"signedUp",
				route.params.tourId,
				route.params.compId,
			])
		})
		.catch((error) => {
			if (error.response.status === 409)
				toast.add({
					severity: "error",
					summary: t("Player.already_exists"),
					life: 3000,
				})
			else
				toast.add({
					severity: "error",
					summary: t("Player.register_failed"),
					detail: error,
					life: 3000,
				})
		})
}

function signUpSingle() {
	signUpPlayer(true)
}

function signUpDoubleIndSame() {
	signUpPlayer(true)
}

function signUpDoubleIndDifA() {
	signUpPlayer(true)
}

function signUpDoubleIndDifB() {
	signUpPlayer(false)
}

function signUpDoubleTog() {
	if (!selectedPlayerA.value || !selectedPlayerB.value) {
		toast.add({
			severity: "error",
			summary: t("ViewSignUp.noPlayerSelected"),
			detail: t("ViewSignUp.selectPlayerFirst"),
			life: 3000,
		})
		return
	}
	const form = {
		playerA: playerClientToServer(selectedPlayerA.value),
		playerB: playerClientToServer(selectedPlayerB.value),
	}

	axios
		.post(
			`/tournament/${route.params.tourId}/competition/${route.params.compId}/signUp`,
			form,
		)
		.then(() => {
			toast.add({
				severity: "success",
				summary: t("Player.register_success"),
				life: 3000,
			})
			queryClient.invalidateQueries([
				"signedUp",
				route.params.tourId,
				route.params.compId,
			])
		})
		.catch((error) => {
			// TODO checks for team already partially registered also in backend
			if (error.response.status === 409)
				toast.add({
					severity: "error",
					summary: t("Player.already_exists"),
					life: 3000,
				})
			else
				toast.add({
					severity: "error",
					summary: t("Player.register_failed"),
					detail: error,
					life: 3000,
				})
		})
}
</script>

<style scoped>
#regDoubIndDif {
	display: grid;
	grid-template-columns: 1fr 1rem 1fr;
	grid-template-rows: auto 0.5rem auto;
}

#regDoubIndDifCondA {
	grid-column: 1;
	grid-row: 1;
}

#regDoubIndDifRegA {
	grid-column: 1;
	grid-row: 3;
}

#regDoubIndDifCondB {
	grid-column: 3;
	grid-row: 1;
}

#regDoubIndDifRegB {
	grid-column: 3;
	grid-row: 3;
}

@media only screen and (max-width: 750px) {
	#regDoubIndDif {
		grid-template-columns: 1fr;
		grid-template-rows: auto auto auto auto;
	}

	#regDoubIndDifCondA {
		grid-column: 1;
		grid-row: 1;
	}

	#regDoubIndDifRegA {
		grid-column: 1;
		grid-row: 2;
		margin-bottom: 10px;
	}

	#regDoubIndDifCondB {
		grid-column: 1;
		grid-row: 3;
	}

	#regDoubIndDifRegB {
		grid-column: 1;
		grid-row: 4;
	}
}
</style>

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
						:disabled="mutSingleLoading || mutDoubleLoading"
						@filter="queryPlayerA"
					>
						<template #empty>
							{{ t("ViewSignUp.atLeastOneLetter") }}
						</template>
						<template #emptyfilter>
							{{ t("ViewSignUp.noPlayerFound") }}
						</template>
					</Dropdown>
					<Button
						:disabled="mutSingleLoading || mutDoubleLoading"
						@click="signUpSingle"
					>
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
							:disabled="mutSingleLoading || mutDoubleLoading"
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
							:disabled="mutSingleLoading || mutDoubleLoading"
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
				<Button
					class="w-full"
					:disabled="mutSingleLoading || mutDoubleLoading"
					@click="signUpDoubleTog"
				>
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
						:disabled="mutSingleLoading || mutDoubleLoading"
						@filter="queryPlayerA"
					>
						<template #empty>
							{{ t("ViewSignUp.atLeastOneLetter") }}
						</template>
						<template #emptyfilter>
							{{ t("ViewSignUp.noPlayerFound") }}
						</template>
					</Dropdown>
					<Button
						:disabled="mutSingleLoading || mutDoubleLoading"
						@click="signUpDoubleIndSame"
					>
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
						:disabled="mutSingleLoading || mutDoubleLoading"
						@filter="queryPlayerA"
					>
						<template #empty>
							{{ t("ViewSignUp.atLeastOneLetter") }}
						</template>
						<template #emptyfilter>
							{{ t("ViewSignUp.noPlayerFound") }}
						</template>
					</Dropdown>
					<Button
						:disabled="mutSingleLoading || mutDoubleLoading"
						@click="signUpDoubleIndDifA"
					>
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
						:disabled="mutSingleLoading || mutDoubleLoading"
						@filter="queryPlayerB"
					>
						<template #empty>
							{{ t("ViewSignUp.atLeastOneLetter") }}
						</template>
						<template #emptyfilter>
							{{ t("ViewSignUp.noPlayerFound") }}
						</template>
					</Dropdown>
					<Button
						:disabled="mutSingleLoading || mutDoubleLoading"
						@click="signUpDoubleIndDifB"
					>
						{{ t("general.signUp") }}
					</Button>
				</div>
			</div>
		</template>
	</template>
</template>

<script lang="ts" setup>
import { ref } from "vue"
import ViewConditions from "@/components/views/competition/signup/ViewConditions.vue"
import { useRoute } from "vue-router"
import { Mode, SignUp } from "@/interfaces/competition"
import { Player } from "@/interfaces/player"
import { useI18n } from "vue-i18n"
import { DropdownFilterEvent } from "primevue/dropdown"
import { useToast } from "primevue/usetoast"
import {
	getCompetitionDetails,
	useSignUpDoubleTog,
	useSignUpSingle,
} from "@/backend/competition"
import { useQueryClient } from "@tanstack/vue-query"
import { findCompPlayers } from "@/backend/player"

const { t } = useI18n()
const toast = useToast()

const route = useRoute()
const queryClient = useQueryClient()

const { data: competition } = getCompetitionDetails(route, t, toast)

const selectedPlayerA = ref<Player | null>(null)
const searchA = ref<string>("")
const { data: suggestionsPlayerA, isFetching: loadingA } = findCompPlayers(
	searchA,
	route,
	false,
	t,
	toast,
)
const selectedPlayerB = ref<Player | null>(null)
const searchB = ref<string>("")
const { data: suggestionsPlayerB, isFetching: loadingB } = findCompPlayers(
	searchB,
	route,
	true,
	t,
	toast,
)

const { mutate: mutateSignUpSingle, isPending: mutSingleLoading } =
	useSignUpSingle(route, t, toast, queryClient)
const { mutate: mutateSignUpDoubleTog, isPending: mutDoubleLoading } =
	useSignUpDoubleTog(route, t, toast, queryClient)

function queryPlayerA(event: DropdownFilterEvent) {
	searchA.value = event.value
}

function queryPlayerB(event: DropdownFilterEvent) {
	searchB.value = event.value
}

function signUpSingle() {
	mutateSignUpSingle({ player: selectedPlayerA, playerB: false })
}

function signUpDoubleIndSame() {
	mutateSignUpSingle({ player: selectedPlayerA, playerB: false })
}

function signUpDoubleIndDifA() {
	mutateSignUpSingle({ player: selectedPlayerA, playerB: false })
}

function signUpDoubleIndDifB() {
	mutateSignUpSingle({ player: selectedPlayerB, playerB: true })
}

function signUpDoubleTog() {
	mutateSignUpDoubleTog({ playerA: selectedPlayerA, playerB: selectedPlayerB })
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

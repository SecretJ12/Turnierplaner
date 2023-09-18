<template>
	<template
		v-if="
			competition?.mode === Mode.SINGLE ||
			competition?.signUp === SignUp.TOGETHER
		"
	>
		<!-- TODO internalization -->
		There's nothing to do for this configuration
	</template>
	<template v-else>
		PlayersA: {{ playersA }}<br />
		PlayersB: {{ playersB }}<br />
		Animation: {{ animation }}<br />
		<div class="grid">
			<div class="col-5 flex flex-column gap-4">
				<SplitButton
					label="Randomize"
					severity="help"
					:model="randomizeItems"
					@click="randomize"
					class="w-fit"
				>
					<template #icon>
						<span class="material-icons mb-1" style="font-size: 1.2rem"
							>casino</span
						>
					</template>
				</SplitButton>
				<Fieldset
					:legend="competition?.playerB.different ? 'Player 1' : 'Player'"
				>
					<VueDraggable
						v-model="playersA"
						group="playerA"
            ghostClass="ghost"
						class="flex flex-row flex-wrap gap-2"
					>
            <div v-for="element in playersA" :key="element.name">
							<Player :key="element.name" :player="element" />
            </div>
					</VueDraggable>
				</Fieldset>
				<Fieldset legend="Player 2" v-if="competition?.playerB.different">
					<VueDraggable
              v-model="playersB"
              group="playerB"
              ghostClass="ghost"
              class="flex flex-row flex-wrap gap-2"
					>
              <div v-for="element in playersB" :key="element.name">
							<Player :key="element.name" :player="element" secondary />
						</div>
					</Vuedraggable>
				</Fieldset>
			</div>
			<div class="col-7 flex flex-row gap-4">
				<DataTable :value="teamsA" showGridlines stripedRows :reorderableColumns="true" @rowReorder="onRowReorderA">
					<Column class="w-6 " header="Player 1" field="name" >
						<template #body="{ index }" >
              <VueDraggable
                  v-model="teamsA[index]"
                  ghostClass="ghost"
                  :group="
									teamsA[index].length === 0
										? 'playerA'
										: {
												name: 'playerA',
												put: false,
										  }
								"
                  :="{
									animation: 200,
									ghostClass: 'ghost',
								}"

                  class="mt-1 mb-1 flex align-items-center justify-content-center"
                  :class="{
									'h-2rem border-dashed dragTo':
										teamsB[index].length === 0,
								}"
              >
                <div v-for="element in teamsA[index]" :key="element.name">
									<div :key="element.name">
										<Player :player="element" />
									</div>
                  </div>
        </Vuedraggable>
						</template>
					</Column>
        </DataTable>

        <DataTable :value="teamsB" showGridlines stripedRows :reorderableColumns="true" @rowReorder="onRowReorderB">
          <Column class="w-6 " header="Player 1" field="name" >
            <template #body="{ index }" >
              <VueDraggable
                  v-model="teamsB[index]"
                  ghostClass="ghost"
                  :group="
									teamsB[index].length === 0
										? competition?.playerB.different
											? 'playerB'
											: 'playerA'
										: {
												name: competition?.playerB.different
													? 'playerB'
													: 'playerA',
												put: false,
										  }
								"


                  class="mt-1 mb-1 flex align-items-center justify-content-center"

                  :class="{
									'h-2rem border-dashed dragTo':
										teamsB[index].length === 0,
								}"
              >
                <div v-for="element in teamsB[index]" :key="element.name">
                  <div :key="element.name">
                    <Player :player="element"
                            :secondary="competition?.playerB.different || false"
                    />
                  </div>
                </div>
              </Vuedraggable>
            </template>
          </Column>
        </DataTable>
<!--        <DataTable :value="teamsB" show-gridlines stripedRows :reorderableColumns="true" @rowReorder="onRowReorderB">-->
<!--					<Column class="w-6" header="Player 2" field="name">-->
<!--						<template #body="{ index }">-->
<!--							<VueDraggable-->
<!--								:v-model="teamsB[index]"-->
<!--                ghostClass="ghost"-->
<!--								:group="-->
<!--									teamsB[index].length === 0-->
<!--										? competition?.playerB.different-->
<!--											? 'playerB'-->
<!--											: 'playerA'-->
<!--										: {-->
<!--												name: competition?.playerB.different-->
<!--													? 'playerB'-->
<!--													: 'playerA',-->
<!--												put: false,-->
<!--										  }-->
<!--								"-->
<!--								class="mt-1 mb-1 flex align-items-center justify-content-center"-->
<!--								:class="{-->
<!--									'h-2rem border-dashed dragTo':-->
<!--										teamsB[index].length === 0,-->
<!--								}"-->
<!--							>-->
<!--                  <div v-for="element in teamsB[index]" :key="element.name">-->
<!--									<div :key="element.name">-->
<!--										<Player-->
<!--											:player="element"-->
<!--											:secondary="competition?.playerB.different || false"-->
<!--										/>-->
<!--									</div>-->
<!--                  </div>-->
<!--							</VueDraggable>-->
<!--						</template>-->
<!--					</Column>-->
<!--				</DataTable>-->
			</div>
		</div>
	</template>

	<div class="mt-2 grid grid-nogutter justify-content-between">
		<Button
			label="Back"
			icon="pi pi-angle-left"
			icon-pos="left"
			@click="previousPage"
		/>
		<!-- TODO add @click -->
		<Button
			v-if="
				!(
					competition?.mode === Mode.SINGLE ||
					competition?.signUp === SignUp.TOGETHER
				)
			"
			:label="t('general.save')"
		>
		</Button>
		<Button
			v-if="route.params.step !== 'scheduleMatches'"
			label="Next"
			icon="pi pi-angle-right"
			icon-pos="right"
			@click="nextPage"
		/>
	</div>
</template>

<script setup lang="ts">
import { VueDraggable } from 'vue-draggable-plus'
import { ref } from "vue"
import { getCompetitionDetails } from "@/backend/competition"
import { useRoute, useRouter } from "vue-router"
import { useToast } from "primevue/usetoast"
import { useI18n } from "vue-i18n"
import { Mode, SignUp } from "@/interfaces/competition"
import axios from "axios"
import { Team } from "@/interfaces/registration/team"
import { signedUpPlayer } from "@/interfaces/player"

const route = useRoute()
const router = useRouter()
const toast = useToast()
const { t } = useI18n({ inheritLocale: true })
const competition = getCompetitionDetails(route, t, toast, {
	suc: update,
})

const animation = ref<boolean>(false)

const playersA = ref<signedUpPlayer[]>([])
const playersB = ref<signedUpPlayer[]>([])

// const teams = ref<{ playerA: signedUpPlayer[]; playerB: signedUpPlayer[] }[]>(
// 	[],
// )

const teamsA = ref<signedUpPlayer[][]>([])
const teamsB = ref<signedUpPlayer[][]>([])

function sleep(milliseconds: number) {
	return new Promise((resolve) => setTimeout(resolve, milliseconds))
}

const delay = 100
const delayBetween = 50

async function randomize() {
	if (!competition.value) return

	animation.value = true
	for (const i in teamsA.value) {
    if (teamsA.value[i].length === 0) {
      const r = Math.floor(Math.random() * playersA.value.length)
      const element = playersA.value[r]
      playersA.value = playersA.value.filter((v, i) => i !== r)
      await sleep(delayBetween)
      teamsA.value[i].push(element)
      await sleep(delay)
    }
  }
  for(const i in teamsB.value){
		if (competition.value.playerB.different) {
			if (teamsB.value[i].length === 0) {
				const r = Math.floor(Math.random() * playersB.value.length)
				const element = playersB.value[r]
				playersB.value = playersB.value.filter((v, i) => i !== r)
				await sleep(delayBetween)
				teamsB.value[i].push(element)
				await sleep(delay)
			}
		} else {
			if (teamsB.value[i].length === 0) {
				const r = Math.floor(Math.random() * playersA.value.length)
				const element = playersA.value[r]
				playersA.value = playersA.value.filter((v, i) => i !== r)
				await sleep(delayBetween)
				teamsB.value[i].push(element)
				await sleep(delay)
			}
		}
	}
	animation.value = false
}

const randomizeItems = [
	{
		label: "Reroll",
		icon: "pi pi-refresh",
		command: reroll,
	},
	{
		label: "Reset",
		icon: "pi pi-times",
		command: reset,
	},
]

const onRowReorderA = (event) => {
  teamsA.value = event.value;
  toast.add({severity:'success', summary: 'Rows Reordered', life: 3000});
};

const onRowReorderB = (event) => {
  teamsB.value = event.value;
  toast.add({severity:'success', summary: 'Rows Reordered', life: 3000});
};

async function reset() {
	if (!competition.value) return

	animation.value = true
	for (const i in teamsA.value) {
		if (teamsA.value[i].length === 1) {
			const element = teamsA.value[i][0]
			teamsA.value[i] = []
			await sleep(delayBetween)
			playersA.value.push(element)
			await sleep(delay)
		}
		if (competition.value.playerB.different) {
			if (teamsB.value[i].length === 1) {
				const element = teamsB.value[i][0]
				teamsB.value[i] = []
				await sleep(delayBetween)
				playersB.value.push(element)
				await sleep(delay)
			}
		} else {
			if (teamsB.value[i].length === 1) {
				const element = teamsB.value[i][0]
				teamsB.value[i] = []
				await sleep(delayBetween)
				playersA.value.push(element)
				await sleep(delay)
			}
		}
	}
	animation.value = false
}

async function reroll() {
	await reset()
	await randomize()
}

function update() {
	playersA.value = []
	playersB.value = []
	teamsA.value = []
	teamsB.value = []

	if (
		competition.value?.mode === Mode.SINGLE ||
		competition.value?.signUp === SignUp.TOGETHER
	)
		return

	axios
		.get<Team[]>(
			`/tournament/${route.params.tourId}/competition/${route.params.compId}/signedUpTeams`,
		)
		.then((response) => {
			response.data.forEach((team) => {
				if (team.playerA !== undefined && team.playerB === undefined)
					playersA.value.push({
						firstName: team.playerA.firstName,
						lastName: team.playerA.lastName,
						name: team.playerA.firstName + " " + team.playerA.lastName,
					})
				else if (team.playerA === undefined && team.playerB !== undefined)
					playersB.value.push({
						firstName: team.playerB.firstName,
						lastName: team.playerB.lastName,
						name: team.playerB.firstName + " " + team.playerB.lastName,
					})
				else if (team.playerA !== undefined && team.playerB !== undefined) {
					teamsA.value.push(
						 [
							{
								firstName: team.playerA.firstName,
								lastName: team.playerA.lastName,
								name: team.playerA.firstName + " " + team.playerA.lastName,
							},
						])
          teamsB.value.push(
						 [
							{
								firstName: team.playerB.firstName,
								lastName: team.playerB.lastName,
								name: team.playerB.firstName + " " + team.playerB.lastName,
							},
						])
				}
				while (
					teamsA.value.length <
					Math.min(playersA.value.length, playersB.value.length)
				) {
					teamsA.value.push(
						[]
					)
				}
        while (
            teamsB.value.length <
            Math.min(playersA.value.length, playersB.value.length)
            ) {
          teamsB.value.push(
              []
          )
        }
			})
		})
		.catch((error) => {
			teamsA.value = []
			teamsB.value = []
			playersA.value = []
			playersB.value = []
			toast.add({
				severity: "error",
				summary: t("ViewCompetition.query_player_failed"),
				life: 3000,
			})
			console.log(error)
		})
}

function save() {
	// TODO udpate players
	toast.add({
		severity: "success",
		summary: "Success",
		detail: "Players updated",
		life: 3000,
	})
}

defineExpose({ save })

function previousPage() {
	router.replace({
		name: "chooseMode",
		params: { tourId: route.params.tourId, compId: route.params.compId },
	})
}

function nextPage() {
	router.replace({
		name: "selectType",
		params: { tourId: route.params.tourId, compId: route.params.compId },
	})
}
</script>

<style scoped>
.playerList-enter-active,
.playerList-leave-active,
.teamList-enter-active,
.teamList-leave-active {
	transition: all 0.5s ease;
}

.playerList-enter-from,
.playerList-leave-to,
.teamList-enter-from,
.teamList-leave-to {
	opacity: 0;
	transform: scale(0%);
}

.ghost {
	visibility: hidden;
}
</style>
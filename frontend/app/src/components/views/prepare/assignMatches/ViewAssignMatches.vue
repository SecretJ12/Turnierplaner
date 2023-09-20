<template>
	<div class="grid">
		<div class="col-4">
			<Button class="mb-2"> Shuffle</Button>
			<DataTable :value="teams" show-gridlines striped-rows removable-sort>
				<Column header="Teams" field="id">
					<template #body="{ index }">
						<div>
							<DraggablePanel
								:list="[teams[index]]"
								item-key="id"
								:tag="TransitionGroup"
								:componentData="{
									tag: 'div',
									name: 'teamList',
									type: 'transition',
								}"
								group="teams"
								single
								class="flex align-tems-center justify-content-center border-round"
							>
								<template #default="{ item }">
									<div>
										<PlayerCard
											:player="{
												name:
													item.playerA.firstName +
													item.playerA.lastName +
													(item.playerB?.firstName
														? ' // ' +
														  item.playerB?.firstName +
														  item.playerB?.lastName
														: ''),
											}"
										/>
									</div>
								</template>
							</DraggablePanel>
						</div>
					</template>
				</Column>
			</DataTable>
		</div>

		<!-- TODO some more drag'n'drop stuff -->
		<template v-if="competition">
			<template v-if="competition.tourType === TourType.GROUPS">
				<DataTable
					v-for="i in [1, 2]"
					:key="i"
					class="col-4"
					:value="[{ name: 1 }, { name: 2 }, { name: 3 }, { name: 4 }]"
					show-gridlines
					striped-rows
					removable-sort
				>
					<Column :header="'Group ' + i" field="name">
						<template #body>
							<div
								class="h-2rem border-dashed"
								style="margin: -0.5rem; width: calc(100% + 1rem)"
							></div>
						</template>
					</Column>
				</DataTable>
			</template>
			<template v-else>
				<div class="col-8 flex justify-content-center">
					<ViewKnockoutTree
						:match="knockoutSystem.finale"
						:third-place="knockoutSystem.thirdPlace"
						:mode="competition.mode"
					/>
				</div>
			</template>
		</template>
	</div>

	<div class="grid grid-nogutter justify-content-between mt-4">
		<Button label="Back" icon="pi pi-angle-left" @click="prevPage" />
		<Button :label="t('general.save')"> </Button>
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
import { useRoute, useRouter } from "vue-router"
import { useI18n } from "vue-i18n"
import { getCompetitionDetails } from "@/backend/competition"
import { useToast } from "primevue/usetoast"
import ViewKnockoutTree from "@/components/views/competition/knockoutSystem/ViewKnockoutTree.vue"
import { TourType } from "@/interfaces/competition"
import { KnockoutMatch } from "@/interfaces/knockoutSystem"
import { ref, TransitionGroup } from "vue"
import axios from "axios"
import DraggablePanel from "@/draggable/DraggablePanel.vue"
import { Team } from "@/interfaces/match"
import PlayerCard from "@/components/views/prepare/assignTeams/PlayerCard.vue"

const route = useRoute()
const router = useRouter()
const toast = useToast()
const { t } = useI18n({ inheritLocale: true })

const competition = getCompetitionDetails(route, t, toast, {
	suc: () => {
		if (competition.value === null) return
		update()
	},
})

const teams = ref<Team[]>([])

function generateTree(size: number): KnockoutMatch {
	return {
		court: "a",
		begin: new Date(),
		end: new Date(),
		finished: false,
		winner: null,
		teamA: null,
		teamB: null,
		sets: null,
		curGame: null,
		prevMatch:
			size > 0
				? {
						winner: true,
						a: generateTree(size - 1),
						b: generateTree(size - 1),
				  }
				: undefined,
	}
}

const knockoutSystem = {
	finale: generateTree(1),
	thirdPlace: generateTree(0),
}

function update() {
	axios
		.get<Team[]>(
			`tournament/${route.params.tourId}/competition/${route.params.compId}/signedUpTeams`,
		)
		.then((response) => {
			teams.value = response.data
			console.log(teams.value)
		})
		.catch((error) => {
			console.log(error)
		})
}

function prevPage() {
	router.replace({
		name: "assignTeams",
		params: { tourId: route.params.tourId, compId: route.params.compId },
	})
}

function nextPage() {
	router.replace({
		name: "scheduleMatches",
		params: { tourId: route.params.tourId, compId: route.params.compId },
	})
}
</script>

<style scoped></style>

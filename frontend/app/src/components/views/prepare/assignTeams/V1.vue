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
		<!-- TODO drag n drop stuff -->
		<div class="grid">
			<DataTable
				class="col-4"
				:value="[{ name: 1 }, { name: 2 }, { name: 3 }, { name: 4 }]"
				show-gridlines
				striped-rows
				removable-sort
			>
				<Column header="Name" field="name" sortable>
					<template #body="{ data }">
						<draggable
							:list="[data]"
							:group="{ name: 'player', pull: 'clone', put: false }"
						>
							<template #item="{ element: element }">
								<Player :name="element.name" />
							</template>
						</draggable>
					</template>
				</Column>
			</DataTable>
			<DataTable
				class="col-8"
				:value="teams"
				show-gridlines
				striped-rows
			>
				<Column header="Player 1" field="name">
					<template #body="{ index }">
						<draggable
							:list="teams[index].playerA"
							:group="{
								name: 'player',
								put: teams[index].playerA.length === 0,
							}"
							tag="div"
							:class="{
								'h-2rem border-dashed dragTo':
									teams[index].playerA.length === 0,
							}"
						>
							<template #item="{ element: element }">
								<Player :name="element.name" />
							</template>
						</draggable>
					</template>
				</Column>
				<Column header="Player 2" field="name">
					<template #body="{ index }">
						<draggable
							:list="teams[index].playerB"
							:group="{
								name: 'player',
								put: teams[index].playerB.length === 0,
							}"
							tag="div"
							:class="{
								'h-2rem border-dashed dragTo': teams[index].playerB.length === 0,
							}"
						>
							<template #item="{ element: element }">
								<Player :name="element.name" />
							</template>
						</draggable>
					</template>
				</Column>
			</DataTable>
		</div>
	</template>

	<div class="grid grid-nogutter justify-content-between mt-4">
		<Button label="Back" icon="pi pi-angle-left" @click="prevPage" />
		<!-- TODO add @click -->
		<Button :label="t('general.save')"></Button>
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
import draggable from "vuedraggable"
import { Mode, SignUp } from "@/interfaces/competition"
import { ref } from "vue"
import Player from "@/components/views/prepare/assignTeams/PlayerV1.vue";

const route = useRoute()
const router = useRouter()
const toast = useToast()
const { t } = useI18n({ inheritLocale: true })

const competition = getCompetitionDetails(route, t, toast, {
	suc: () => {
		if (competition.value === null) return
	},
})

const teams = ref(
	Array.from({ length: 2 }, () => {
		return { playerA: [], playerB: [] }
	}),
)

function prevPage() {
	router.replace({
		name: "selectType",
		params: { tourId: route.params.tourId, compId: route.params.compId },
	})
}

function nextPage() {
	router.replace({
		name: "assignMatches",
		params: { tourId: route.params.tourId, compId: route.params.compId },
	})
}
</script>

<style scoped>
.dragTo {
	margin: -0.5rem;
	width: calc(100% + 1rem);
}
</style>
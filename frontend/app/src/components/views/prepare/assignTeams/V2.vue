<template>
	<div class="grid">
		<div class="col-4 flex flex-column gap-4">
			<Fieldset legend="Player 1">
				<draggable
					:list="playerA"
					group="playerA"
					tag="div"
					class="flex flex-row flex-row flex-wrap gap-2"
					itemKey="name"
				>
					<template #item="{ element }">
						<PlayerV2 :player="element" />
					</template>
				</draggable>
			</Fieldset>
			<Fieldset legend="Player 2" v-if="competition?.playerB.different">
				<draggable
					:list="playerB"
					group="playerB"
					tag="div"
					class="flex flex-row flex-row flex-wrap gap-2"
					itemKey="name"
				>
					<template #item="{ element }">
						<PlayerV2 :player="element" secondary />
					</template>
				</draggable>
			</Fieldset>
		</div>
		<div class="col-offset-1 col-7 flex flex-column gap-4">
			<DataTable
				:value="teams"
				showGridlines
				stripedRows
				>
				<Column class="w-6" header="Player 1" field="name">
					<template #body="{ index }">
						<draggable
							:list="teams[index].playerA"
							:group="teams[index].playerA.length === 0 ? 'playerA' : {
								name: 'playerA',
								put: false,
							}"
							tag="div"
							class="mt-1 mb-1 flex align-items-center justify-content-center"
							:class="{
								'h-2rem border-dashed dragTo':
									teams[index].playerA.length === 0,
							}"
							itemKey="name"
						>
							<template #item="{ element: element }">
								<div>
									<PlayerV2 :player="element" />
								</div>
							</template>
						</draggable>
					</template>
				</Column>
				<Column class="w-6" header="Player 2" field="name">
					<template #body="{ index }">
						<draggable
							:list="teams[index].playerB"
							:group="teams[index].playerB.length === 0 ? 'playerB' : {
								name: 'playerB',
								put: false,
							}"
							tag="div"
							class="mt-1 mb-1 flex align-items-center justify-content-center"
							:class="{
								'h-2rem border-dashed dragTo': teams[index].playerB.length === 0,
							}"
							itemKey="name"
						>
							<template #item="{ element: element }">
								<div>
									<PlayerV2 :player="element" :secondary="competition?.playerB.different || false" />
								</div>
							</template>
						</draggable>
					</template>
				</Column>
			</DataTable>
		</div>
	</div>
</template>

<script setup lang="ts">
import PlayerV2 from "@/components/views/prepare/assignTeams/PlayerV2.vue"
import draggable from "vuedraggable"
import { ref } from "vue"
import { getCompetitionDetails } from "@/backend/competition"
import { useRoute } from "vue-router"
import { useToast } from "primevue/usetoast"
import { useI18n } from "vue-i18n"

const route = useRoute()
const toast = useToast()
const { t } = useI18n({ inheritLocale: true })
const competition = getCompetitionDetails(route, t, toast, {})

const playerA = ref<{ name: string }[]>([
	{ name: "PlayerA 1" },
	{ name: "PlayerA 2" },
	{ name: "PlayerA 3" },
	{ name: "PlayerA 4" },
	{ name: "PlayerA 5" },
	{ name: "PlayerA 6" },
	{ name: "PlayerA 7" },
	{ name: "PlayerA 8" },
])
const playerB = ref<{ name: string }[]>([
	{ name: "PlayerB 1" },
	{ name: "PlayerB 2" },
	{ name: "PlayerB 3" },
	{ name: "PlayerB 4" },
])

const teams = ref(
	Array.from({ length: 4 }, () => {
		return { playerA: [], playerB: [] }
	}),
)
</script>

<style scoped></style>
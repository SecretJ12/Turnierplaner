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
				<Column header="Name" field="name" sortable />
			</DataTable>
			<DataTable
				class="col-8"
				:value="[{ name: 1 }, { name: 2 }, { name: 3 }, { name: 4 }]"
				show-gridlines
				striped-rows
				removable-sort
			>
				<Column header="Player 1" field="name" sortabl>
					<template #body>
						<div class="h-2rem border-dashed" style="margin: -0.5rem; width: calc(100% + 1rem)"></div>
					</template>
				</Column>
				<Column header="Player 2" field="name" sortabl>
					<template #body>
						<div class="h-2rem border-dashed" style="margin: -0.5rem; width: calc(100% + 1rem)"></div>
					</template>
				</Column>
			</DataTable>
		</div>
	</template>

	<div class="grid grid-nogutter justify-content-between mt-4">
		<Button label="Back" icon="pi pi-angle-left" @click="prevPage" />
		<!-- TODO add @click -->
		<Button
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
import { useRoute, useRouter } from "vue-router"
import { useI18n } from "vue-i18n"
import { getCompetitionDetails } from "@/backend/competition"
import { useToast } from "primevue/usetoast"
import { Mode, SignUp } from "@/interfaces/competition"

const route = useRoute()
const router = useRouter()
const toast = useToast()
const { t } = useI18n({ inheritLocale: true })

const competition = getCompetitionDetails(route, t, toast, {
	suc: () => {
		if (competition.value === null) return
	},
})

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
</style>
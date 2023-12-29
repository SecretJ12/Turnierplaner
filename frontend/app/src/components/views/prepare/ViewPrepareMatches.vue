<template>
	<div id="button">
		<Card class="w-12">
			<template #header>
				<TabMenu
					:active-index="activeTab"
					:model="competitions || []"
					@tab-change="tabChange"
				>
					<template #item="{ item, props }">
						<div v-bind="props.action" class="cursor-pointer">
							<span v-bind="props.label">{{ item.name }}</span>
						</div>
					</template>
				</TabMenu>
				<Steps
					:active-step="<number>route.meta.step - 1"
					:model="stepList"
					:readonly="true"
					class="mt-5"
				>
				</Steps>
			</template>
			<template #content>
				<router-view />
			</template>
		</Card>
	</div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from "vue-router"
import { inject, ref } from "vue"
import { useI18n } from "vue-i18n"
import { useToast } from "primevue/usetoast"
import { TabMenuChangeEvent } from "primevue/tabmenu"
import { getListCompetitions } from "@/backend/competition"
import Steps from "primevue/steps"

const { t } = useI18n({ inheritLocale: true })
const router = useRouter()
const route = useRoute()
const toast = useToast()

const isLoggedIn = inject("loggedIn", ref(false))
const competitions = getListCompetitions(route, isLoggedIn, t, toast, {
	suc: () => updateRoute(),
	err: () => {
		router.push("/")
	},
})
const activeTab = ref<number>(0)

function updateRoute(compId?: string) {
	if (competitions.value === null) return

	if (!compId) compId = <string>route.params.compId
	if (compId === "" || !competitions.value.find((c) => c.name === compId))
		compId = competitions.value[0].name

	activeTab.value = competitions.value.findIndex((c) => c.name === compId)

	// TODO insert correct step for compId
	let step = route.name
	if (route.meta.step === undefined || !step) step = "editPlayers"

	router.replace({
		name: step,
		params: { tourId: route.params.tourId, compId },
	})
}

function tabChange(event: TabMenuChangeEvent) {
	if (competitions.value === null) return

	activeTab.value = event.index
	updateRoute(competitions.value[event.index].name)
}

// TODO internalization
const stepList = ref([
	{
		label: "Edit Teams",
		name: "editTeams",
		index: 1,
	},
	{
		label: "Select Tye",
		name: "selectType",
		index: 2,
	},
	{
		label: "Assign Matches",
		name: "assignMatches",
		index: 3,
	},
	{
		label: "Schedule Matches",
		name: "scheduleMatches",
		index: 4,
	},
])
</script>

<style scoped>
#button {
	width: 100%;
	margin: 10px;
	display: flex;
	flex-wrap: wrap;
	flex-direction: row;
	justify-content: center;
}

::v-deep(b) {
	display: block;
}

::v-deep(.p-card-body) {
	padding: 2rem;
}
</style>

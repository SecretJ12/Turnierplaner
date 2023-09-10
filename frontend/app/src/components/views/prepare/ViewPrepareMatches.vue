<template>
	<div id="button">
		<Card>
			<template #header>
				<TabMenu
					:active-index="activeTab"
					:model="competitions || []"
					@tab-change="tabChange"
				>
					<template #item="{ item, props }">
						<div v-bind="props.action" class="cursor-pointer">
							<span v-bind="props.icon" />
							<span v-bind="props.label">{{ item.name }}</span>
						</div>
					</template>
				</TabMenu>
				<Steps
					:model="stepList"
					:readonly="true"
					class="mt-5"
					:pt="{
						menuitem: ({ context }) => ({
							class: isActive(context.item) && 'p-highlight p-steps-current',
						}),
					}"
				>
					>
					<template #item="{ label, index, props }">
						<div v-bind="props.action">
							<span
								v-bind="
									// @ts-expect-error
									props.step
								"
								class="font-green"
								>{{ index + 1 }}</span
							>
							<span v-bind="props.label">{{ label }}</span>
						</div>
					</template>
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

	// TODO insert correct step for compId
	let step = route.name
	if (!route.meta.step || !step) step = "editPlayers"

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
		label: "Edit Players",
		name: "editPlayers",
		index: 1,
	},
	{
		label: "Select Type",
		name: "selectType",
		index: 2,
	},
	{
		label: "Assign Teams",
		name: "assignTeams",
		index: 3,
	},
	{
		label: "Assign Matches",
		name: "assignMatches",
		index: 4,
	},
	{
		label: "Schedule Matches",
		name: "scheduleMatches",
		index: 5,
	},
])

function isActive(item: { index: number }) {
	return item.index === route.meta.step
}
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

.font-green {
	color: green;
}
</style>

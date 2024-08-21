<template>
	<Card class="w-full m-2" style="margin-top: -10px !important">
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
			<router-view v-slot="{ Component }">
				<component
					:is="Component"
					ref="curPrepStep"
					v-model:is-updating="isUpdating"
				/>
			</router-view>

			<div class="mt-2 grid grid-nogutter justify-content-between">
				<Button
					:style="{
						visibility: route.meta.step !== 1 ? 'visible' : 'hidden',
					}"
					:disabled="route.meta.step === 1 || isUpdating"
					:label="t('general.back')"
					icon="pi pi-angle-left"
					icon-pos="left"
					@click="prevPage"
				/>
				<Button
					v-if="route.meta.reset"
					:disabled="isUpdating"
					:label="t('general.reset')"
					severity="danger"
					@click="reset"
				/>
				<Button
					:disabled="isUpdating"
					:label="t('general.save')"
					severity="success"
					@click="save"
				/>
				<Button
					v-if="route.meta.step !== 4"
					:disabled="isUpdating"
					icon="pi pi-angle-right"
					icon-pos="right"
					:label="t('general.next')"
					@click="nextPage"
				/>
				<Button
					v-else
					:disabled="isUpdating"
					label="Complete"
					icon="pi pi-check"
					icon-pos="right"
					class="p-button-success"
					@click="nextPage"
				/>
			</div>
		</template>
	</Card>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from "vue-router"
import { computed, inject, ref, watch } from "vue"
import { useI18n } from "vue-i18n"
import { useToast } from "primevue/usetoast"
import { TabMenuChangeEvent } from "primevue/tabmenu"
import { getCompetitionsList } from "@/backend/competition"
import Steps from "primevue/steps"
import { Progress } from "@/interfaces/competition"
import Button from "primevue/button"
import ViewEditTeams from "@/components/views/prepare/editTeams/ViewEditTeams.vue"

const { t } = useI18n({ inheritLocale: true })

function $t(name: string) {
	return computed(() => t(name))
}

const router = useRouter()
const route = useRoute()
const toast = useToast()

const isUpdating = ref(false)
const curPrepStep = ref<InstanceType<typeof ViewEditTeams> | null>()

const isLoggedIn = inject("loggedIn", ref(false))
const { data: competitions } = getCompetitionsList(route, isLoggedIn, t, toast)
const activeTab = ref<number>(0)
watch([competitions, route], () => updateRoute())
updateRoute()

function updateRoute(compId?: string) {
	if (!competitions.value || competitions.value.length === 0) return

	if (!compId) compId = <string>route.params.compId
	if (compId === "" || !competitions.value.find((c) => c.name === compId))
		compId = competitions.value[0].name

	activeTab.value = competitions.value.findIndex((c) => c.name === compId)

	let step = route.name
	if (route.meta.step === undefined || !step) {
		switch (competitions.value[activeTab.value].cProgress) {
			case Progress.TEAMS:
				step = "editTeams"
				break
			case Progress.GAMES:
				step = "scheduleMatches"
				break
			case Progress.SCHEDULING:
				step = "scheduleMatches"
				break
			default:
				step = "settings"
		}
		step = "editTeams"
	}

	router.replace({
		name: step,
		params: { tourId: route.params.tourId, compId },
	})
}

function tabChange(event: TabMenuChangeEvent) {
	if (!competitions.value) return

	activeTab.value = event.index
	updateRoute(competitions.value[event.index].name)
}

function prevPage() {
	if (curPrepStep.value) curPrepStep.value.prevPage()
}

function reset() {
	if (curPrepStep.value) curPrepStep.value.reset()
}

function save() {
	if (curPrepStep.value) curPrepStep.value.save()
}

function nextPage() {
	if (curPrepStep.value) curPrepStep.value.nextPage()
}

const stepList = ref([
	{
		label: $t("ViewPrepare.steps.settings"),
		name: "settings",
		index: 1,
	},
	{
		label: $t("ViewPrepare.steps.editTeams"),
		name: "editTeams",
		index: 2,
	},
	{
		label: $t("ViewPrepare.steps.assignMatches"),
		name: "assignMatches",
		index: 3,
	},
	{
		label: $t("ViewPrepare.steps.scheduleMatches"),
		name: "scheduleMatches",
		index: 4,
	},
])
</script>

<style scoped>
::v-deep(b) {
	display: block;
}

::v-deep(.p-card-body) {
	padding: 2rem;
}
</style>

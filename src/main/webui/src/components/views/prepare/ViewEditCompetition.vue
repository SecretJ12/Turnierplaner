<template>
	<div class="flex justify-content-center">
		<Card id="card">
			<template #content>
				<FormCompetition
					v-if="!competition || isUpdating"
					:competition="CompetitionDefault"
					:disabled="true"
					@submit="submit"
				/>
				<FormCompetition
					v-else
					ref="form"
					:competition="competition"
					:disabled="false"
					@submit="submit"
				/>
			</template>
		</Card>
	</div>
</template>

<script lang="ts" setup>
import { router } from "@/main"
import { useRoute } from "vue-router"
import FormCompetition from "@/components/views/competitions/FormCompetition.vue"
import { useI18n } from "vue-i18n"
import { CompetitionDefault, CompetitionServer } from "@/interfaces/competition"
import {
	getCompetitionDetails,
	useUpdateCompetition,
} from "@/backend/competition"
import { useToast } from "primevue/usetoast"
import { ref, watch } from "vue"

const { t } = useI18n()
const toast = useToast()

const route = useRoute()
const form = ref<InstanceType<typeof FormCompetition> | null>(null)

const isUpdating = defineModel<boolean>("isUpdating", { default: false })

function sleep(milliseconds: number) {
	return new Promise((resolve) => setTimeout(resolve, milliseconds))
}

const { data: competition } = getCompetitionDetails(route, t, toast)
watch(competition, async () => {
	isUpdating.value = true
	await sleep(100)
	isUpdating.value = false
})

const { mutate } = useUpdateCompetition(route, t, toast, {
	suc(competition) {
		router.replace({
			name: "settings",
			params: { tourId: route.params.tourid, compId: competition.name },
		})
	},
})

function save() {
	form.value?.onSubmit()
}

function submit(server_data: CompetitionServer) {
	mutate(server_data)
}

function nextPage() {
	router.replace({
		name: "editTeams",
		params: { tourId: route.params.tourId, compId: route.params.compId },
	})
}

defineExpose({ save, nextPage })
</script>

<style scoped>
#card {
	width: min(90dvw, 50rem);
}
</style>

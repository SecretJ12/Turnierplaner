<template>
	<div class="w-full flex flex-column align-items-center">
		<Card class="w-full md:w-30rem">
			<template #content>
				<strong>{{ t("general.description") }}:</strong>
				{{ props.competition.description }}<br />
				<strong>{{ t("ViewCompetition.tournament_system") }}:</strong>
				{{ t("CompetitionSettings." + props.competition.tourType.toLowerCase())
				}}<br />
				<strong>{{ t("ViewCompetition.game_mode") }}:</strong>
				{{ t("CompetitionSettings." + props.competition.mode.toLowerCase()) }}
			</template>
		</Card>

		<!-- Show registration is over -->
		<Card class="mt-2 mb-2 w-12 md:w-10 lg:w-8 xl:w-7">
			<template #content>
				<p v-if="!allowRegistration" style="text-align: center">
					{{ t("ViewCompetition.registration_over") }}
				</p>
				<template v-else>
					<ViewSignUpForm
						:begin-game-phase="props.beginGamePhase"
						:competition="props.competition"
						@registered="childUpdate"
					/>

					<ViewRegistrationNotice
						v-if="false"
						:comp-details="props.competition"
					/>
				</template>

				<div class="mt-2">
					<ViewTable
						:competition="props.competition"
						:update="updateChildren"
					/>
				</div>
			</template>
		</Card>

		<!-- TODO add options for admin -->
	</div>
</template>

<script lang="ts" setup>
import { inject, ref, watch } from "vue"
import { auth } from "@/security/AuthService"
import axios from "axios"
import { useRoute } from "vue-router"
import ViewTable from "@/components/views/competition/signup/ViewTable.vue"
import ViewRegistrationNotice from "@/components/views/competition/signup/ViewRegistrationNotice.vue"
import ViewSignUpForm from "@/components/views/competition/signup/ViewSignUpForm.vue"
import { Competition } from "@/interfaces/competition"
import { useI18n } from "vue-i18n"

const { t } = useI18n({ inheritLocale: true })

let windowWidth = ref(window.innerWidth)
window.addEventListener("resize", () => {
	windowWidth.value = window.innerWidth
})

const props = defineProps<{
	allowRegistration: boolean
	beginGamePhase: Date
	competition: Competition
}>()

const route = useRoute()
const isLoggedIn = inject("loggedIn", ref(false))
const canEdit = ref(false)

const updateChildren = ref(0)
watch(isLoggedIn, async () => {
	update()
})
update()

function update() {
	canEdit.value = false
	auth.getUser().then((user) => {
		if (user !== null) {
			axios
				.get<boolean>(`/tournament/${route.params.tourId}/competition/canEdit`)
				.then((response) => {
					canEdit.value = response.data
				})
				.catch(() => {
					canEdit.value = false
				})
		}
	})

	updateChildren.value++
}

function childUpdate() {
	updateChildren.value++
}
</script>

<style scoped></style>
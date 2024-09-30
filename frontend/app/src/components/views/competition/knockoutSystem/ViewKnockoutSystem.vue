<template>
	<p>Knockout system test</p>
	<template v-if="knockoutSystem">
		<ViewKnockoutTree
			:finale="knockoutSystem.finale ?? undefined"
			:third-place="knockoutSystem.thirdPlace ?? undefined"
			:border-radius="0"
			:border-thickness="2"
			:margin-small="30"
			:margin-big="40"
			:titles="knockoutTitle(t)"
		>
			<template #match="{ match }">
				<ViewMatch
					v-if="match"
					:match="match"
					:mode="props.mode"
					@click="showPopUp(match)"
				/>
			</template>
			<template #additional="{ match }">
				<ViewMatchDate v-if="match" :match="match" />
			</template>
		</ViewKnockoutTree>
	</template>
	<UpdatePointsDialog
		v-if="canEdit"
		ref="dialog"
		:number-sets="props.numberSets"
	/>
</template>

<script lang="ts" setup>
import { useRoute } from "vue-router"
import ViewKnockoutTree from "@/components/views/competition/knockoutSystem/ViewKnockoutTree.vue"
import { Mode, NumberSets } from "@/interfaces/competition"
import { getKnockout } from "@/backend/knockout"
import ViewMatch from "@/components/views/competition/knockoutSystem/ViewMatch.vue"
import ViewMatchDate from "@/components/views/competition/knockoutSystem/ViewMatchDate.vue"
import { knockoutTitle } from "@/components/views/competition/knockoutSystem/KnockoutTitleGenerator"
import { useI18n } from "vue-i18n"
import { inject, ref } from "vue"
import { Match } from "@/interfaces/match"
import UpdatePointsDialog from "@/components/items/UpdatePointsDialog.vue"
import { getCanEdit } from "@/backend/security"

const { t } = useI18n({ inheritLocale: true })

const props = defineProps<{
	mode: Mode
	numberSets: NumberSets
}>()

const route = useRoute()
const isLoggedIn = inject("loggedIn", ref(false))
const { data: canEdit } = getCanEdit(<string>route.params.tourId, isLoggedIn)

const { data: knockoutSystem } = getKnockout(route)
const dialog = ref()

const showPopUp = function (match: Match) {
	if (canEdit.value) {
		dialog.value.showPopUp(match)
	}
}
</script>

<style scoped>
table {
	text-align: center;
}
</style>

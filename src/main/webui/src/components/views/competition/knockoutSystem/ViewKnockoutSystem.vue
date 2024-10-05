<template>
	<div class="w-full xl:w-11">
		<Card v-if="knockoutSystem">
			<template #content>
				<div class="w-full flex flex-column align-items-center">
					<HorizontalScroller>
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
									:number-sets="props.numberSets"
									:allow-update="true"
								/>
							</template>
							<template #additional="{ match }">
								<ViewMatchDate v-if="match" :match="match" />
							</template>
						</ViewKnockoutTree>
					</HorizontalScroller>
				</div>
			</template>
		</Card>
	</div>
</template>

<script lang="ts" setup>
import { useRoute } from "vue-router"
import ViewKnockoutTree from "@/components/views/competition/knockoutSystem/ViewKnockoutTree.vue"
import HorizontalScroller from "@/components/items/HorizontalScroller.vue"
import { Mode, NumberSets } from "@/interfaces/competition"
import { getKnockout } from "@/backend/knockout"
import ViewMatch from "@/components/views/competition/knockoutSystem/ViewMatch.vue"
import ViewMatchDate from "@/components/views/competition/knockoutSystem/ViewMatchDate.vue"
import { knockoutTitle } from "@/components/views/competition/knockoutSystem/KnockoutTitleGenerator"
import { useI18n } from "vue-i18n"

const { t } = useI18n()

const props = defineProps<{
	mode: Mode
	numberSets: NumberSets
}>()

const route = useRoute()

const { data: knockoutSystem } = getKnockout(route)
</script>

<style scoped>
table {
	text-align: center;
}
</style>

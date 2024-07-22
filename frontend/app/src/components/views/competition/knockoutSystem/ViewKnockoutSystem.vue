<template>
	<p>Knockout system</p>
	<template v-if="knockoutSystem">
		<ViewKnockoutTree
			:finale="knockoutSystem.finale"
			:third-place="knockoutSystem.thirdPlace"
			:border-radius="0"
			:border-thickness="2"
			:margin-small="30"
			:margin-big="40"
		>
			<template #match="{match}">
				<ViewMatch v-if="match" :match="match" :mode="props.mode" />
			</template>
			<template #additional="{match}">
				<ViewMatchDate v-if="match" :match="match" />
			</template>
		</ViewKnockoutTree>
	</template>
</template>

<script lang="ts" setup>
import { useRoute } from "vue-router"
import ViewKnockoutTree from "@/components/views/competition/knockoutSystem/ViewKnockoutTree.vue"
import { Mode } from "@/interfaces/competition"
import { getKnockout } from "@/backend/knockout"
import ViewMatch from "@/components/views/competition/knockoutSystem/ViewMatch.vue"
import ViewMatchDate from "@/components/views/competition/knockoutSystem/ViewMatchDate.vue"

const props = defineProps<{
	mode: Mode
}>()

const route = useRoute()
const { data: knockoutSystem } = getKnockout(route)
// TODO implement view
</script>

<style scoped>
table {
	text-align: center;
}
</style>

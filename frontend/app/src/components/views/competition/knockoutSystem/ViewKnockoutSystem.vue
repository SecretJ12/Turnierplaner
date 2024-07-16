<template>
	<p>Knockout system</p>
	<template v-if="knockoutSystem">
		<ViewKnockoutTree
			:match="knockoutSystem.finale"
			:third-place="knockoutSystem.thirdPlace"
			:mode="props.mode"
		/>
	</template>
</template>

<script lang="ts" setup>
import { useRoute } from "vue-router"
import ViewKnockoutTree from "@/components/views/competition/knockoutSystem/ViewKnockoutTree.vue"
import { Mode } from "@/interfaces/competition"
import { getKnockout } from "@/backend/knockout"

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

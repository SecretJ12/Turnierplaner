<template>
	<p>Knockout system</p>
	<template v-if="knockoutSystem !== null">
		<ViewKnockoutTree
			:match="knockoutSystem.finale"
			:third-place="knockoutSystem.thirdPlace"
			:mode="props.mode"
		/>
	</template>
</template>

<script lang="ts" setup>
import { useRoute } from "vue-router"
import axios from "axios"
import { ref } from "vue"
import {
	KnockoutSystem,
	KnockoutSystemServer,
	knockoutSystemServerToClient,
} from "@/interfaces/knockoutSystem"
import ViewKnockoutTree from "@/components/views/competition/knockoutSystem/ViewKnockoutTree.vue"
import { Mode } from "@/interfaces/competition"

const props = defineProps<{
	mode: Mode
}>()

const route = useRoute()
const knockoutSystem = ref<KnockoutSystem | null>(null)

await axios
	.get<KnockoutSystemServer>(
		`tournament/${route.params.tourId}/competition/${route.params.compId}/knockoutMatches`,
	)
	.then((response) => {
		knockoutSystem.value = knockoutSystemServerToClient(response.data)
	})
	.catch(() => {})

// TODO implement view
</script>

<style scoped>
table {
	text-align: center;
}
</style>

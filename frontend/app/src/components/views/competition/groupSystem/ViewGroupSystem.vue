<template>
	<!-- TODO show on card -->
	<!-- TODO add toggle to show as list? -->
	<template v-if="groupSystem">
		<ViewGroupTable
			v-for="group in groupSystem.groups"
			:group="group"
			:number-sets="props.numberSets"
		/>
	</template>

	<!-- TODO show finals -->
</template>

<script lang="ts" setup>
import { useRoute } from "vue-router"
import ViewGroupTable from "@/components/views/competition/groupSystem/ViewGroupTable.vue"
import { getGroup } from "@/backend/group"
import { Mode, NumberSets } from "@/interfaces/competition"

const route = useRoute()
const { data: groupSystem } = getGroup(route)
const props = defineProps<{
	numberSets: NumberSets
}>()
</script>

<style>
table {
	margin-bottom: 20px;
}
</style>

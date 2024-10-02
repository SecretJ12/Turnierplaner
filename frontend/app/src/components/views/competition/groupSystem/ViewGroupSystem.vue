<template>
	<!-- TODO add toggle to show as list? -->
	<div v-if="groupSystem" class="lg:m-2 grid">
		<div
			v-for="group in groupSystem.groups"
			:key="group.index"
			class="xl:col-6 col-12"
		>
			<Card>
				<template #content>
					<ViewGroupTable :group="group" :number-sets="props.numberSets" />
					<ViewGroupResults :group="group" />
				</template>
			</Card>
		</div>
	</div>

	<!-- TODO show finals -->
</template>

<script lang="ts" setup>
import { useRoute } from "vue-router"
import ViewGroupTable from "@/components/views/competition/groupSystem/ViewGroupTable.vue"
import { getGroup } from "@/backend/group"
import { NumberSets } from "@/interfaces/competition"
import ViewGroupResults from "@/components/views/competition/groupSystem/ViewGroupResults.vue"

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

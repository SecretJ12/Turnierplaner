<template>
	<!-- TODO show on card -->
	<!-- TODO add toggle to show as list? -->
	<template v-if="groupSystem">
		<ViewGroupTable
			v-for="group in groupSystem.groups"
			:key="group.index"
			:group="group"
			style="width: 1000px"
		/>
	</template>

	<!-- TODO show finals -->
</template>

<script lang="ts" setup>
import { useRoute } from "vue-router"
import ViewGroupTable from "@/components/views/competition/groupSystem/ViewGroupTable.vue"
import { getGroup } from "@/backend/group"

const route = useRoute()
const { data: groupSystem } = getGroup(route)
</script>

<style>
table {
	margin-bottom: 20px;
}
</style>

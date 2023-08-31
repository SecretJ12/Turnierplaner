<template>
	<!-- TODO show on card -->
	<!-- TODO add toggle to show as list? -->
	<ViewGroupTable
		v-if="groupSystem !== undefined"
		v-for="group in groupSystem.groups"
		:key="group.index"
		:group="group"
		style="width: 1000px"
	/>

	<!-- show finals -->
</template>

<script lang="ts" setup>
import { useRoute } from "vue-router"
import axios from "axios"
import { ref } from "vue"
import {
	GroupSystem,
	GroupSystemServer,
	groupSystemServerToClient,
} from "@/interfaces/groupSystem"
import ViewGroupTable from "@/components/views/competition/groupSystem/ViewGroupTable.vue"

const route = useRoute()
const groupSystem = ref<GroupSystem | undefined>()

await axios
	.get<GroupSystemServer>(
		`tournament/${route.params.tourId}/competition/${route.params.compId}/groupMatches`,
	)
	.then((response) => {
		groupSystem.value = groupSystemServerToClient(response.data)
	})
	.catch(() => {})
</script>

<style>
table {
	margin-bottom: 20px;
}
</style>

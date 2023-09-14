<template>
	<TabView>
		<TabPanel header="Version I">
			<V1 />
		</TabPanel>
		<TabPanel header="Version II">
			<V2 />
		</TabPanel>
	</TabView>
</template>

<script setup lang="ts">
import V1 from "@/components/views/prepare/assignTeams/V1.vue";
import V2 from "@/components/views/prepare/assignTeams/V2.vue";
</script>

<style scoped>
</style>
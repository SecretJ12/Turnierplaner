<template>
	<Card class="cursor-pointer" style="width: 25em" @click="selected">
		<!-- TODO internalization -->
		<template #title> Add a new competition</template>
		<template #subtitle> Click here to add a new competition</template>
	</Card>
</template>

<script lang="ts" setup>
const emit = defineEmits(["selected"])

function selected() {
	emit("selected")
}
</script>

<style scoped>
</style>

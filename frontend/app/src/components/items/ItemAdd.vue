<template>
	<Card style="width: 25em" @click="selected">
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
.material-symbols-outlined {
	font-variation-settings:
		"FILL" 0,
		"wght" 700,
		"GRAD" 200,
		"opsz" 48;
}
</style>

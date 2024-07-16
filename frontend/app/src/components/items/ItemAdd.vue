<template>
	<Card class="cursor-pointer md:w-23rem w-full" @click="selected">
		<template #title>{{ props.title }}</template>
		<template #subtitle>{{ props.content }}</template>
	</Card>
</template>

<script lang="ts" setup>
const emit = defineEmits(["selected"])

const props = defineProps<{
	title: string
	content: string
}>()

function selected() {
	emit("selected")
}
</script>

<style scoped></style>

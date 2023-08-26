<template>
	<Card style="width: 25em" @click="selected">
		<template #title> Add a new competition</template>
		<template #subtitle> Click here to add a new competition</template>
		<template #footer>
			<div class="grid grid-nogutter justify-content-between">
				<Button rounded outlined>
					<template #icon>
						<span class="material-symbols-outlined"> add </span>
					</template>
				</Button>
				<i></i>
			</div>
		</template>
	</Card>
</template>

<script lang="ts" setup>
import Button from "primevue/button"

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

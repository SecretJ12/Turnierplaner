<template>
	<Card id="card" @click="selected">
		<template #title>
			{{ name }}
		</template>
		<template #subtitle>
			{{ description }}
		</template>
		<template #footer>
			<div class="grid grid-nogutter justify-content-between">
				<Button v-if="type === 'GROUPS'" text outlined disabled>
					<template #icon>
						<span class="material-symbols-outlined">grid_view</span>
					</template>
				</Button>
				<Button v-else-if="type === 'KNOCKOUT'" disabled text outlined>
					<template #icon>
						<span class="material-symbols-outlined">groups</span>
					</template>
				</Button>
				<Button v-if="canEdit" @click="settings" @click.stop rounded outlined>
					<template #icon>
						<span class="material-icons">settings</span>
					</template>
				</Button>
			</div>
		</template>
	</Card>
</template>

<script lang="ts" setup>
const props = defineProps<{
	name: string
	description: string
	type: string
	canEdit: boolean
}>()

const emit = defineEmits(["selected", "settings"])

function selected() {
	emit("selected", props.name)
}

function settings() {
	emit("settings", props.name)
}
</script>

<style scoped>
#card {
	width: 25em;
	cursor: pointer;
}
</style>

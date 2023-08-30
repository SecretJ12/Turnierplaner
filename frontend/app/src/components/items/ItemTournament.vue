<template>
	<Card id="card" @click="selected">
		<template #title>
			{{ props.tournament.name }}
		</template>
		<template #subtitle>
			{{ props.tournament.description }}
			<!-- TODO entweder nur game phase oder anzeigen was gerade angezeigt wird -->
			<!-- TODO date an den unteren rand des items -->
			<p v-if="new Date() >= props.tournament.registration_phase[1]">
				{{ props.tournament.game_phase.begin.toLocaleDateString() }}
				- {{ props.tournament.game_phase.end.toLocaleDateString() }}
			</p>
			<p v-else>
				{{ props.tournament.registration_phase.begin.toLocaleDateString() }} -
				{{ props.tournament.registration_phase.end.toLocaleDateString() }}
			</p>
		</template>
		<template #footer>
			<div class="grid grid-nogutter justify-content-end">
				<Button v-if="props.canCreate" @click="settings" @click.stop rounded outlined>
					<template #icon>
						<span class="material-icons">settings</span>
					</template>
				</Button>
			</div>
		</template>
	</Card>
</template>

<script lang="ts" setup>
import { Tournament } from "@/interfaces/tournament"
import Button from "primevue/button"

const props = defineProps<{
	canCreate: boolean
	tournament: Tournament
}>()

const emit = defineEmits(["selected", "settings"])

function selected() {
	emit("selected", props.tournament.name)
}

function settings() {
	emit("settings", props.tournament.name)
}
</script>

<style scoped>

#card {
  width: 25em;
  cursor: pointer;
}

</style>

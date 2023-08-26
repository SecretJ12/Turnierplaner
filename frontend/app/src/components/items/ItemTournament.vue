<template>
	<Card style="width: 25em" @click="selected">
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
			<div class="grid grid-nogutter justify-content-between">
				<!-- TODO remove visibility icon?	-->
				<!--				<Button-->
				<!--					v-if="props.canCreate && !props.tournament.visible"-->
				<!--					rounded-->
				<!--					visible-->
				<!--				>-->
				<!--					<template #icon>-->
				<!--						<span class="material-icons">visibility_off</span>-->
				<!--					</template>-->
				<!--				</Button>-->
				<!--				<Button v-else-if="props.canCreate" rounded visible>-->
				<!--					<template #icon>-->
				<!--						<span class="material-icons">visibility</span>-->
				<!--					</template>-->
				<!--				</Button>-->
				<i></i>
				<Button v-if="props.canCreate" @click="settings" rounded outlined>
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

<style scoped></style>

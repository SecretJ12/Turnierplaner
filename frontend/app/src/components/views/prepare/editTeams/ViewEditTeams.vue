<template>
	<div>
		<div v-for="a in playersA">{{ a.value }}</div>
		<br />
		<!--		<div v-for="a in playersB">{{ a.value }}</div>-->
		<div>{{ playersB }}</div>
		<br />
		<p v-for="a in teams">{{ a }}</p>
	</div>
	<Fieldset legend="Team A">
		<DraggablePanel
			:list="playersA"
			:put="['playersA', 'playersB']"
			item-key="id"
			:tag="TransitionGroup"
			:componentData="{
				tag: 'div',
				name: 'default',
				type: 'transition',
			}"
			group="playersA"
			class="flex flex-row flex-wrap gap-2 border-3 min-h-3rem border-round border-dashed"
		>
			<template #default="{ item }">
				<div
					class="border-round select-none bg-primary-400 cursor-pointer inline p-3 text-50"
				>
					{{ item.value }}
				</div>
			</template>
		</DraggablePanel>
	</Fieldset>
	<Fieldset legend="Team B">
		<DraggablePanel
			:list="playersB"
			:put="['playersB', 'playersA']"
			item-key="id"
			:tag="TransitionGroup"
			:componentData="{
				tag: 'div',
				name: 'default',
				type: 'transition',
			}"
			group="playersB"
			class="flex flex-row flex-wrap gap-2 border-3 min-h-3rem border-round border-dashed"
		>
			<template #default="{ item }">
				<div
					class="border-round select-none bg-primary-400 cursor-pointer inline p-3 text-50"
				>
					{{ item.value }}
				</div>
			</template>
		</DraggablePanel>
	</Fieldset>
	<Fieldset legend="team">
		<DraggablePanel
			:list="teams"
			:put="['teams', 'playersA', 'playersB']"
			item-key="id"
			:tag="TransitionGroup"
			:componentData="{
				tag: 'div',
				name: 'default',
				type: 'transition',
			}"
			group="teams"
			class="flex flex-row flex-wrap gap-2 border-3 min-h-3rem border-round border-dashed bg-green-50"
			wrap
		>
			<template #default="{ item: outerItem }">
				<div style="grid-auto-rows: 1fr">
					<DraggablePanel
						:list="outerItem.playerA"
						:put="['playersA']"
						item-key="id"
						:tag="TransitionGroup"
						single
						:component-data="{
							tag: 'div',
							name: 'default',
							type: 'transition',
						}"
						group="playersA"
						class="gap-2 border-3 fle align-items-center bg-blue-50 justify-content-center border-round border-dashed"
						style="min-width: 200px; min-height: 100px"
						@onRemove="cleanUpTeams"
						hook
					>
						<template #default="{ item: innerItem }">
							<div class="border-2">
								{{ innerItem.value }}
							</div>
						</template>
					</DraggablePanel>
					<DraggablePanel
						:list="outerItem.playerB"
						:put="['playersB']"
						item-key="id"
						:tag="TransitionGroup"
						single
						:component-data="{
							tag: 'div',
							name: 'default',
							type: 'transition',
						}"
						group="playersB"
						class="bg-red-50 border-round border-dashed"
						style="min-width: 200px; min-height: 100px"
						@onRemove="cleanUpTeams"
						hook
					>
						<template #default="{ item: innerItem }">
							<div class="border-2 w-full h-full">
								{{ innerItem.value }}
							</div>
						</template>
					</DraggablePanel>
				</div>
			</template>
		</DraggablePanel>
	</Fieldset>
</template>

<script setup lang="ts">
import { Team } from "@/interfaces/match"
import { ref, TransitionGroup } from "vue"
import axios from "axios"
import { useRoute, useRouter } from "vue-router"
import { useToast } from "primevue/usetoast"
import { useI18n } from "vue-i18n"
import { searchedPlayer, TeamArray } from "@/interfaces/player"
import DraggablePanel from "@/draggable/DraggablePanel.vue"
import PlayerCard from "@/components/views/prepare/assignTeams/PlayerCard.vue"

const route = useRoute()
const router = useRouter()
const toast = useToast()
const { t } = useI18n({ inheritLocale: true })

const teams = ref<TeamArray[]>([])
const playersA = ref<searchedPlayer[]>([])
const playersB = ref<searchedPlayer[]>([])

update()

function cleanUpTeams() {
	for (let i = 0; i < teams.value.length; i++) {
		if (
			teams.value[i].playerA.length === 0 &&
			teams.value[i].playerB.length === 0
		) {
			teams.value.splice(i, 1)
			return
		}
	}
	console.log("cleanupped")
}

function update() {
	teams.value = []
	let counter = 0
	axios
		.get<Team[]>(
			`/tournament/${route.params.tourId}/competition/${route.params.compId}/signedUpTeams`,
		)
		.then((response) => {
			response.data.forEach((team) => {
				counter += 1
				if (counter >= 15) {
				} else if (team.playerA !== undefined && team.playerB === undefined) {
					playersA.value.push({
						id: team.playerA.id,
						firstName: team.playerA.firstName,
						lastName: team.playerA.lastName,
						value: team.playerA.firstName + " " + team.playerA.lastName,
					})
				} else if (team.playerA === undefined && team.playerB !== undefined) {
					playersB.value.push({
						id: team.playerB.id,
						firstName: team.playerB.firstName,
						lastName: team.playerB.lastName,
						value: team.playerB.firstName + " " + team.playerB.lastName,
					})
				} else if (team.playerA !== undefined && team.playerB !== undefined) {
					if (Math.random() > 0.5) {
						playersA.value.push({
							id: team.playerA.id,
							firstName: team.playerA.firstName,
							lastName: team.playerA.lastName,
							value: team.playerA.firstName + " " + team.playerA.lastName,
						})
						playersB.value.push({
							id: team.playerB.id,
							firstName: team.playerB.firstName,
							lastName: team.playerB.lastName,
							value: team.playerB.firstName + " " + team.playerB.lastName,
						})
					} else {
						teams.value.push({
							id: team.id,
							playerA: [
								{
									id: team.playerA.id,
									firstName: team.playerA.firstName,
									lastName: team.playerA.lastName,
									value: team.playerA.firstName + " " + team.playerA.lastName,
								},
							],
							playerB: [
								{
									id: team.playerB.id,
									firstName: team.playerB.firstName,
									lastName: team.playerB.lastName,
									value: team.playerB.firstName + " " + team.playerB.lastName,
								},
							],
						})
					}
				}
			})
		})
}
</script>

<style scoped></style>

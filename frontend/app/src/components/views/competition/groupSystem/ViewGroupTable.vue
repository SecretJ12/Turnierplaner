<template>
	<!-- TODO rework, show courts, ... -->
	<table style="width: calc(100% - 1px)">
		<tr>
			<th id="first">
				{{ t("ViewGroupSystem.group") }} {{ props.group.index }}
			</th>
			<template
				v-for="(team, index) in props.group.teams.slice().reverse()"
				:key="index"
			>
				<th
					v-if="index !== props.group.teams.length - 1"
					:class="hoverIdB === index ? 'highlight' : ''"
					@mouseover="headerHover(team.id)"
					@mouseleave="hoverLeave()"
				>
					<ViewTeamNames :team="team" :inverted="true" />
				</th>
			</template>
		</tr>
		<template v-for="(team, index) in props.group.teams" :key="index">
			<tr v-if="index !== props.group.teams.length - 1">
				<th
					:class="hoverIdA === index ? 'highlight ' : ''"
					@mouseover="headerHover(team.id)"
					@mouseleave="hoverLeave()"
				>
					<ViewTeamNames :team="team" :inverted="true" />
				</th>
				<template
					v-for="(teamB, indexB) in props.group.teams.slice().reverse()"
					:key="indexB"
				>
					<td
						v-if="
							indexB !== props.group.teams.length - 1 &&
							index + indexB < props.group.teams.length - 1
						"
						:class="{
							'cursor-pointer': canEdit,
							highlightLow:
								hoverIdA &&
								hoverIdB &&
								((hoverIdA === index && indexB < hoverIdB) ||
									(hoverIdB === indexB && index < hoverIdA)),
							highlight: hoverTeam === team.id || hoverTeam === teamB.id,
						}"
						@mouseover="matchHover(index, indexB)"
						@mouseleave="hoverLeave()"
						@click="showPopUp(findMatch(team, teamB))"
					>
						<div>
							<ViewMatch :match="findMatch(team, teamB)" />
						</div>
					</td>
				</template>
			</tr>
		</template>
	</table>
	<UpdatePointsDialog
		v-if="canEdit"
		ref="dialog"
		:number-sets="props.numberSets"
	/>
</template>

<script setup lang="ts">
import { Group } from "@/interfaces/groupSystem"
import { inject, ref } from "vue"
import { useI18n } from "vue-i18n"
import ViewMatch from "@/components/views/competition/groupSystem/ViewMatch.vue"
import { Match } from "@/interfaces/match"
import { Team } from "@/interfaces/team"
import ViewTeamNames from "@/components/links/LinkTeamNames.vue"
import UpdatePointsDialog from "@/components/items/UpdatePointsDialog.vue"
import { NumberSets } from "@/interfaces/competition"
import { getCanEdit } from "@/backend/security"
import { useRoute } from "vue-router"

const { t } = useI18n({ inheritLocale: true })

const props = defineProps<{
	group: Group
	numberSets: NumberSets
}>()

const route = useRoute()
const isLoggedIn = inject("loggedIn", ref(false))
const { data: canEdit } = getCanEdit(<string>route.params.tourId, isLoggedIn)

function findMatch(teamA: Team, teamB: Team): Match {
	const match: Match | undefined = props.group.matches.find((match) => {
		if (match.teamA === null || match.teamB === null) return false
		if (match.teamA.id === teamA.id && match.teamB.id === teamB.id) return true
		if (match.teamA.id === teamB.id && match.teamB.id === teamA.id) return true
	})
	if (match === undefined) {
		console.error("Match does not exist")
		throw new Error("Match does not exist")
	}
	return match
}

const hoverIdA = ref<null | number>(null)
const hoverIdB = ref<null | number>(null)
const hoverTeam = ref<null | string>(null)

function matchHover(indexA: number, indexB: number) {
	hoverIdA.value = indexA
	hoverIdB.value = indexB
}

function headerHover(team: string | undefined) {
	hoverTeam.value = team || ""
}

function hoverLeave() {
	hoverIdA.value = null
	hoverIdB.value = null
	hoverTeam.value = null
}

const dialog = ref()

function showPopUp(match: Match) {
	if (canEdit.value) dialog.value.showPopUp(match)
}
</script>

<style scoped>
table {
	table-layout: fixed;
	text-align: center;
	border-spacing: 0;
	border-collapse: separate;
	border-top-left-radius: 15px;
	border-top: solid black 1px;
	border-left: solid black 1px;
}

#first {
	width: 150px;
	border-top-left-radius: 15px;
}

th,
td {
	width: 1fr;
	height: 80px;
	border-bottom: solid black 1px;
	border-right: solid black 1px;
}

th {
	background-color: #f0f0f0;
}

td:hover {
	background-color: #dfdfdf;
}

.highlight {
	background-color: #dfdfdf;
}

.highlightLow {
	background-color: #f9f9f9;
}
</style>

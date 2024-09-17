<template>
	<DataTable
		v-model:filters="filters"
		data-key="id"
		resizable-columns
		:loading="!tournament"
		:value="matches"
		filter-display="row"
		:global-filter-fields="['title']"
	>
		<template #empty> No matches found.</template>
		<template #loading> Loading matches...</template>
		<Column
			sortable
			field="title"
			:header="t('general.title')"
			:show-filter-menu="false"
		>
			<template #body="{ data }">
				{{ genTitle(data.title, t) }}
			</template>
			<template #filter="{ filterModel, filterCallback }">
				<InputText
					v-model="filterModel.value"
					size="small"
					type="text"
					class="p-column-filter"
					placeholder="Search by name"
					@input="filterCallback()"
				/>
			</template>
		</Column>
		<Column
			sortable
			field="compName"
			:header="t('general.competition')"
			:show-filter-menu="false"
		>
			<template #filter="{ filterModel, filterCallback }">
				<InputText
					v-model="filterModel.value"
					size="small"
					type="text"
					class="p-column-filter"
					placeholder="Search by name"
					@input="filterCallback()"
				/>
			</template>
		</Column>
		<Column
			sortable
			field="court"
			:header="t('general.court')"
			:show-filter-menu="false"
		>
			<template #filter="{ filterModel, filterCallback }">
				<MultiSelect
					v-model="filterModel.value"
					:options="courts?.map((court) => court.name) || []"
					placeholder="Any"
					class="p-column-filter"
					style="min-width: 14rem"
					:max-selected-labels="3"
					@change="filterCallback()"
				/>
			</template>
		</Column>
		<Column
			sortable
			field="begin"
			:header="t('general.begin')"
			data-type="date"
		>
			<template #body="{ data }">
				{{ data.begin?.toLocaleString(t("lang"), dateOptions) }}
			</template>
			<template #filter="{ filterModel, filterCallback }">
				<Calendar
					v-model="filterModel.value"
					:date-format="t('date_format')"
					:placeholder="t('date_format')"
					:mask="t('date_format')"
					show-time
					@date-select="filterCallback()"
				/>
			</template>
		</Column>
		<Column
			sortable
			field="teamA"
			:header="t('general.team') + ' A'"
			:show-filter-menu="false"
		>
			<template #body="{ data }">
				{{ data.teamA?.playerA?.name }}
				<template v-if="data.teamA?.playerB">
					<br />
					{{ data.teamA?.playerB?.name }}
				</template>
			</template>
			<template #filter="{ filterModel, filterCallback }">
				<InputText
					v-model="filterModel.value"
					size="small"
					type="text"
					class="p-column-filter"
					placeholder="Search by name"
					@input="filterCallback()"
				/>
			</template>
		</Column>
		<Column
			sortable
			field="teamB"
			:header="t('general.team') + ' B'"
			:show-filter-menu="false"
		>
			<template #body="{ data }">
				{{ data.teamB?.playerA?.name }}
				<template v-if="data.teamB?.playerB">
					<br />
					{{ data.teamB?.playerB?.name }}
				</template>
			</template>
			<template #filter="{ filterModel, filterCallback }">
				<InputText
					v-model="filterModel.value"
					size="small"
					type="text"
					class="p-column-filter"
					placeholder="Search by name"
					@input="filterCallback()"
				/>
			</template>
		</Column>
		<!-- TODO add results -->
		<Column :header="t('general.result')" />
	</DataTable>
</template>

<script setup lang="ts">
import { genTitle, getTournamentDetails } from "@/backend/tournament"
import { useRoute } from "vue-router"
import { useI18n } from "vue-i18n"
import { useToast } from "primevue/usetoast"
import { computed, ref } from "vue"
import { getTournamentCourts } from "@/backend/court"
import { FilterMatchMode, FilterService } from "primevue/api"
import { DataTableFilterMeta } from "primevue/datatable"
import { Team } from "@/interfaces/team"
import { AnnotatedMatch } from "@/interfaces/match"
import { getFilteredMatches } from "@/backend/match"

const route = useRoute()
const { t } = useI18n({ inheritLocale: true })
const toast = useToast()
const { data: tournament } = getTournamentDetails(route, t, toast)
const { data: courts } = getTournamentCourts(route)

const TEAMS_FILTER = "TEAMS_FILTER"
const TITLE_FILTER = "TITLE_FILTER"

FilterService.register(TEAMS_FILTER, teamFilter)
FilterService.register(TITLE_FILTER, titleFilter)

const filters = ref<DataTableFilterMeta>({
	title: { value: null, matchMode: TITLE_FILTER },
	compName: { value: null, matchMode: FilterMatchMode.CONTAINS },
	court: { value: null, matchMode: FilterMatchMode.IN },
	begin: { value: null, matchMode: FilterMatchMode.DATE_AFTER },
	teamA: { value: null, matchMode: TEAMS_FILTER },
	teamB: { value: null, matchMode: TEAMS_FILTER },
})

function teamFilter(team: Team | null, filter: string | null) {
	const filterValue = filter?.toLowerCase() || ""
	const playerA = team?.playerA?.name.toLowerCase() || ""
	const playerB = team?.playerB?.name.toLowerCase() || ""
	return playerA.includes(filterValue) || playerB.includes(filterValue)
}
function titleFilter(
	title: AnnotatedMatch["title"] | null,
	filter: string | null,
) {
	const titleValue = title ? genTitle(title, t) : ""
	const filterValue = filter?.toLowerCase() || ""
	return titleValue.toLowerCase().includes(filterValue)
}

const { data: matches } = getFilteredMatches(
	route,
	t,
	computed(() => tournament.value?.game_phase.begin),
	computed(() => tournament.value?.game_phase.end),
	courts,
)

const dateOptions: Intl.DateTimeFormatOptions = {
	year: "2-digit",
	month: "numeric",
	day: "numeric",
	hour: "numeric",
	minute: "numeric",
}
</script>

<style scoped></style>

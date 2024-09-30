<template>
	<DataTable
		v-model:filters="filter"
		:value="playerPage?.data"
		lazy
		:loading="isLoading"
		:total-records="playerPage?.total"
		paginator
		:rows="pageSize"
		size="small"
		filter-display="row"
		@page="(event) => (page = event.page)"
	>
		<Column
			field="name"
			:header="t('general.name')"
			:show-filter-menu="false"
			:show-clear-button="false"
		>
			<template #filter="{ filterModel, filterCallback }">
				<InputText
					v-model="filterModel.value"
					size="small"
					type="text"
					class="p-column-filter"
					:placeholder="t('general.filter_by_name')"
					@input="filterCallback()"
				/>
			</template>
			<template #body="{ data }">
				<LinkPlayerName :player="data" />
			</template>
		</Column>
		<Column field="sex" :header="t('general.sex')">
			<template #body="{ data }">
				<template v-if="(<Player>data).sex">
					{{ t(data.sex === Sex.MALE ? "general.male" : "general.female") }}
				</template>
			</template>
		</Column>
	</DataTable>
</template>

<script setup lang="ts">
import { findPlayers } from "@/backend/player"
import { computed, ref } from "vue"
import { useToast } from "primevue/usetoast"
import { useI18n } from "vue-i18n"
import { Player } from "@/interfaces/player"
import { Sex } from "@/interfaces/competition"
import LinkPlayerName from "@/components/links/LinkPlayerName.vue"

const { t } = useI18n()
const toast = useToast()

const filter = ref({
	name: { value: "", matchMode: "contains" },
})

const page = ref(0)
const pageSize = ref(5)
const { data: playerPage, isLoading: isLoading } = findPlayers(
	computed(() => filter.value.name.value),
	page,
	pageSize,
	t,
	toast,
)
</script>

<style scoped></style>

<template>
	<DataTable :value="player" size="small">
		<Column field="name" :header="t('general.name')" />
		<Column field="sex" :header="t('general.sex')" />
	</DataTable>
</template>

<script setup lang="ts">
import { findPlayers } from "@/backend/player"
import { ref } from "vue"
import { useToast } from "primevue/usetoast"
import { useI18n } from "vue-i18n"

const { t } = useI18n()
const toast = useToast()

const search = ref("")
const page = ref(0)
const pageSize = ref(0)
const { data: player } = findPlayers(search, page, pageSize, t, toast)
</script>

<style scoped></style>

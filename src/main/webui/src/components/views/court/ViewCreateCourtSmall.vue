<template>
	<InputGroup>
		<InputText v-model="name" size="small" />
		<Dropdown
			v-model="type"
			:options="options"
			option-label="name"
			placeholder="Platztyp"
		/>
		<Button size="small" @click="add">Add</Button>
	</InputGroup>
</template>

<script setup lang="ts">
import { ref } from "vue"
import { useCreateCourt } from "@/backend/court"
import { useI18n } from "vue-i18n"
import { courttype } from "@/interfaces/court"

const { t } = useI18n({ inheritLocale: true })
const { mutate: addCourt } = useCreateCourt()

const name = ref("")
const type = ref("HARD")

const options = [
	{ id: "HARD", name: t("court.courtTypes.HARD") },
	{ id: "CLAY", name: t("court.courtTypes.CLAY") },
	{ id: "GRASS", name: t("court.courtTypes.GRASS") },
]

function add() {
	addCourt({
		name: name.value,
		type: courttype.HARD,
	})
}
</script>

<style scoped></style>

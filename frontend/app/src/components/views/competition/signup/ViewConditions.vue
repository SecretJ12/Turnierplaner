<template>
	<div class="flex flex-row gap-2">
		<Tag
			v-if="props.player.sex !== Sex.ANY"
			class="w-min font-medium"
			rounded
			:value="t('CompetitionSettings.' + props.player.sex.toLowerCase())"
		/>
		<Tag
			v-if="props.player.hasMinAge"
			v-tooltip="
				t('ViewCompetition.born_before') +
				' ' +
				(props.player.minAge !== null
					? props.player.minAge.toLocaleString(t('lang'), dateOptions)
					: 'Error')
			"
			class="w-min font-medium"
			:value="generateAboveTag()"
			rounded
		/>
		<Tag
			v-if="props.player.hasMaxAge"
			v-tooltip="
				t('ViewCompetition.born_after') +
				' ' +
				(props.player.maxAge !== null
					? props.player.maxAge.toLocaleString(t('lang'), dateOptions)
					: 'Error')
			"
			class="w-min font-medium"
			:value="generateUnderTag()"
			rounded
		/>
	</div>
</template>

<script lang="ts" setup>
import { useI18n } from "vue-i18n"
import { settingsPlayer, Sex } from "@/interfaces/competition"

const { t } = useI18n({ inheritLocale: true })

const props = defineProps<{
	beginGamePhase: Date
	player: settingsPlayer
}>()

function generateAboveTag() {
	if (props.player.minAge === null) return ""
	const dif =
		props.beginGamePhase.getFullYear() - props.player.minAge.getFullYear()
	return `${t("Player.over")}${dif}`
}

function generateUnderTag() {
	if (props.player.maxAge === null) return ""
	const dif =
		props.beginGamePhase.getFullYear() - props.player.maxAge.getFullYear()
	return `${t("Player.under")}${dif - 1}`
}
const dateOptions: Intl.DateTimeFormatOptions = {
	year: "numeric",
	month: "numeric",
	day: "numeric",
}
</script>

<style scoped></style>

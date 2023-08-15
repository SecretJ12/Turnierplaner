<template>
  <el-space style="margin-bottom: 2px;">
    <el-tag v-if="props.player.sex !== Sex.ANY"
            effect="plain"
            round
            size="small"
    >
      {{ t("CompetitionSettings." + props.player.sex.toLowerCase()) }}
    </el-tag>

    <el-tooltip v-if="props.player.hasMinAge"
                :content="t('ViewCompetition.born_before') + ' '
                    + (props.player.minAge !== null ?
                        props.player.minAge.toLocaleString(t('lang'), dateOptions) : 'Error')"
                class="box-item"
                effect="dark"
                placement="top-start"
    >
      <el-tag
          effect="plain"
          round
          size="small"
      >
        {{ generateAboveTag() }}
      </el-tag>
    </el-tooltip>
    <el-tooltip v-if="props.player.hasMaxAge"
                :content="t('ViewCompetition.born_after') + ' '
                    + (props.player.maxAge !== null ?
                        props.player.maxAge.toLocaleString(t('lang'), dateOptions) : 'Error')"
                class="box-item"
                effect="dark"
                placement="top-start"
    >
      <el-tag
          effect="plain"
          round
          size="small"
      >
        {{ generateUnderTag() }}
      </el-tag>
    </el-tooltip>
  </el-space>
</template>

<script lang="ts" setup>
import {useI18n} from "vue-i18n"
import {settingsPlayer, Sex} from "@/interfaces/competition"

const {t} = useI18n({inheritLocale: true})

const props = defineProps<{
  beginGamePhase: Date,
  player: settingsPlayer
}>()

function generateAboveTag() {
	if (props.player.minAge === null)
		return ""
	const dif = props.beginGamePhase.getFullYear() - props.player.minAge.getFullYear()
	return `Ü${dif}`
}

function generateUnderTag() {
	if (props.player.maxAge === null)
		return ""
	const dif = props.beginGamePhase.getFullYear() - props.player.maxAge.getFullYear()
	return `U${dif - 1}`
}

const dateOptions: Intl.DateTimeFormatOptions = {
	year: "numeric",
	month: "numeric",
	day: "numeric"
}
</script>

<style scoped>

</style>
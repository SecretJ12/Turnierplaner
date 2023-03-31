<template>
    <template v-if="props.player.sex !== 'ANY' || props.player.hasMinAge || props.player.hasMaxAge">
        <span>{{ i18n.global.t("ViewCompetition.requirements") }}:
      <template v-if="props.player.sex !== 'ANY'">
          {{ i18n.global.t("CompetitionSettings." + props.player.sex.toLowerCase()) }}
      </template>
      <template v-if="props.player.sex !== 'ANY'
          && (props.player.hasMinAge || props.player.hasMaxAge)">,
      </template>
      <template v-if="props.player.hasMinAge || props.player.hasMaxAge">
          {{ i18n.global.t("ViewCompetition.born") + " " }}
      </template>
      <template v-if="props.player.hasMaxAge">
          {{ i18n.global.t("ViewCompetition.after") + " " }}
          {{ props.player.minAge.toLocaleString(i18n.global.t('lang'), dateOptions) + " " }}
      </template>
      <template v-if="props.player.hasMinAge && props.player.hasMaxAge">
          {{ i18n.global.t("ViewCompetition.and") + " " }}
      </template>
      <template v-if="props.player.hasMinAge">
          {{ i18n.global.t("ViewCompetition.before") + " " }}
          {{ props.player.maxAge.toLocaleString(i18n.global.t('lang'), dateOptions) }}
      </template>
  </span>
    </template>
</template>

<script setup>

import {i18n} from "@/main";

const props = defineProps({
    player: {
        type: Object,
        sex: String,
        hasMinAge: Boolean,
        minAge: Date,
        hasMaxAge: Boolean,
        maxAge: Date
}})


const dateOptions = {
    year: "numeric",
    month: "numeric",
    day: "numeric"
};
</script>

<style scoped>

</style>
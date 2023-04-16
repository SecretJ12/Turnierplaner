<template>
  <p v-if="!props.match.finished">
    {{ props.match.begin.toLocaleString(t("lang"), dateOptions) }}
  </p>
  <p v-else>
    finished <!-- TODO show standing -->
  </p>
</template>

<script setup lang="ts">
import {Match} from "@/interfaces/match";
import {useI18n} from "vue-i18n"

const {t} = useI18n({inheritLocale: true})

const props = defineProps<{
  match: Match
}>()

const dateOptions: Intl.DateTimeFormatOptions = {
  year: "2-digit",
  month: "numeric",
  day: "numeric",
  hour: "numeric",
  minute: "numeric"
}
</script>

<style scoped>
p {
  text-align: center;
  margin: 0;
}
</style>
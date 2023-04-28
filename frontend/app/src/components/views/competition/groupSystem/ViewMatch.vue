<template>
  <p v-if="props.match.begin > new Date()">
    {{ $t('ViewGroupSystem.start') }}<br/>
    {{ props.match.begin.toLocaleString(t("lang"), dateOptions) }}
  </p>
  <div v-else-if="props.order">
    {{ scoreA }}-{{ scoreB }}
  </div>
  <div v-else>
    {{ scoreB }}-{{ scoreA }}
  </div>
</template>

<script setup lang="ts">
import {Match} from "@/interfaces/match";
import {useI18n} from "vue-i18n"

const {t} = useI18n({inheritLocale: true})

const props = withDefaults(defineProps<{
  match: Match
  order: boolean
}>(), {
  order: true
})

const dateOptions: Intl.DateTimeFormatOptions = {
  year: "2-digit",
  month: "numeric",
  day: "numeric",
  hour: "numeric",
  minute: "numeric"
}

let scoreA = 0
let scoreB = 0
if (props.match.sets !== null)
  for (const set of props.match.sets) {
    if (set.scoreA < set.scoreB) {
      scoreB += 1
    } else if (set.scoreA > set.scoreB) {
      scoreA += 1
    }
  }
</script>

<style scoped>
p {
  text-align: center;
  margin: 0;
}
</style>
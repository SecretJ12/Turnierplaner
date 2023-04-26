<template>
  <table>
    <tr>
      <td class="name">
        Player A
      </td>
      <td class="result"> <!-- TODO show correct results -->
        1
      </td>
      <td class="result">
        2
      </td>
    </tr>
    <tr>
      <td class="name">
        Player B
      </td>
      <td class="result">
        6
      </td>
      <td class="result">
        6
      </td>
    </tr>
  </table>
  <div id="date">
    {{ props.match.begin.toLocaleString(t("lang"), dateOptions) }}
  </div>
</template>

<script setup lang="ts">
import {KnockoutMatch} from "@/interfaces/knockoutSystem";
import {useI18n} from "vue-i18n";

const {t} = useI18n({inheritLocale: true })

const props = defineProps<{
  match: KnockoutMatch
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
table {
  height: 80px;
  width: 300px;
  background-color: #E0E0E0;
  margin: 0;
  border: solid black 2px;
}

td {
  border: solid black 2px;
}

.name {
  text-align: left;
  padding-left: 20px;
}

#date {
  height: 20px;
  text-align: right;
  padding-right: 20px;
  font-style: italic;
}

.result {
  text-align: center;
  width: 30px;
  background-color: #FDFDFD;
}
</style>
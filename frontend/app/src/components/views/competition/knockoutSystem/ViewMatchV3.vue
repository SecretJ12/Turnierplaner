<template>
  <table>
    <tr>
      <td class="name">
        Player A
      </td>
      <td class="result">
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
</template>

<script setup lang="ts">

import {KnockoutMatch} from "@/interfaces/knockoutSystem";

const props = defineProps<{
  match: KnockoutMatch
}>()
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

.result {
  text-align: center;
  width: 30px;
  background-color: #FDFDFD;
}
</style>
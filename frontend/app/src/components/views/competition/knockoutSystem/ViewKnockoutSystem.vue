<template>
  <p>Knockout system</p>
  <template v-if="knockoutSystem !== undefined">
    <ViewKnockoutTree style="width: 60%;" :match="knockoutSystem.finale" />
  </template>
  <p v-if="knockoutSystem !== undefined">
    {{ knockoutSystem.finale }}
    {{ knockoutSystem.thirdPlace }}
  </p>
</template>

<script setup lang="ts">
import {useRoute} from 'vue-router'
import axios from "axios"
import {ref} from "vue"
import {KnockoutSystem, KnockoutSystemServer, knockoutSystemServerToClient} from "@/interfaces/knockoutSystem"
import {useI18n} from "vue-i18n"
import ViewKnockoutTree from "@/components/views/competition/knockoutSystem/ViewKnockoutTree.vue";

const {t} = useI18n({inheritLocale: true })

const route = useRoute()
const knockoutSystem = ref<KnockoutSystem | undefined>()

await axios.get<KnockoutSystemServer>(`tournament/${route.params.tourId}/competition/${route.params.compId}/knockoutMatches`)
    .then((response) => {
      knockoutSystem.value = knockoutSystemServerToClient(response.data)
    })
    .catch((_) => {
    })


// TODO implement view
</script>

<style scoped>
table {
  text-align: center;
}
</style>
<template>
  <p>Knockout system</p>
  <template v-if="knockoutSystem !== null">
    <ViewKnockoutTreeV3 :match="knockoutSystem.finale" :mode="props.mode"/>
  </template>
</template>

<script lang="ts" setup>
import {useRoute} from 'vue-router'
import axios from "axios"
import {ref} from "vue"
import {useI18n} from "vue-i18n"
import {KnockoutSystem, KnockoutSystemServer, knockoutSystemServerToClient} from "@/interfaces/knockoutSystem"
import ViewKnockoutTreeV3 from "@/components/views/competition/knockoutSystem/ViewKnockoutTree.vue"
import {Mode} from "@/interfaces/competition";

const {t} = useI18n({inheritLocale: true})

const props = defineProps<{
  mode: Mode
}>()

const route = useRoute()
const knockoutSystem = ref<KnockoutSystem | null>(null)

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
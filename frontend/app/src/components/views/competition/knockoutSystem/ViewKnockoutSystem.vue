<template>
  <p>Knockout system</p>
  <template v-if="knockoutSystem !== null">
    <el-tabs v-model="currentTab" style="width: 800px;">
      <el-tab-pane label="Tree" name="knockout">
        <ViewKnockoutTree :match="knockoutSystem.finale" />
      </el-tab-pane>
      <el-tab-pane label="TreeV2" name="knockoutv2">
        <ViewKnockoutTreeV2 :match="knockoutSystem.finale" />
      </el-tab-pane>
    </el-tabs>
  </template>
</template>

<script lang="ts" setup>
import {useRoute} from 'vue-router'
import axios from "axios"
import {ref} from "vue"
import {useI18n} from "vue-i18n"
import {KnockoutSystem, KnockoutSystemServer, knockoutSystemServerToClient} from "@/interfaces/knockoutSystem"
import ViewKnockoutTree from "@/components/views/competition/knockoutSystem/ViewKnockoutTree.vue"
import ViewKnockoutTreeV2 from "@/components/views/competition/knockoutSystem/ViewKnockoutTreeV2.vue"

const {t} = useI18n({inheritLocale: true })

const currentTab = ref("knockoutv2")

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
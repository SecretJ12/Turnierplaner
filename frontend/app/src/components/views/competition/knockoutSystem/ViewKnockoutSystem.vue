<template>
  <p>Knockout system</p>
  <template v-if="knockoutSystem !== null">
    <el-tabs v-model="currentTab" style="width: min(90%, 1000px);">
      <el-tab-pane label="Tree" name="knockout">
        <ViewKnockoutTree :match="knockoutSystem.finale"/>
      </el-tab-pane>
      <el-tab-pane label="TreeV2" name="knockoutv2">
        <ViewKnockoutTreeV2 :match="knockoutSystem.finale"/>
      </el-tab-pane>
      <el-tab-pane label="TreeV3" name="knockoutv3">
        <ViewKnockoutTreeV3 :match="knockoutSystem.finale" :mode="props.mode"/>
      </el-tab-pane>
    </el-tabs>
  </template>
</template>

<script lang="ts" setup>
import {useRoute} from "vue-router"
import axios from "axios"
import {ref} from "vue"
import {useI18n} from "vue-i18n"
import {KnockoutSystem, KnockoutSystemServer, knockoutSystemServerToClient} from "@/interfaces/knockoutSystem"
import ViewKnockoutTree from "@/components/views/competition/knockoutSystem/ViewKnockoutTree.vue"
import ViewKnockoutTreeV2 from "@/components/views/competition/knockoutSystem/ViewKnockoutTreeV2.vue"
import ViewKnockoutTreeV3 from "@/components/views/competition/knockoutSystem/ViewKnockoutTreeV3.vue"
import {Mode} from "@/interfaces/competition"

const props = defineProps<{
  mode: Mode
}>()

const currentTab = ref("knockoutv3")

const route = useRoute()
const knockoutSystem = ref<KnockoutSystem | null>(null)

await axios.get<KnockoutSystemServer>(`tournament/${route.params.tourId}/competition/${route.params.compId}/knockoutMatches`)
	.then((response) => {
		knockoutSystem.value = knockoutSystemServerToClient(response.data)
	})
	.catch(() => {
	})


// TODO implement view
</script>

<style scoped>
table {
  text-align: center;
}
</style>
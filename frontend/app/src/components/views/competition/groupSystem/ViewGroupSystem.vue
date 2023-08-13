<template>
  <!-- Only to allow better comparison -->
  <el-tabs v-model="currentTab" style="width: 1000px;">
    <el-tab-pane label="Liste" name="list">
      <ViewGroupList v-for="group in groupSystem.groups" v-if="groupSystem !== undefined" :group="group"/>
    </el-tab-pane>
    <el-tab-pane label="Tabelle" name="table">
      <ViewGroupTable v-for="group in groupSystem.groups" v-if="groupSystem !== undefined" :group="group"/>
    </el-tab-pane>
    <el-tab-pane label="Tabelle V2" name="tablev2">
      <ViewGroupTableV2 class="table" v-for="group in groupSystem.groups" v-if="groupSystem !== undefined"
                        :group="group"/>
    </el-tab-pane>
  </el-tabs>
</template>

<script lang="ts" setup>
import {useRoute} from 'vue-router'
import axios from "axios"
import {ref} from "vue"
import {GroupSystem, GroupSystemServer, groupSystemServerToClient} from "@/interfaces/groupSystem"
import ViewGroupList from "@/components/views/competition/groupSystem/ViewGroupList.vue"
import ViewGroupTable from "@/components/views/competition/groupSystem/ViewGroupTable.vue"
import {useI18n} from "vue-i18n"
import ViewGroupTableV2 from "@/components/views/competition/groupSystem/ViewGroupTableV2.vue"

const {t} = useI18n({inheritLocale: true})

const route = useRoute()
const groupSystem = ref<GroupSystem | undefined>()
const currentTab = ref("tablev2")

await axios.get<GroupSystemServer>(`tournament/${route.params.tourId}/competition/${route.params.compId}/groupMatches`)
    .then((response) => {
      groupSystem.value = groupSystemServerToClient(response.data)
    })
    .catch((_) => {
    })
</script>

<style>
table {
  margin-bottom: 20px;
}
</style>
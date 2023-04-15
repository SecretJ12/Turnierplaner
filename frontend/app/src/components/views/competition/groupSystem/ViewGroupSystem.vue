<template>
    <!-- Only to allow better comparison -->
    <el-tabs v-model="currentTab" style="width: 1000px;">
        <el-tab-pane label="Liste" name="list">
            <ViewGroupList v-if="groupSystem !== undefined" v-for="group in groupSystem.groups" :group="group" />
        </el-tab-pane>
        <el-tab-pane label="Tabelle" name="table">
            <ViewGroupTable v-if="groupSystem !== undefined" v-for="group in groupSystem.groups" :group="group" />
        </el-tab-pane>
    </el-tabs>
</template>

<script setup lang="ts">
import {useRoute} from 'vue-router'
import axios from "axios"
import {ref} from "vue"
import {GroupSystem, GroupSystemServer, groupSystemServerToClient} from "@/interfaces/groupSystem"
import ViewGroupList from "@/components/views/competition/groupSystem/ViewGroupList.vue"
import ViewGroupTable from "@/components/views/competition/groupSystem/ViewGroupTable.vue"
import {useI18n} from "vue-i18n"
const { t } = useI18n({inheritLocale: true})

const route = useRoute()
const groupSystem = ref<GroupSystem | undefined>()
const currentTab = ref("table")

await axios.get<GroupSystemServer>(`tournament/${route.params.tourId}/competition/${route.params.compId}/groupMatches`)
    .then((response) => {
        groupSystem.value = groupSystemServerToClient(response.data)
    })
    .catch((_) => {
    })
</script>


<style>
</style>
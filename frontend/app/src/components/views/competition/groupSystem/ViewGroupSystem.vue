<template>
    <!--<el-tabs v-if="groupSystem !== undefined"
             style="width: 1000px"
             v-model="activeGroup">
        <el-tab-pane v-for="group in groupSystem.groups"
                     :label="'Gruppe ' + (group.index)"
                     :name="group.index">
            <ViewGroup :group="group" />
        </el-tab-pane>
    </el-tabs>-->

    <div v-if="groupSystem !== undefined"
         class="container text-center hover-effect"  v-for="(group,index_group) in groupSystem.groups">
        <table class="table" @click="selected(index_group)">
            <tr class="table-light">
                <th scope="col">
                    {{ $t("ViewGroupSystem.group") }} {{ index_group + 1 }}
                </th>
                <th scope="col" v-for="team in group.teams" id="table-header">
                    {{ team.playerA.firstName }}, {{ team.playerA.lastName }}
                </th>
            </tr>
            <tbody class="table-group-divider">
            <tr v-for="(team,index_t1) in group.teams">
                <th>{{ team.playerA.firstName }}, {{ team.playerA.lastName }}</th>
                <td v-for="(team,index_t2) in group.teams">
                    <div v-if="index_t1===index_t2">
                        /
                    </div>
                    <div v-else>
                        tba
                    </div>
                </td>
            </tr>
            </tbody>
            <caption v-if="progress[index_group]">
                {{ $t("ViewGroupSystem.progress") }}
            </caption>
            <caption v-else>
                {{ $t("ViewGroupSystem.finished") }}
            </caption>
        </table>
    </div>
</template>

<script setup lang="ts">
import {useRoute} from 'vue-router'
import axios from "axios"
import {ref} from "vue"
import {router} from "@/main"
import {GroupSystem, GroupSystemServer, groupSystemServerToClient} from "@/interfaces/groupSystem";
import ViewGroup from "@/components/views/competition/groupSystem/ViewGroup.vue";

const route = useRoute()
const groupSystem = ref<GroupSystem | undefined>()
const activeGroup = ref(1)

await axios.get<GroupSystemServer>(`tournament/${route.params.tourId}/competition/${route.params.compId}/groupMatches`)
    .then((response) => {
        groupSystem.value = groupSystemServerToClient(response.data)
    })
    .catch((_) => {
    })

// TODO update progress array
const progress = [false, true]
function selected(group: any){
    router.push({path: `/tournament/${route.params.tourId}/competition/${route.params.compId}/${group}`})
}
</script>


<style>
</style>
<template>
    <div class="container text-center hover-effect">
        <table class="table">
            <tr class="table-light">
                <th scope="col">
                    {{ t("ViewGroupSystem.group") }} {{ props.group.index }}
                </th>
                <th scope="col" v-for="team in props.group.teams" id="table-header">
                    {{ team.playerA.firstName }}, {{ team.playerA.lastName }}
                </th>
            </tr>
            <tbody class="table-group-divider">
            <tr v-for="(team,index_t1) in props.group.teams">
                <th>{{ team.playerA.firstName }}, {{ team.playerA.lastName }}</th>
                <td v-for="(_,index_t2) in props.group.teams">
                    <div v-if="index_t1===index_t2">
                        /
                    </div>
                    <div v-else>
                        tba
                    </div>
                </td>
            </tr>
            </tbody>
            <caption v-if="progress[props.group.index-1]">
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
import {Group} from "@/interfaces/groupSystem"
import {useI18n} from "vue-i18n"

const {t} = useI18n({inheritLocale: true})

const route = useRoute()
const props = defineProps<{
    group: Group
}>()

// TODO update progress array
const progress = [false, true]
</script>

<style scoped>

</style>
<template>
    <!-- SINGLE -->
    <el-table v-if="props.compDetails.mode === 'SINGLE'" :data="playersA"
              :empty-text="$t('ViewCompetition.no_registration')" border stripe>
        <el-table-column :label="i18n.global.t('general.name')" prop="name" sortable="custom"/>
    </el-table>
    <!-- DOUBLE TOGETHER -->
    <el-table v-else-if="props.compDetails.signup === 'TOGETHER'" :data="teams"
              :empty-text="$t('ViewCompetition.no_registration')" border stripe>
        <el-table-column :label="i18n.global.t('ViewCompetition.playerA')" prop="playerA.name" sortable="custom"/>
        <el-table-column :label="i18n.global.t('ViewCompetition.playerB')" prop="playerB.name" sortable="custom"/>
    </el-table>
    <!-- DOUBLE INDIVIDUAL SAME -->
    <el-table v-else-if="!props.compDetails.playerB.different"
              :empty-text="$t('ViewCompetition.no_registration')" :data="playersA" border stripe>
        <el-table-column :label="i18n.global.t('general.name')" prop="name" sortable="custom"/>
    </el-table>
    <!-- DOUBLE INDIVIDUAL DIFFERENT -->
    <el-row v-else :gutter="20" justify="space-between">
        <el-col :span="12">
            <el-table :data="playersA" :empty-text="$t('ViewCompetition.no_registration')" border stripe>
                <el-table-column :label="i18n.global.t('ViewCompetition.playerA')"
                                 prop="name" sortable="custom"/>
            </el-table>
        </el-col>
        <el-col :span="12">
            <el-table :data="playersB" :empty-text="$t('ViewCompetition.no_registration')" border stripe>
                <el-table-column :label="i18n.global.t('ViewCompetition.playerB')" prop="name" sortable="custom"/>
            </el-table>
        </el-col>
    </el-row>
</template>

<script setup>
import {i18n} from "@/main";
import axios from "axios";
import {ElMessage} from "element-plus";
import {ref, watch} from "vue";
import {useRoute} from "vue-router";

const props = defineProps({
    compDetails: {
        type: Object,
        name: String,
        description: String,
        tourType: String,
        mode: String,
        signup: String,
        playerA: {
            type: Object,
            sex: String,
            hasMinAge: Boolean,
            minAge: Date,
            hasMaxAge: Boolean,
            maxAge: Date
        },
        playerB: {
            type: Object,
            different: Boolean,
            sex: String,
            hasMinAge: Boolean,
            minAge: Date,
            hasMaxAge: Boolean,
            maxAge: Date
        }
    },
    update: Number
})


const route = useRoute()
const teams = ref([])
const playersA = ref([])
const playersB = ref([])

watch(() => props.update, async () => {
    update()
})
update()

function update() {
    axios.get(`/tournament/${route.params.tourId}/competition/${route.params.compId}/signedUpPlayers`)
        .then((response) => {
            if (props.compDetails.mode === 'SINGLE'
                || (props.compDetails.mode === 'DOUBLE' && props.compDetails.signup === 'INDIVIDUAL' && !props.compDetails.playerB.different)) {
                playersA.value = response.data.filter((team) => team.playerA !== null).map((team) => {
                    let player = team.playerA
                    player.name = player.firstName + ' ' + player.lastName
                    return player
                })
            } else if (props.compDetails.mode === 'DOUBLE' && props.compDetails.signup === 'INDIVIDUAL' && props.compDetails.playerB.different) {
                playersA.value = response.data.filter((team) => team.playerA !== null).map((team) => {
                    let player = team.playerA
                    player.name = player.firstName + ' ' + player.lastName
                    return player
                })
                playersB.value = response.data.filter((team) => team.playerB !== null).map((team) => {
                    let player = team.playerB
                    player.name = player.firstName + ' ' + player.lastName
                    return player
                })
            } else if (props.compDetails.mode === 'DOUBLE' && props.compDetails.signup === 'TOGETHER') {
                teams.value = response.data.map((team) => {
                    team.playerA.name = team.playerA.firstName + ' ' + team.playerA.lastName
                    team.playerB.name = team.playerB.firstName + ' ' + team.playerB.lastName
                    return team
                })
            } else {
                ElMessage.error("invalid mode")
            }
        })
        .catch((error) => {
            players.value = []
            ElMessage.error(i18n.global.t("ViewCompetition.query_player_failed"))
            console.log(error)
        })
}
</script>

<style scoped>

</style>
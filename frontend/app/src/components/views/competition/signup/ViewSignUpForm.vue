<template>
  <!-- TODO mobile view (use css grid?) -->
    <!-- SINGLE -->
    <template v-if="props.compDetails.mode === 'SINGLE'">
        <!-- Registration player A -->
        <el-space direction="vertical" fill style="width: 100%;">
            <ViewConditions :player="props.compDetails.playerA" />
            <el-row :gutter="20" class="row-bg" justify="space-between">
                <el-col :span="16">
                    <el-autocomplete
                            v-model="playerSearchA"
                            :fetch-suggestions="queryPlayerA"
                            :placeholder="i18n.global.t('general.name')"
                            hide-loading
                            style="width: 100%"
                            @keyup.enter="signUpSingle"
                    />
                </el-col>
                <el-col :span="8">
                    <el-button
                            style="width: 100%"
                            @click="signUpSingle"
                    >
                        {{ i18n.global.t('general.signUp') }}
                    </el-button>
                </el-col>
            </el-row>
        </el-space>
    </template>
    <!-- DOUBLE TOGETHER -->
    <template v-else-if="props.compDetails.signup === 'TOGETHER'">
        <el-space direction="vertical" fill style="width: 100%;">
            <el-row :gutter="20" class="row-bg" justify="space-between">
                <el-col :span="12">
                    <el-space direction="vertical" fill style="width: 100%;">
                        <!-- Registration player A -->
                        <ViewConditions :player="props.compDetails.playerA" />
                        <el-autocomplete
                                v-model="playerSearchA"
                                :fetch-suggestions="queryPlayerA"
                                :placeholder="i18n.global.t('ViewCompetition.playerA')"
                                hide-loading
                                style="width: 100%"
                        />
                    </el-space>
                </el-col>
                <el-col :span="12">
                    <el-space direction="vertical" fill style="width: 100%;">
                        <!-- Registration player B -->
                        <ViewConditions :player="props.compDetails.playerB" />
                        <el-autocomplete
                                v-model="playerSearchB"
                                :fetch-suggestions="queryPlayerB"
                                :placeholder="i18n.global.t('ViewCompetition.playerB')"
                                hide-loading
                                style="width: 100%"
                        />
                    </el-space>
                </el-col>
            </el-row>
            <el-button
                    style="width: 100%"
                    @click="signUpDoubleTog"
            >
                {{ i18n.global.t('general.signUp') }}
            </el-button>
        </el-space>
    </template>
    <!-- DOUBLE INDIVIDUAL SAME -->
    <template v-else-if="!props.compDetails.playerB.different">
        <el-space direction="vertical" fill style="width: 100%;">
            <!-- Registration player A -->
            <ViewConditions :player="props.compDetails.playerA" />
            <el-row :gutter="20" class="row-bg" justify="space-between">
                <el-col :span="16">
                    <el-autocomplete
                            v-model="playerSearchA"
                            :fetch-suggestions="queryPlayerA"
                            :placeholder="i18n.global.t('general.name')"
                            hide-loading
                            style="width: 100%"
                    />
                </el-col>
                <el-col :span="8">
                    <el-button
                            style="width: 100%"
                            @click="signUpDoubleIndSame"
                    >
                        {{ i18n.global.t('general.signUp') }}
                    </el-button>
                </el-col>
            </el-row>
        </el-space>
    </template>
    <!-- DOUBLE INDIVIDUAL DIFFERENT -->
    <template v-else>
        <el-row :gutter="20" class="row-bg" justify="space-between">
            <el-col :span="12">
                <el-space direction="vertical" fill style="width: 100%;">
                    <!-- Registration player A -->
                    <ViewConditions :player="props.compDetails.playerA" />
                    <el-row :gutter="20" class="row-bg" justify="space-between">
                        <el-col :span="16">
                            <el-autocomplete
                                    v-model="playerSearchA"
                                    :fetch-suggestions="queryPlayerA"
                                    :placeholder="i18n.global.t('ViewCompetition.playerA')"
                                    hide-loading
                                    style="width: 100%"
                            />
                        </el-col>
                        <el-col :span="8">
                            <el-button
                                    style="width: 100%"
                                    @click="signUpDoubleIndDifA"
                            >
                                {{ i18n.global.t('general.signUp') }}
                            </el-button>
                        </el-col>
                    </el-row>
                </el-space>
            </el-col>
            <el-col :span="12">
                <el-space direction="vertical" fill style="width: 100%;">
                    <!-- Registration player B -->
                    <ViewConditions :player="props.compDetails.playerB" />
                    <el-row :gutter="20" class="row-bg" justify="space-between">
                        <el-col :span="16">
                            <el-autocomplete
                                    v-model="playerSearchB"
                                    :fetch-suggestions="queryPlayerB"
                                    :placeholder="i18n.global.t('ViewCompetition.playerB')"
                                    hide-loading
                                    style="width: 100%"
                            />
                        </el-col>
                        <el-col :span="8">
                            <el-button
                                    style="width: 100%"
                                    @click="signUpDoubleIndDifB"
                            >
                                {{ i18n.global.t('general.signUp') }}
                            </el-button>
                        </el-col>
                    </el-row>
                </el-space>
            </el-col>
        </el-row>
    </template>
</template>

<script setup>
import {i18n} from "@/main";
import {defineProps, ref} from "vue";
import axios from "axios";
import {ElMessage} from "element-plus";
import ViewConditions from "@/components/views/competition/signup/ViewConditions.vue";
import {useRoute} from "vue-router";

const route = useRoute()

const emit = defineEmits(['registered'])
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
    }
})

const playerSearchA = ref("")
let queriedPlayerA = []
const playerSearchB = ref("")
let queriedPlayerB = []

function queryPlayerA(search, callback) {
    queriedPlayerA = queriedPlayerA.filter((item) => {
        return item.value.toLowerCase().includes(search.toLowerCase())
    })
    callback(queriedPlayerA)
    axios.get(`/player/find?search=${search}`
        + (props.compDetails.playerA.sex !== 'ANY' ? `&sex=${props.compDetails.playerA.sex}` : '')
        + (props.compDetails.playerA.hasMinAge ? `&minAge=${props.compDetails.playerA.minAge.toISOString().slice(0, 10)}` : '')
        + (props.compDetails.playerA.hasMaxAge ? `&minAge=${props.compDetails.playerA.maxAge.toISOString().slice(0, 10)}` : ''))
        .then((result) => {
            queriedPlayerA = result.data.map((item) => {
                item.value = item.firstName + ' ' + item.lastName
                return item
            })
            callback(queriedPlayerA)
        })
        .catch((error) => {
            ElMessage.error(i18n.global.t("ViewCompetition.query_search_failed"))
            console.log(error)
        })
}
function queryPlayerB(search, callback) {
    queriedPlayerB = queriedPlayerB.filter((item) => {
        return item.value.toLowerCase().includes(search.toLowerCase())
    })
    callback(queriedPlayerB)
    axios.get(`/player/find?search=${search}`
        + (props.compDetails.playerB.sex !== 'ANY' ? `&sex=${props.compDetails.playerB.sex}` : '')
        + (props.compDetails.playerB.hasMinAge ? `&minAge=${props.compDetails.playerB.minAge.toISOString().slice(0, 10)}` : '')
        + (props.compDetails.playerB.hasMaxAge ? `&minAge=${props.compDetails.playerB.maxAge.toISOString().slice(0, 10)}` : ''))
        .then((result) => {
            queriedPlayerB = result.data.map((item) => {
                item.value = item.firstName + ' ' + item.lastName
                return item
            })
            callback(queriedPlayerB)
        })
        .catch((error) => {
            ElMessage.error(i18n.global.t("ViewCompetition.query_search_failed"))
            console.log(error)
        })
}

function signUpPlayer(queriedPlayer, playerSearch, playerName) {
    const validPlayers = queriedPlayer.filter((p) => {
        return p.value.includes(playerSearch.value)
    })
    if (validPlayers.length > 1) {
        ElMessage.error(i18n.global.t("Player.too_many_results"))
        return
    }
    if (validPlayers.length === 0) {
        ElMessage.error(i18n.global.t("Player.no_result"))
        return
    }
    const player = validPlayers[0]


    const form = {}
    form[playerName] = {
        firstName: player.firstName,
        lastName: player.lastName
    }

    axios.post(`/tournament/${route.params.tourId}/competition/${route.params.compId}/signUp`, form)
        .then((_) => {
            ElMessage.success(i18n.global.t("Player.register_success"))
            emit('registered', '')
        })
        .catch((error) => {
            if (error.response.status === 409)
                ElMessage.error(i18n.global.t("Player.already_exists"))
            else
                ElMessage.error(i18n.global.t("Player.register_failed"))
        })
}
function signUpSingle() {
    signUpPlayer(queriedPlayerA, playerSearchA, "playerA")
}
function signUpDoubleIndSame(){
    signUpPlayer(queriedPlayerA, playerSearchA, "playerA")
}
function signUpDoubleIndDifA() {
    signUpPlayer(queriedPlayerA, playerSearchA, "playerA")
}
function signUpDoubleIndDifB() {
    signUpPlayer(queriedPlayerB, playerSearchB, "playerB")
}
function signUpDoubleTog() {
    const validPlayersA = queriedPlayerA.filter((p) => {
        return p.value.includes(playerSearchA.value)
    })
    if (validPlayersA.length > 1) {
        ElMessage.error(i18n.global.t("Player.too_many_results"))
        return
    }
    if (validPlayersA.length === 0) {
        ElMessage.error(i18n.global.t("Player.no_result"))
        return
    }
    const playerA = validPlayersA[0]

    const validPlayersB = queriedPlayerB.filter((p) => {
        return p.value.includes(playerSearchB.value)
    })
    if (validPlayersB.length > 1) {
        ElMessage.error(i18n.global.t("Player.too_many_results"))
        return
    }
    if (validPlayersB.length === 0) {
        ElMessage.error(i18n.global.t("Player.no_result"))
        return
    }
    const playerB = validPlayersB[0]

    const form = {
        playerA: {
            firstName: playerA.firstName,
            lastName: playerA.lastName
        },
        playerB: {
            firstName: playerB.firstName,
            lastName: playerB.lastName
        }
    }

    axios.post(`/tournament/${route.params.tourId}/competition/${route.params.compId}/signUp`, form)
        .then((_) => {
            ElMessage.success(i18n.global.t("Player.register_success"))
            emit('registered', '')
        })
        .catch((error) => {
            if (error.response.status === 409)
                ElMessage.error(i18n.global.t("Player.already_exists"))
            else
                ElMessage.error(i18n.global.t("Player.register_failed"))
        })
}
</script>

<style scoped>

</style>
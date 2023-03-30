<template>
    <el-space direction="vertical" fill>
        <!-- Show registration is over -->
        <p v-if="!allowRegistration" style="text-align: center">
            {{ i18n.global.t("ViewCompetition.registration_over") }}
        </p>
        <template v-else>
            <!-- Notice to register -->
            <el-row :gutter="20" class="row-bg" justify="space-between">
                <el-col :span="16">
                    <span id="notice_register">{{ $t('ViewPlayerRegistration.not_found') }}</span>
                </el-col>
                <el-col :span="8">
                    <el-button
                        style="width: 100%"
                        @click="playerRegistration"
                    >
                        {{ i18n.global.t('general.register') }}
                    </el-button>
                </el-col>
            </el-row>

            <!-- Registration player A -->
            <span>{{ i18n.global.t("ViewCompetition.requirements") }}:
                <template v-if="compDetails.playerA.sex !== 'ANY'">
                    {{ i18n.global.t("CompetitionSettings." + compDetails.playerA.sex.toLowerCase()) }}
                </template>
                <template v-if="compDetails.playerA.sex !== 'ANY'
                    && (compDetails.playerA.hasMinAge || compDetails.playerA.hasMaxAge)">,
                </template>
                <template v-if="compDetails.playerA.hasMinAge || compDetails.playerA.hasMaxAge">
                    {{ i18n.global.t("ViewCompetition.born") + " " }}
                </template>
                <template v-if="compDetails.playerA.hasMaxAge">
                    {{ i18n.global.t("ViewCompetition.after") + " " }}
                    {{ compDetails.playerA.minAge.toLocaleString(i18n.global.t('lang'), dateOptions) + " " }}
                </template>
                <template v-if="compDetails.playerA.hasMinAge && compDetails.playerA.hasMaxAge">
                    {{ i18n.global.t("ViewCompetition.and") + " " }}
                </template>
                <template v-if="compDetails.playerA.hasMinAge">
                    {{ i18n.global.t("ViewCompetition.before") + " " }}
                    {{ compDetails.playerA.maxAge.toLocaleString(i18n.global.t('lang'), dateOptions) }}
                </template>
            </span>
            <el-row :gutter="20" class="row-bg" justify="space-between">
                <el-col :span="16">
                    <el-autocomplete
                        v-model="playerSearchA"
                        :fetch-suggestions="queryPlayerA"
                        :placeholder="i18n.global.t('general.name')"
                        hide-loading
                        style="width: 100%"
                        @keyup.enter="signUp"
                    />
                </el-col>
                <el-col :span="8">
                    <el-button
                        style="width: 100%"
                        @click="signUp"
                    >
                        {{ i18n.global.t('general.signUp') }}
                    </el-button>
                </el-col>
            </el-row>
        </template>
        <el-row :gutter="20" class="row-bg" justify="center">
            <el-col :span="24">
                <el-table :data="playersA" :empty-text="$t('ViewCompetition.no_registration')" border stripe>
                    <el-table-column :label="i18n.global.t('general.name')" prop="name" sortable="custom"/>
                </el-table>
            </el-col>
            <!-- TODO lists for other view types -->
        </el-row>
        <!-- TODO add delete for admin -->
    </el-space>
</template>

<script setup>
import {inject, ref, watch} from "vue"
import {i18n, router} from '@/main'
import {auth} from "@/security/AuthService";
import axios from "axios";
import {useRoute} from "vue-router";
import {ElMessage} from "element-plus";

const props = defineProps({
    allowRegistration: Boolean,
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

const route = useRoute()
const isLoggedIn = inject('loggedIn', ref(false))
const canEdit = ref(false)

const teams = ref([])
const playersA = ref([])
const playersB = ref([])

watch(isLoggedIn, async () => {
  update()
})
update()

function update() {
  canEdit.value = false
  auth.getUser().then((user) => {
    if (user !== null) {
      axios.get(`/tournament/${route.params.tourId}/competition/canEdit`)
          .then((response) => {
            canEdit.value = response.data
          })
          .catch((_) => {
            canEdit.value = false
          })
    }
  });
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

const playerSearchA = ref("")
let queriedPlayerA = []

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

function signUp() {
    const validPlayers = queriedPlayerA.filter((p) => {
        return p.value.includes(playerSearchA.value)
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

  const form = {
    firstName: player.firstName,
    lastName: player.lastName
  }
  axios.post(`/tournament/${route.params.tourId}/competition/${route.params.compId}/signUp`, form)
      .then((_) => {
        ElMessage.success("Player.register_success")
        update()
      })
      .catch((error) => {
        if (error.response.status === 409)
          ElMessage.error(i18n.global.t("Player.already_exists"))
        else
          ElMessage.error(i18n.global.t("Player.register_failed"))
      })

}

function playerRegistration() {
  router.push({path: "/player/registration"})
}

const dateOptions = {
    year: "numeric",
    month: "numeric",
    day: "numeric"
};
</script>

<style scoped>
#notice_register {
  font-size: 14px;
  color: #404040;
}
</style>
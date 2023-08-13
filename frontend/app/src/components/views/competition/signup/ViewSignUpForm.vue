<template>
  <!-- SINGLE -->
  <template v-if="props.competition.mode === Mode.SINGLE">
    <!-- Registration player A -->
    <el-space direction="vertical" fill style="width: 100%;">
      <ViewConditions :beginGamePhase="props.beginGamePhase" :player="props.competition.playerA"/>
      <el-row :gutter="20" class="row-bg" justify="space-between">
        <el-col :span="16">
          <el-autocomplete
              v-model="playerSearchA"
              :fetch-suggestions="queryPlayerA"
              :placeholder="t('general.name')"
              hide-loading
              style="width: 100%"
          />
        </el-col>
        <el-col :span="8">
          <el-button
              style="width: 100%"
              @click="signUpSingle"
          >
            {{ t('general.signUp') }}
          </el-button>
        </el-col>
      </el-row>
    </el-space>
  </template>
  <!-- DOUBLE TOGETHER -->
  <template v-else-if="props.competition.signUp === SignUp.TOGETHER">
    <el-space direction="vertical" fill style="width: 100%;">
      <el-row :gutter="20" class="row-bg" justify="space-between">
        <el-col :span="12">
          <ViewConditions :beginGamePhase="props.beginGamePhase" :player="props.competition.playerA"/>
        </el-col>
        <el-col :span="12">
          <ViewConditions v-if="props.competition.playerB.different" :beginGamePhase="props.beginGamePhase" :player="props.competition.playerB"/>
          <ViewConditions v-else :beginGamePhase="props.beginGamePhase" :player="props.competition.playerA"/>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="row-bg" justify="space-between">
        <el-col :span="12">
          <el-autocomplete
              v-model="playerSearchA"
              :fetch-suggestions="queryPlayerA"
              :placeholder="t('ViewCompetition.playerA')"
              hide-loading
              style="width: 100%"
          />
        </el-col>
        <el-col :span="12">
          <el-autocomplete
              v-model="playerSearchB"
              :fetch-suggestions="queryPlayerB"
              :placeholder="t('ViewCompetition.playerB')"
              hide-loading
              style="width: 100%"
          />
        </el-col>
      </el-row>
      <el-button
          style="width: 100%"
          @click="signUpDoubleTog"
      >
        {{ t('general.signUp') }}
      </el-button>
    </el-space>
  </template>
  <!-- DOUBLE INDIVIDUAL SAME -->
  <template v-else-if="!props.competition.playerB.different">
    <el-space direction="vertical" fill style="width: 100%;">
      <!-- Registration player A -->
      <ViewConditions :beginGamePhase="props.beginGamePhase" :player="props.competition.playerA"/>
      <el-row :gutter="20" class="row-bg" justify="space-between">
        <el-col :span="16">
          <el-autocomplete
              v-model="playerSearchA"
              :fetch-suggestions="queryPlayerA"
              :placeholder="t('general.name')"
              hide-loading
              style="width: 100%"
          />
        </el-col>
        <el-col :span="8">
          <el-button
              style="width: 100%"
              @click="signUpDoubleIndSame"
          >
            {{ t('general.signUp') }}
          </el-button>
        </el-col>
      </el-row>
    </el-space>
  </template>
  <!-- DOUBLE INDIVIDUAL DIFFERENT -->
  <template v-else>
    <div id="regDoubIndDif">
      <ViewConditions id="regDoubIndDifCondA"
                      :beginGamePhase="props.beginGamePhase" :player="props.competition.playerA"/>
      <el-row id="regDoubIndDifRegA" :gutter="20" class="row-bg" justify="space-between">
        <el-col :span="16">
          <el-autocomplete
              v-model="playerSearchA"
              :fetch-suggestions="queryPlayerA"
              :placeholder="t('ViewCompetition.playerA')"
              hide-loading
              style="width: 100%"
          />
        </el-col>
        <el-col :span="8">
          <el-button
              style="width: 100%"
              @click="signUpDoubleIndDifA"
          >
            {{ t('general.signUp') }}
          </el-button>
        </el-col>
      </el-row>

      <ViewConditions id="regDoubIndDifCondB"
                      :beginGamePhase="props.beginGamePhase" :player="props.competition.playerB"/>
      <el-row id="regDoubIndDifRegB" :gutter="20" class="row-bg" justify="space-between">
        <el-col :span="16">
          <el-autocomplete
              v-model="playerSearchB"
              :fetch-suggestions="queryPlayerB"
              :placeholder="t('ViewCompetition.playerB')"
              hide-loading
              style="width: 100%"
          />
        </el-col>
        <el-col :span="8">
          <el-button
              style="width: 100%"
              @click="signUpDoubleIndDifB"
          >
            {{ t('general.signUp') }}
          </el-button>
        </el-col>
      </el-row>
    </div>
  </template>
</template>

<script lang="ts" setup>
import {ref} from "vue"
import axios from "axios"
import {ElMessage} from "element-plus"
import ViewConditions from "@/components/views/competition/signup/ViewConditions.vue"
import {useRoute} from "vue-router"
import {Competition, Mode, Sex, SignUp} from "@/interfaces/competition"
import {Player, searchedPlayer} from "@/interfaces/player"
import {useI18n} from "vue-i18n"
import {Team} from "@/interfaces/registration/team"

const {t} = useI18n({inheritLocale: true})

const route = useRoute()

const emit = defineEmits(['registered'])
const props = defineProps<{
  beginGamePhase: Date,
  competition: Competition
}>()

const playerSearchA = ref("")
let queriedPlayerA: searchedPlayer[] = []
const playerSearchB = ref("")
let queriedPlayerB: searchedPlayer[] = []

function queryPlayerA(search: string, callback: (player: searchedPlayer[]) => void) {
  queriedPlayerA = queriedPlayerA.filter((item) => {
    return item.value.toLowerCase().includes(search.toLowerCase())
  })
  callback(queriedPlayerA)
  if (props.competition.playerA.hasMinAge && props.competition.playerA.minAge === null) {
    console.log("Data invalid")
    return
  }
  axios.get<Player[]>(`/player/find?search=${search}`
      + (props.competition.playerA.sex !== Sex.ANY ? `&sex=${props.competition.playerA.sex}` : '')
      + (props.competition.playerA.hasMinAge && props.competition.playerA.minAge !== null ?
          `&minAge=${props.competition.playerA.minAge.toISOString().slice(0, 10)}` : '')
      + (props.competition.playerA.hasMaxAge && props.competition.playerA.maxAge !== null ?
          `&minAge=${props.competition.playerA.maxAge.toISOString().slice(0, 10)}` : ''))
      .then((result) => {
        queriedPlayerA = result.data.map((item) => {
          return {
            id: item.id,
            firstName: item.firstName,
            lastName: item.lastName,
            value: item.firstName + ' ' + item.lastName
          }
        })
        callback(queriedPlayerA)
      })
      .catch((error) => {
        ElMessage.error(t("ViewCompetition.query_search_failed"))
        console.log(error)
      })
}

function queryPlayerB(search: string, callback: (player: Player[]) => void) {
  queriedPlayerB = queriedPlayerB.filter((item) => {
    return item.value.toLowerCase().includes(search.toLowerCase())
  })
  callback(queriedPlayerB)
  axios.get<Player[]>(`/player/find?search=${search}`
      + (props.competition.playerB.sex !== 'ANY' ? `&sex=${props.competition.playerB.sex}` : '')
      + (props.competition.playerB.hasMinAge && props.competition.playerB.minAge !== null ?
          `&minAge=${props.competition.playerB.minAge.toISOString().slice(0, 10)}` : '')
      + (props.competition.playerB.hasMaxAge && props.competition.playerB.maxAge !== null ?
          `&minAge=${props.competition.playerB.maxAge.toISOString().slice(0, 10)}` : ''))
      .then((result) => {
        queriedPlayerB = result.data.map((item) => {
          return {
            id: item.id,
            firstName: item.firstName,
            lastName: item.lastName,
            value: item.firstName + ' ' + item.lastName
          }
        })
        callback(queriedPlayerB)
      })
      .catch((error) => {
        ElMessage.error(t("ViewCompetition.query_search_failed"))
        console.log(error)
      })
}

function signUpPlayer(queriedPlayer: searchedPlayer[], playerSearch: string, playerA: boolean) {
  const validPlayers = queriedPlayer.filter((p) => {
    return p.value.includes(playerSearch)
  })
  if (validPlayers.length > 1) {
    ElMessage.error(t("Player.too_many_results"))
    return
  }
  if (validPlayers.length === 0) {
    ElMessage.error(t("Player.no_result"))
    return
  }
  const player = validPlayers[0]

  const form: Team = {}
  if (playerA)
    form["playerA"] = {
      firstName: player.firstName,
      lastName: player.lastName
    }
  else
    form["playerB"] = {
      firstName: player.firstName,
      lastName: player.lastName
    }

  axios.post(`/tournament/${route.params.tourId}/competition/${route.params.compId}/signUp`, form)
      .then((_) => {
        ElMessage.success(t("Player.register_success"))
        emit('registered', '')
      })
      .catch((error) => {
        if (error.response.status === 409)
          ElMessage.error(t("Player.already_exists"))
        else
          ElMessage.error(t("Player.register_failed"))
      })
}

function signUpSingle() {
  signUpPlayer(queriedPlayerA, playerSearchA.value, true)
}

function signUpDoubleIndSame() {
  signUpPlayer(queriedPlayerA, playerSearchA.value, true)
}

function signUpDoubleIndDifA() {
  signUpPlayer(queriedPlayerA, playerSearchA.value, true)
}

function signUpDoubleIndDifB() {
  signUpPlayer(queriedPlayerB, playerSearchB.value, false)
}

function signUpDoubleTog() {
  const validPlayersA = queriedPlayerA.filter((p) => {
    return p.value.includes(playerSearchA.value)
  })
  if (validPlayersA.length > 1) {
    ElMessage.error(t("Player.too_many_results"))
    return
  }
  if (validPlayersA.length === 0) {
    ElMessage.error(t("Player.no_result"))
    return
  }
  const playerA = validPlayersA[0]

  const validPlayersB = queriedPlayerB.filter((p) => {
    return p.value.includes(playerSearchB.value)
  })
  if (validPlayersB.length > 1) {
    ElMessage.error(t("Player.too_many_results"))
    return
  }
  if (validPlayersB.length === 0) {
    ElMessage.error(t("Player.no_result"))
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
        ElMessage.success(t("Player.register_success"))
        emit('registered', '')
      })
      .catch((error) => {
        if (error.response.status === 409)
          ElMessage.error(t("Player.already_exists"))
        else
          ElMessage.error(t("Player.register_failed"))
      })
}
</script>

<style scoped>
#regDoubIndDif {
  display: grid;
  grid-template-columns: 1fr 20px 1fr;
  grid-template-rows: auto 2px auto;
}

#regDoubIndDifCondA {
  grid-column: 1;
  grid-row: 1;
}

#regDoubIndDifRegA {
  grid-column: 1;
  grid-row: 3;
}

#regDoubIndDifCondB {
  grid-column: 3;
  grid-row: 1;
}

#regDoubIndDifRegB {
  grid-column: 3;
  grid-row: 3;
}

@media only screen and (max-width: 750px) {
  #regDoubIndDif {
    grid-template-columns: 1fr;
    grid-template-rows: auto auto auto auto;
  }

  #regDoubIndDifCondA {
    grid-column: 1;
    grid-row: 1;
  }

  #regDoubIndDifRegA {
    grid-column: 1;
    grid-row: 2;
    margin-bottom: 10px;
  }

  #regDoubIndDifCondB {
    grid-column: 1;
    grid-row: 3;
  }

  #regDoubIndDifRegB {
    grid-column: 1;
    grid-row: 4;
  }
}
</style>
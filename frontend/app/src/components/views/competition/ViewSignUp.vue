<template>
  <el-space direction="vertical" fill>
    <el-row id="" :gutter="20" class="row-bg" justify="space-between">
      <el-col :span="16">
        <el-autocomplete
            v-model="playerSearch"
            :fetch-suggestions="queryPlayer"
            :placeholder="i18n.global.t('general.name')"
            @keyup.enter="signUp"
            hide-loading
            style="width: 100%"
        />
      </el-col>
      <el-col :span="8" >
        <el-button
            style="width: 100%"
            @click="signUp"
        >
          {{ i18n.global.t('general.signUp') }}
        </el-button>
      </el-col>
    </el-row>
    <el-row id="" :gutter="20" class="row-bg" justify="space-between">
      <el-col :span="16">
        <el-text class="mx-1" size="small" type="danger" >{{ $t('ViewPlayerRegistration.not_found') }}</el-text>
<!--TODO only show warning when text is empty? And smaller in red?-->
<!-- Why does text formatting not work above??? -->
      </el-col>
      <el-col :span="8" >
        <el-button
            style="width: 100%"
            @click="playerRegistration"
        >
          {{ i18n.global.t('general.register') }}
        </el-button>
      </el-col>
    </el-row>
    <el-row :gutter="20" class="row-bg" justify="center">
      <el-col :span="24">
        <el-table stripe border :data="players" :empty-text="$t('ViewCompetition.no_registration')">
          <el-table-column sortable prop="name" :label="i18n.global.t('general.name')" />
          <!-- TODO add delete for admin -->
        </el-table>
      </el-col>
    </el-row>
  </el-space>
</template>

<script setup>
import {inject, ref, watch} from "vue"
import {i18n, router} from '@/main'
import {auth} from "@/security/AuthService";
import axios from "axios";
import { useRoute } from "vue-router";
import { ElMessage } from "element-plus";

const route = useRoute()
const isLoggedIn = inject('loggedIn', ref(false))
const canEdit = ref(false)

const players = ref([])

watch (isLoggedIn, async () => {
  update()
})
update()

function update() {
  canEdit.value = false
  auth.getUser().then((user) => {
    if (user !== null) {
      axios.get('/competition/canEdit')
        .then((response) => {
          canEdit.value = response.status === 200
        })
        .catch((error) => {
          canEdit.value = false
          console.log(error)
        })
    }
  });
  axios.get(`/competition/signedUpPlayers?tourName=${route.params.tourId}&compName=${route.params.compId}`)
    .then((response) => {
      if (response.status !== 200)
        players.value = []
      else
        players.value = response.data.map((player) => {
          player.name = player.firstName + ' ' + player.lastName
          return player
        })
    })
    .catch((error) => {
      ElMessage.error(i18n.global.t("ViewCompetition.query_player_failed"))
      console.log(error)
    })
}

const playerSearch = ref("")

let queriedPlayer = []
function queryPlayer(search, callback) {
  queriedPlayer = queriedPlayer.filter((item) => {
    return item.value.includes(search)
  })
  callback(queriedPlayer)
  axios.get(`/player/players?search=${search}`)
    .then((result) => {
      queriedPlayer = result.data.map((item) => {
        item.value = item.firstName + ' ' + item.lastName
        return item
      })
      callback(queriedPlayer)
    })
    .catch((error) => {
      ElMessage.error(i18n.global.t("ViewCompetition.query_search_failed"))
      console.log(error)
    })
}

function signUp() {
  const validPlayers = queriedPlayer.filter((p) => {
    return p.value.includes(playerSearch.value)
  })
  if (validPlayers.length > 1) {
    ElMessage.error("zu viele ergebnisse") // TODO i18n
    return
  }
  if (validPlayers.length === 0) {
    ElMessage.error("niemanden gefunden") // TODO i18n
    return
  }
  const player = validPlayers[0]

  const form = {
    firstName: player.firstName,
    lastName: player.lastName,
    tourName: route.params.tourId,
    compName: route.params.compId
  }
  axios.post(`/competition/signUp`, form)
      .then((response) => {
        if (response.status === 200)
          ElMessage.success("erfolgreich erstellt") // TODO i18n
        else
          ElMessage.error("ne irgendwie passt da was nich") // TODO i18n
        update()
      })
      .catch((error) => {
        console.log(error)
        ElMessage.error("ne irgendwie passt da was nich") // TODO i18n
      })

}
function playerRegistration(){
  router.push({path: "/player/registration"})
}

</script>

<style scoped>

</style>
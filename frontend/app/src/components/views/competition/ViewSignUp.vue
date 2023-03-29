<template>
  <el-space direction="vertical" fill>
      <p v-if="!allowRegistration" style="text-align: center">
          {{ i18n.global.t("ViewCompetition.registration_over") }}
      </p>
    <el-row v-if="allowRegistration" :gutter="20" class="row-bg" justify="space-between">
      <el-col :span="16">
        <el-autocomplete
            v-model="playerSearch"
            :fetch-suggestions="queryPlayer"
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
    <el-row v-if="allowRegistration" :gutter="20" class="row-bg" justify="space-between">
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
    <el-row :gutter="20" class="row-bg" justify="center">
      <el-col :span="24">
        <el-table :data="players" :empty-text="$t('ViewCompetition.no_registration')" border stripe>
          <el-table-column :label="i18n.global.t('general.name')" prop="name" sortable="custom"/>
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
import {useRoute} from "vue-router";
import {ElMessage} from "element-plus";

const props = defineProps({
    allowRegistration: Boolean
})

const route = useRoute()
const isLoggedIn = inject('loggedIn', ref(false))
const canEdit = ref(false)

const players = ref([])

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
          players.value = response.data.map((player) => {
              player.name = player.firstName + ' ' + player.lastName
              return player
          })
      })
      .catch((error) => {
        players.value = []
        ElMessage.error(i18n.global.t("ViewCompetition.query_player_failed"))
        console.log(error)
      })
}

const playerSearch = ref("")

let queriedPlayer = []

function queryPlayer(search, callback) {
  queriedPlayer = queriedPlayer.filter((item) => {
    return item.value.toLowerCase().includes(search.toLowerCase())
  })
  callback(queriedPlayer)
  axios.get(`/player/find?search=${search}`)
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

</script>

<style scoped>
#notice_register {
  font-size: 14px;
  color: #404040;
}
</style>
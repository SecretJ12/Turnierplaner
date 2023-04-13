<template>
  <div id="container">
    <h2>
      <font-awesome-icon v-if=canEdit
                         id="settings"
                         :icon="['fas', 'gear']" class="fa-1x" @click="settings">
      </font-awesome-icon>
      {{ route.params.tourId }}
    </h2>
    <div id="content">
      <div id="competitions">
        <item v-for="competition in competitions"
              :can-edit="canEdit"
              :description="competition.description"
              :name="competition.name"
              :type="competition.tourType"
              @selected="selected"
              @settings="settingsItem"/>
        <AddItem v-if="canEdit" @selected="addCompetition"/>
      </div>
      <el-steps v-if="tournament !== null" id="progress" :active="progress"
                :process-status="statusActive"
                direction="vertical"
                finish-status="success">
        <el-step :description="tournament.registration_phase.begin.toLocaleString(t('lang'), options)
            +'\n - '+tournament.registration_phase.end.toLocaleString(t('lang'), options)"
                 :title="t('TournamentSettings.registration_phase')"/>
        <el-step :description="tournament.game_phase.begin.toLocaleString(t('lang'), options)
            +' - '+tournament.game_phase.end.toLocaleString(t('lang'), options)"
                 :title="t('TournamentSettings.game_phase')"
        />
      </el-steps>
    </div>
  </div>
</template>

<script setup lang="ts">
import Item from '../../items/ItemCompetition.vue'
import {inject, ref, watch} from "vue"
import {useRoute} from "vue-router"
import AddItem from '@/components/items/ItemAdd.vue'
import {router} from '@/main'
import axios from "axios"
import {auth} from "@/security/AuthService"
import {ElMessage} from "element-plus"
import {useI18n} from "vue-i18n"
import {Competition, CompetitionServer, competitionServerToClient} from "@/interfaces/competition";
import {Tournament, TournamentServer, tournamentServerToClient} from "@/interfaces/tournament";
const { t } = useI18n({inheritLocale: true})

const route = useRoute()

const competitions = ref<Competition[]>([])

const isLoggedIn = inject('loggedIn', ref(false))
const canEdit = ref(false)

const progress = ref(0)
const statusActive = ref<"wait"|"process"|"success"|"error">("wait")

const tournament = ref<Tournament | null>(null)

watch(isLoggedIn, async () => {
  update()
})
update()

function update() {
    canEdit.value = false
    auth.getUser().then((user) => {
        if (user !== null) {
            axios.get<boolean>(`/tournament/${route.params.tourId}/competition/canEdit`)
                .then((response) => {
                  canEdit.value = response.data
                })
                .catch((_) => {
                  canEdit.value = false
                })
      }
    })
    axios.get<CompetitionServer[]>(`/tournament/${route.params.tourId}/competition/list`)
        .then((response) => {
            if (response.status === 200)
                competitions.value = response.data.map(competitionServerToClient)
            else {
                ElMessage.error(t("ViewCompetitions.loadingFailed"))
            }
        })
        .catch((error) => {
            ElMessage.error(t("ViewCompetitions.loadingFailed"))
            console.log(error)
            router.push("/")
        })
    axios.get<TournamentServer>(`/tournament/${route.params.tourId}/details`)
        .then((response) => {
            const date = new Date()
            tournament.value = tournamentServerToClient(response.data)

            if (date < tournament.value.registration_phase.begin) {
              progress.value = 0
              statusActive.value = "wait"
            } else if (date < tournament.value.registration_phase.end) {
              progress.value = 0
              statusActive.value = "process"
            } else if (date < tournament.value.game_phase.begin) {
              progress.value = 1
              statusActive.value = "wait"
            } else if (date < tournament.value.game_phase.end) {
              progress.value = 1
              statusActive.value = "process"
            } else {
              progress.value = 1
              statusActive.value = "success"
            }
        })
        .catch((error) => {
            statusActive.value = "error"
            console.log(error)
        })
}

function settings() {
  router.push({path: '/tournament/' + route.params.tourId + '/edit'})
}

function selected(competition: string) {
  router.push({path: "/tournament/" + route.params.tourId + "/competition/" + competition})
}

function settingsItem(competition: string) {
  router.push({path: '/tournament/' + route.params.tourId + '/competition/' + competition + '/edit'})
}

function addCompetition() {
  router.push({path: '/tournament/' + route.params.tourId + '/createCompetition'})
}

const options: Intl.DateTimeFormatOptions = {
  weekday: "long",
  year: "numeric",
  month: "long",
  day: "numeric",
  hour: "numeric",
  minute: "numeric",
}
</script>


<style scoped>
#settings {
  color: #303030;
  margin-right: 5px;
}

#settings:hover {
  filter: drop-shadow(0 0 6px #808080);
}

#settings:active {
  color: #505050;
}

#competitions {
  margin: 10px;
  display: flex;
  flex-wrap: wrap;
  flex-direction: row;
  justify-content: center;
}

#container {
  width: 100%;
}

#content {
  display: flex;
  flex-direction: row;
  justify-content: center;
}

#competitions > * {
  margin: 0 10px 10px 10px;
}

#progress {
  margin-top: 20px;
}

h2 {
  text-align: center;
  font-size: 30px;
}
</style>
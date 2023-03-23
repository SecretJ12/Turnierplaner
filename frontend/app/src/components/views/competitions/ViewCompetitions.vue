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
              :type="competition.type"
              @selected="selected"
              @settings="settingsItem"/>
        <AddItem v-if="canEdit" @selected="addCompetition"/>
      </div>
      <el-steps id="progress" :active="progress"
                :process-status="statusActive"
                direction="vertical"
                finish-status="success">
        <el-step :description="beginRegistration.toLocaleString(i18n.global.t('lang'), options)
            +'\n - '+endRegistration.toLocaleString(i18n.global.t('lang'), options)"
                 :title="$t('TournamentSettings.registration_phase')"/>
        <el-step :description="beginGamePhase.toLocaleString(i18n.global.t('lang'), options)
            +' - '+endGamePhase.toLocaleString(i18n.global.t('lang'), options)"
                 :title="$t('TournamentSettings.game_phase')"
        />
      </el-steps>
    </div>
  </div>
</template>

<script setup>
import Item from '../../items/ItemCompetition.vue';
import {inject, ref, watch} from "vue";
import {useRoute} from "vue-router";
import AddItem from '@/components/items/ItemAdd.vue';
import {i18n, router} from '@/main'
import axios from "axios";
import {auth} from "@/security/AuthService";
import {ElMessage} from "element-plus";

const route = useRoute()

const competitions = ref([])

const isLoggedIn = inject('loggedIn', ref(false))
const canEdit = ref(false)

const progress = ref(0)
const statusActive = ref("wait")

const beginRegistration = ref(new Date())
const endRegistration = ref(new Date())
const beginGamePhase = ref(new Date())
const endGamePhase = ref(new Date())

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
  axios.get(`/tournament/${route.params.tourId}/competition/list`)
      .then((response) => {
        if (response.status === 200)
          competitions.value = response.data
        else {
          ElMessage.error(i18n.global.t("ViewCompetitions.loadingFailed"))
        }
      })
      .catch((error) => {
        ElMessage.error(i18n.global.t("ViewCompetitions.loadingFailed"))
        console.log(error)
        router.push("/")
      })
  axios.get(`/tournament/${route.params.tourId}/details`)
      .then((response) => {
        const date = new Date()
        beginRegistration.value = new Date(response.data.beginRegistration)
        endRegistration.value = new Date(response.data.endRegistration)
        beginGamePhase.value = new Date(response.data.beginGamePhase)
        endGamePhase.value = new Date(response.data.endGamePhase)

        if (date < beginRegistration.value) {
          progress.value = 0
          statusActive.value = "wait"
        } else if (date < endRegistration.value) {
          progress.value = 0
          statusActive.value = "progress"
        } else if (date < beginGamePhase.value) {
          progress.value = 1
          statusActive.value = "wait"
        } else if (date < endGamePhase.value) {
          progress.value = 1
          statusActive.value = "progress"
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

function selected(competition) {
  router.push({path: "/tournament/" + route.params.tourId + "/competition/" + competition})
}

function settingsItem(competition) {
  router.push({path: '/tournament/' + route.params.tourId + '/competition/' + competition + '/edit'})
}

function addCompetition() {
  router.push({path: '/tournament/' + route.params.tourId + '/createCompetition'})
}

const options = {
  weekday: "long",
  year: "numeric",
  month: "long",
  day: "numeric",
  hour: "numeric",
  minute: "numeric",
};

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
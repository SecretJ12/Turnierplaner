<template>
  <div id="container">
    <div>
      <font-awesome-icon
          v-if=canEdit
          id="settings"
          :icon="['fas', 'gear']" class="fa-1x" @click="settings">
      </font-awesome-icon>
      <div id="tourName">
        {{ route.params.tourId }}
      </div>
      <h2>
        {{ route.params.compId }}
      </h2>
    </div>
    <p v-if="detailsLoaded">
      {{ $t("ViewCompetition.tournament_system") }}: {{ $t("CompetitionSettings." + type) }}<br>
      <template v-if="detailsLoaded && description.length > 0">
        {{ $t("general.description") }}: {{ description }}<br>
      </template>
      {{ $t("ViewCompetition.game_mode") }}: {{ $t("CompetitionSettings." + mode) }}<br>
    </p>

    <template v-if="tournamentLoaded">
      <template v-if="!registration_started">
          <!-- TODO -->
          {{ i18n.global.t("ViewCompetition.registration_not_started") }} {{ beginRegistration.toLocaleString(i18n.global.t('lang'), dateOptions) }}
      </template>
      <template v-else-if="!game_started">
        <!-- TODO only if registration phase has started -->
        <!-- show registration page -->
        <ViewSignUp :allowRegistration="allow_registration" />
      </template>
      <template v-else>
        <!-- TODO show after plan has been published -->
        <!-- show game page -->
        <ViewGame :type="type"/>
      </template>
    </template>
  </div>
</template>

<script setup>
import {useRoute} from 'vue-router'
import {inject, reactive, ref, watch} from "vue";
import {auth} from "@/security/AuthService";
import axios from "axios";
import {i18n, router} from "@/main";

import ViewSignUp from "@/components/views/competition/ViewSignUp.vue";
import ViewGame from "@/components/views/competition/ViewGame.vue";

const route = useRoute()
const isLoggedIn = inject('loggedIn', ref(false))
const canEdit = ref(false)

const detailsLoaded = ref(false)
const description = ref("")
const type = ref("knockout")
const mode = ref('single')

const tournamentLoaded = ref(false)
const registration_started = ref(false)
const allow_registration = ref(false)
const game_started = ref(false)
let beginRegistration = ref(new Date())

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
  axios.get(`/tournament/${route.params.tourId}/competition/${route.params.compId}/details`)
      .then((response) => {
          description.value = response.data.description
          type.value = response.data.type.toLowerCase()
          mode.value = response.data.mode.toLowerCase()
          detailsLoaded.value = true
      })
      .catch((error) => {
          console.log(error)
      })
  axios.get(`/tournament/${route.params.tourId}/details`)
      .then((response) => {
          registration_started.value = new Date(response.data.beginRegistration) < new Date()
          allow_registration.value = registration_started.value
              && new Date(response.data.endRegistration) > new Date()
          game_started.value = new Date(response.data.beginGamePhase) < new Date()
          beginRegistration.value = new Date(response.data.beginRegistration)
          tournamentLoaded.value = true
      })
      .catch((error) => {
          console.log(error)
      })
}

function settings() {
  router.push({path: `/tournament/${route.params.tourId}/competition/${route.params.compId}/edit`})
}

const dateOptions = {
    weekday: "long",
    year: "numeric",
    month: "long",
    day: "numeric",
    hour: "numeric",
    minute: "numeric",
};
</script>

<style scoped>
#container {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

h2 {
  font-size: 30px;
  display: inline;
  margin-left: 15px;
}

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

#tourName {
  display: inline;
  color: gray;
  font-size: 18px;
}
</style>
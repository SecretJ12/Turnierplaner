<script setup>
import Item from '../../items/ItemCompetition.vue';
import { inject, ref, watch } from "vue";
import { useRoute } from "vue-router";
import AddItem from '@/components/items/ItemAdd.vue';
import {i18n, router} from '@/main'
import axios from "axios";
import { auth } from "@/security/AuthService";
import {ElMessage} from "element-plus";

const route = useRoute()

const competitions = ref([])

const isLoggedIn = inject('loggedIn', ref(false))
const canEdit = ref(false);

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
          .catch((_) => {
            canEdit.value = false
          })
    }
  });
  axios.get(`/competition/list?tourName=${route.params.tourId}`)
    .then((response) => {
      if (response.status === 200)
        competitions.value = response.data
      else {
        ElMessage.error(i18n.global.t("ViewCompetitions.loadingFailed"))
        router.push("/")
      }
    })
    .catch((error) => {
      ElMessage.error(i18n.global.t("ViewCompetitions.loadingFailed"))
      console.log(error)
      router.push("/")
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
</script>

<template>
  <div id="container">
    <h2>
      <font-awesome-icon v-if=canEdit
          @click="settings"
          id="settings" :icon="['fas', 'gear']" class="fa-1x" >
      </font-awesome-icon>
      {{route.params.tourId}}
    </h2>
    <div id="competitions">
      <item v-for="competition in competitions"
            :name="competition.name"
            :description="competition.description"
            :type="competition.type"
            :can-edit="canEdit"
            @selected="selected"
            @settings="settingsItem" />
      <AddItem v-if="canEdit" @selected="addCompetition"/>
    </div>
  </div>
</template>

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

  #competitions > * {
    margin: 0 10px 10px 10px;
  }

  h2 {
    text-align: center;
  }
</style>
<script setup>
import Item from '../items/ItemCompetition.vue';
import { inject, ref, watch } from "vue";
import { useRoute } from "vue-router";
import AddItem from '@/components/items/ItemAdd.vue';
import { router } from '@/main'
import axios from "axios";
import { auth } from "@/security/AuthService";

const route = useRoute()
function selected(competition) {
  router.push({path: "/tournament/" + route.params.tourId + "/competition/" + competition})
}

const competitions = ref([])
axios.get(`/competition/list?tourName=${route.params.tourId}`)
    .then((response) => {
      competitions.value = response.data
    })
    .catch((error) => {
    })

const isLoggedIn = inject('loggedIn', ref(false))
const canEdit = ref(false);

watch (isLoggedIn, async () => {
  checkCanEdit()
})
checkCanEdit()
function checkCanEdit() {
  canEdit.value = false
  auth.getUser().then((user) => {
    if (user !== null) {
      axios.get('/competition/canEdit')
          .then((response) => {
            canEdit.value = response.status === 200
          })
          .catch((error) => {
          })
    }
  });
}
function addCompetition() {
  router.push({path: '/createCompetition'})
}
</script>

<template>
  <div id="container">
    <h2>{{route.params.tourId}}</h2>
    <div id="competitions">
      <item v-for="competition in competitions"
            :name="competition.name"
            :description="competition.description"
            :type="competition.type"
            @selected="selected" />
      <AddItem v-if="canEdit" @selected="addCompetition"/>
    </div>
  </div>
</template>

<style scoped>
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
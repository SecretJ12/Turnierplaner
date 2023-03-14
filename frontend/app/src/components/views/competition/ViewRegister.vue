<template>
  <!-- TODO field to search for players and register for tournament -->
  <!-- button to register player -->
  <el-table stripe border :data="players" style="width: fit-content">
    <el-table-column width="200" prop="name" :label="i18n.global.t('general.name')" />
  </el-table>
</template>

<script setup>
import { inject, ref, watch } from "vue"
import { i18n, router } from '@/main'
import {auth} from "@/security/AuthService";
import axios from "axios";
import { useRoute } from "vue-router";

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
  // TODO only show players belonging to competition
  axios.get(`/player/players`)
    .then((result) => {
      players.value = result.data.map((value) => {
        value.name = value.firstName + ' ' + value.lastName
        return value
      })
    })
}
</script>

<style scoped>

</style>
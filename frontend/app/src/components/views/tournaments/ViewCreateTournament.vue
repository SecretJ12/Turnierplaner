<template>
  <FormTournament :data="data" :submit-text="t('general.create')" @submit="submit" />
</template>

<script setup lang="ts">
import {reactive} from 'vue'
import axios from "axios"
import {router} from "@/main"
import {ElMessage} from "element-plus"
import FormTournament from "@/components/views/tournaments/FormTournament.vue"
import {Tournament, TournamentServer} from "@/interfaces/tournament"
import {useI18n} from "vue-i18n"
const { t } = useI18n({inheritLocale: true})

const data = reactive<Tournament>({
  name: '',
  visible: true,
  description: '',
  registration_phase: [new Date(), new Date], // TODO nice default dates
  game_phase: [new Date(), new Date] // TODO nice default dates
})

function submit(server_data: TournamentServer) {
  axios.post("/tournament/add", server_data)
      .then(_ => {
          ElMessage.success(t("ViewCreateTournament.tournamentCreated"))
          router.push({path: "/tournament/" + data.name})
      })
      .catch(_ => {
          ElMessage.error(t("ViewCreateTournament.tournamentCreationFailed"))
      })
}

</script>

<style scoped>
</style>
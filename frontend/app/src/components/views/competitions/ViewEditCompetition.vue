<template>
    <FormCompetition v-if="competition !== null" :disabled="disabled" :competition="competition"
                     :submit-text="t('general.update')" @submit="submit"/>
</template>

<script setup lang="ts">
import {router} from "@/main"
import {ref} from "vue"
import axios from "axios"
import {ElMessage} from "element-plus"
import {useRoute} from "vue-router"
import FormCompetition from "@/components/views/competitions/FormCompetition.vue"
import {useI18n} from "vue-i18n"
import {Competition, CompetitionServer, competitionServerToClient} from "@/interfaces/competition"

const { t } = useI18n({inheritLocale: true})

const route = useRoute()

const competition = ref<Competition | null>(null)

const disabled = ref(true)

axios.get<CompetitionServer>(`/tournament/${route.params.tourId}/competition/${route.params.compId}/details`)
    .then((response) => {
        competition.value = competitionServerToClient(response.data)
        disabled.value = false
    })
    .catch((error) => {
      ElMessage.error(t("ViewEditCompetition.loadingDetailsFailed"))
      console.log(error)
      router.back();
    })

function submit(server_data: CompetitionServer) {
  axios.post(`/tournament/${route.params.tourId}/competition/update`, server_data)
      .then(_ => {
          ElMessage.success(t("ViewEditCompetition.saved"))
      })
      .catch(_ => {
        ElMessage.error(t("ViewEditCompetition.saving_failed"))
      })
}
</script>

<style scoped>
</style>
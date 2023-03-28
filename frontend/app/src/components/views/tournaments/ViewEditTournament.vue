<template>
  <FormTournament :disabled="disabled" :data="data" :submit-text="i18n.global.t('general.update')" @submit="submit" />
</template>

<script setup>
import {reactive, ref} from 'vue'
import axios from "axios";
import {i18n, router} from "@/main";
import {ElMessage} from "element-plus";
import {useRoute} from "vue-router";
import FormTournament from "@/components/views/tournaments/FormTournament.vue";

const route = useRoute()

const data = reactive({
  id: null,
  name: '',
  visible: true,
  description: '',
  registration_phase: [],
  game_phase: []
})

const disabled = ref(true)

axios.get(`/tournament/${route.params.tourId}/details`)
    .then((response) => {
      data.id = response.data.id
      data.name = response.data.name
      data.visible = response.data.visible
      data.description = response.data.description
      data.registration_phase = [response.data.beginRegistration, response.data.endRegistration]
      data.game_phase = [response.data.beginGamePhase, response.data.endGamePhase]
      disabled.value = false
    })
    .catch((error) => {
      ElMessage.error(i18n.global.t("ViewEditTournament.loadingDetailsFailed"))
      console.log(error)
      router.back();
    })

function submit(server_data) {
  server_data["id"] = data.id

  axios.post("/tournament/update", server_data)
      .then(_ => {
        ElMessage.success("Tournament saved")
      })
      .catch(_ => {
        ElMessage.error("Couldn't create tournament")
      })
}
</script>

<style scoped>
</style>
<template>
  <FormCompetition :data="data" :submit-text="i18n.global.t('general.create')" @submit="submit" />
</template>

<script setup>
import {i18n, router} from "@/main";
import {reactive} from "vue";
import axios from "axios";
import {ElMessage} from "element-plus";
import {useRoute} from "vue-router";
import FormCompetition from "@/components/views/competitions/FormCompetition.vue";

const route = useRoute()

const data = reactive({
  name: '',
  description: '',
  tourType: 'KNOCKOUT',
  mode: 'SINGLE',
  signup: 'INDIVIDUAL',
  playerA: {
    sex: "MALE",
    hasMinAge: false,
    minAge: new Date(),
    hasMaxAge: false,
    maxAge: new Date()
  },
  playerB: {
      different: Boolean,
    sex: "MALE",
    hasMinAge: false,
    minAge: new Date(),
    hasMaxAge: false,
    maxAge: new Date()
  }
})

function submit(server_data) {
  axios.post(`/tournament/${route.params.tourId}/competition/add`, server_data)
      .then(_ => {
        router.push({path: "/tournament/" + route.params.tourId})
      })
      .catch(_ => {
        ElMessage.error(i18n.global.t("ViewCreateCompetition.creationFailed"))
      })
}
</script>

<style scoped>
</style>
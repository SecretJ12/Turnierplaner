<template>
  <FormCompetition :disabled="disabled" :data="data" :submit-text="i18n.global.t('general.update')" @submit="submit" />
</template>

<script setup>
import {i18n, router} from "@/main";
import {reactive, ref} from "vue";
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

const disabled = ref(true)

axios.get(`/tournament/${route.params.tourId}/competition/${route.params.compId}/details`)
    .then((response) => {
      data.id = response.data.id
      data.name = response.data.name
      data.description = response.data.description
      data.tourType = response.data.type
      disabled.value = false
        // TODO receive all the data
    })
    .catch((error) => {
      ElMessage.error(i18n.global.t("ViewEditCompetition.loadingDetailsFailed"))
      console.log(error)
      router.back();
    })

function submit(server_data) {
  server_data["id"] = data.id

  axios.post(`/tournament/${route.params.tourId}/competition/update`, server_data)
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
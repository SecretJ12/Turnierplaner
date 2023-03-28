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
  id: null,
  name: '',
  description: '',
  type: 'KNOCKOUT'
})

const disabled = ref(true)

axios.get(`/tournament/${route.params.tourId}/competition/${route.params.compId}/details`)
    .then((response) => {
      data.id = response.data.id
      data.name = response.data.name
      data.description = response.data.description
      data.type = response.data.type
      disabled.value = false
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
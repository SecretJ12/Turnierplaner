<template>
  <FormCompetition :disabled="disabled" :data="data" :submit-text="t('general.update')" @submit="submit" />
</template>

<script setup>
import {router} from "@/main"
import {reactive, ref} from "vue"
import axios from "axios"
import {ElMessage} from "element-plus"
import {useRoute} from "vue-router"
import FormCompetition from "@/components/views/competitions/FormCompetition.vue"
import {useI18n} from "vue-i18n"
const { t } = useI18n({inheritLocale: true})

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
        console.log(response.data)
        data.id = response.data.id
        data.name = response.data.name
        data.description = response.data.description
        data.tourType = response.data.type
        data.mode = response.data.mode
        data.signup = response.data.signUp
        data.playerA.sex = response.data.playerA.sex
        data.playerA.hasMinAge = response.data.playerA.hasMinAge
        data.playerA.minAge = new Date(response.data.playerA.minAge)
        data.playerA.hasMaxAge = response.data.playerA.hasMaxAge
        data.playerA.maxAge = new Date(response.data.playerA.maxAge)
        data.playerB.different = response.data.playerB.different
        data.playerB.sex = response.data.playerB.sex
        data.playerB.hasMinAge = response.data.playerB.hasMinAge
        data.playerB.minAge = new Date(response.data.playerB.minAge)
        data.playerB.hasMaxAge = response.data.playerB.hasMaxAge
        data.playerB.maxAge = new Date(response.data.playerB.maxAge)
        disabled.value = false
    })
    .catch((error) => {
      ElMessage.error(t("ViewEditCompetition.loadingDetailsFailed"))
      console.log(error)
      router.back();
    })

function submit(server_data) {
  server_data["id"] = data.id

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
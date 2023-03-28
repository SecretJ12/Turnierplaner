<template>
  <FormTournament :data="data" :submit-text="i18n.global.t('general.create')" @submit="submit" />
</template>

<script setup>
import {reactive, ref} from 'vue'
import axios from "axios";
import {i18n, router} from "@/main";
import {ElMessage} from "element-plus";
import FormTournament from "@/components/views/tournaments/FormTournament.vue";

const formRef = ref()
const data = reactive({
  name: '',
  visible: true,
  description: '',
  registration_phase: null,
  game_phase: null
})

function submit(server_data) {
  axios.post("/tournament/add", server_data)
      .then(_ => {
        router.push({path: "/tournament/" + data.name})
      })
      .catch(_ => {
        ElMessage.error(i18n.global.t("ViewCreateTournament.creationFailed"))
      })
}

</script>

<style scoped>
</style>
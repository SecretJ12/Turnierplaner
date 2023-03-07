<template>
  <div id="createTournament">
    <el-form
        ref="formRef"
        :model="data"
        label-width="120px"
        size="large"
        scroll-to-error="scroll-to-error"
        label-position="top"
    >
      <GeneralSettings />

      <el-row class="row-bg" justify="end">
        <el-col :span="5">
          <el-form-item>
            <el-button type="primary" @click="submit(formRef)">Create</el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { settings } from '@/settings'
import axios from "axios";
import { ElMessage } from "element-plus";
import { router } from "@/main";

import GeneralSettings from '@/components/Forms/FormTournamentSettingsGeneral.vue';

const formRef = ref()
const data = reactive({
  name: '',
  visible: true,
  description: '',
  registration_phase: null,
  game_phase: null
})

function submit(formRef) {
  if (!formRef)
    return
  formRef.validate((valid) => {
    if (valid) {
      console.log('submit!')
      axios.post(settings.BACKEND+"/tournament/add", data)
          .then(value => {
            router.push({path: "/tournament/" + data.name})
          })
          .catch(reason => {
            ElMessage.error("Couldn't create tournament")
          })
    } else {
      console.log('error submit!')
    }
  })
}
</script>

<style scoped>
#createTournament {
  width: 100%;
  margin: 10px;
  display: flex;
  flex-wrap: wrap;
  flex-direction: row;
  justify-content: center;
}
</style>
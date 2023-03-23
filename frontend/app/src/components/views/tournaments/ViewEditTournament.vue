<template>
  <div id="form">
    <el-form
        ref="formRef"
        :disabled="disabled"
        :model="data"
        label-position="top"
        label-width="120px"
        scroll-to-error="scroll-to-error"
        size="large"
    >
      <el-row :gutter="20" class="row-bg" justify="space-between">
        <el-col :span="20">
          <!-- Tournament name -->
          <el-form-item
              :label="$t('general.name')"
              :rules="[
                  {
                    required: true,
                    message: i18n.global.t('general.name_missing'),
                    trigger: 'blur',
                  }
              ]"
              prop="name"
          >
            <el-input
                v-model="data.name"
                maxlength="30"
                show-word-limit
            />
          </el-form-item>
        </el-col>
        <el-col :span="4">
          <!-- Visible -->
          <el-form-item
              :label="$t('TournamentSettings.visible')"
              prop="visible"
          >
            <el-switch
                v-model="data.visible"
                style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- Description -->
      <el-form-item
          :label="$t('general.description')"
          prop="description"
      >
        <el-input
            v-model="data.description"
            :autosize="{minRows: 3, maxRows: 5}"
            autosize
            maxlength="100"
            show-word-limit
            type="textarea"
        />
      </el-form-item>

      <!-- Times -->
      <el-divider/>
      <!-- Begin -> End registration -->
      <el-form-item
          :label="$t('TournamentSettings.registration_phase')"
          :rules="[
            {
              required: true,
              validator: checkDates,
              trigger: 'blur',
            }
    ]"
          prop="registration_phase"
      >
        <el-date-picker
            v-model="data.registration_phase"
            end-placeholder="End date"
            start-placeholder="Start date"
            type="datetimerange"
        />
      </el-form-item>
      <!-- Begin -> End game phase -->
      <el-form-item
          :label="$t('TournamentSettings.game_phase')"
          :rules="[
            {
              required: true,
              validator: checkDates,
              trigger: 'blur',
            }
    ]"
          prop="game_phase"
      >
        <el-date-picker
            v-model="data.game_phase"
            end-placeholder="End date"
            start-placeholder="Start date"
            type="datetimerange"
        />
      </el-form-item>

      <el-row class="row-bg" justify="end">
        <el-form-item>
          <el-button type="primary" @click="submit(formRef)">
            {{ i18n.global.t("general.update") }}
          </el-button>
        </el-form-item>
      </el-row>
    </el-form>
  </div>
</template>

<script setup>
import {reactive, ref} from 'vue'
import axios from "axios";
import {i18n, router} from "@/main";
import {ElMessage} from "element-plus";
import {useRoute} from "vue-router";

const route = useRoute()

const formRef = ref()
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

function submit(formRef) {
  if (!formRef)
    return
  formRef.validate((valid) => {
    if (valid) {
      console.log('submit!')
      const server_data = {
        id: data.id,
        name: data.name,
        description: data.description,
        beginRegistration: data.registration_phase[0],
        endRegistration: data.registration_phase[1],
        beginGamePhase: data.game_phase[0],
        endGamePhase: data.game_phase[1],
        visible: data.visible
      }
      axios.post("/tournament/update", server_data)
          .then(_ => {
            ElMessage.success("Tournament saved")
          })
          .catch(_ => {
            ElMessage.error("Couldn't create tournament")
          })
    } else {
      console.log('validation failed')
    }
  })
}

const checkDates = (rule, value, callback) => {
  if (!value)
    callback(new Error(i18n.global.t("TournamentSettings.missing_date")))
  if (data.registration_phase === null)
    callback()
  if (data.game_phase === null)
    callback()
  if (new Date(data.registration_phase[1]) > new Date(data.game_phase[0]))
    callback(new Error(i18n.global.t("TournamentSettings.wrong_dates")))
  callback()
}

</script>

<style scoped>
#form {
  width: 100%;
  margin: 10px;
  display: flex;
  flex-wrap: wrap;
  flex-direction: row;
  justify-content: center;
}
</style>
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
      <el-row :gutter="20" class="row-bg" justify="space-between">
        <el-col :span="20">
          <!-- Tournament name -->
          <el-form-item
              :label="$t('TournamentSettings.name')"
              prop="name"
              :rules="[
                  {
                    required: true,
                    message: i18n.global.t('TournamentSettings.name_missing'),
                    trigger: 'blur',
                  }
              ]"
          >
            <el-input
                v-model="data.name"
                show-word-limit
                maxlength="30"
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
          :label="$t('TournamentSettings.description')"
          prop="description"
      >
        <el-input
            type="textarea"
            :autosize="{minRows: 3, maxRows: 5}"
            v-model="data.description"
            show-word-limit
            maxlength="100"
            autosize
        />
      </el-form-item>

      <!-- Times -->
      <el-divider />
      <!-- Begin -> End registration -->
      <el-form-item
          :label="$t('TournamentSettings.registration_phase')"
          prop="registration_phase"
          :rules="[
        {
          validator: checkDates,
          trigger: 'blur',
        }
    ]"
      >
        <el-date-picker
            v-model="data.registration_phase"
            type="datetimerange"
            start-placeholder="Start date"
            end-placeholder="End date"
        />
      </el-form-item>
      <!-- Begin -> End game phase -->
      <el-form-item
          :label="$t('TournamentSettings.game_phase')"
          prop="game_phase"
          :rules="[
        {
          validator: checkDates,
          trigger: 'blur',
        }
    ]"
      >
        <el-date-picker
            v-model="data.game_phase"
            type="datetimerange"
            start-placeholder="Start date"
            end-placeholder="End date"
        />
      </el-form-item>

      <el-row class="row-bg" justify="end">
        <el-form-item>
          <el-button type="primary" @click="submit(formRef)">
            {{ i18n.global.t("general.create") }}
          </el-button>
        </el-form-item>
      </el-row>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { settings } from '@/settings'
import axios from "axios";
import { i18n } from "@/main";
import { ElMessage } from "element-plus";
import { router } from "@/main";

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
      const server_data = {
        name: data.name,
        description: data.description,
        beginRegistration: data.registration_phase[0],
        endRegistration: data.registration_phase[1],
        beginGamePhase: data.game_phase[0],
        endGamePhase: data.game_phase[1],
        visible: data.visible
      }
      axios.post(settings.BACKEND+"/tournament/add", server_data)
          .then(_ => {
            router.push({path: "/tournament/" + data.name})
          })
          .catch(_ => {
            // TODO add to i18n
            ElMessage.error("Couldn't create tournament")
          })
    } else {
      console.log('error submit!')
    }
  })
}

const checkDates = (rule, value, callback) => {
  if (!value)
    callback(new Error(i18n.global.t("TournamentSettings.missing_date")))
  if (new Date(data.registration_phase[1]) > new Date(data.game_phase[0]))
    callback(new Error(i18n.global.t("TournamentSettings.wrong_dates")))
  callback()
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
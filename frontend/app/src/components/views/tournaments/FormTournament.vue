<template>
  <div id="form">
    <el-form
        ref="formRef"
        :model="props.data"
        :disabled="props.disabled"
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
            {{ props.submitText }}
          </el-button>
        </el-form-item>
      </el-row>
    </el-form>
  </div>
</template>

<script setup>
import {ref} from "vue";
import {i18n} from "@/main";

const formRef = ref()
const props = defineProps({
  submitText: String,
  disabled: false,
  data: {
    name: String,
    description: String,
    type: String
  }
})

const emit = defineEmits(['submit'])

function submit(formRef) {
  if (!formRef)
    return
  formRef.validate((valid) => {
    if (valid) {
      console.log('submit!')
      const server_data = {
        name: props.data.name,
        description: props.data.description,
        beginRegistration: props.data.registration_phase[0],
        endRegistration: props.data.registration_phase[1],
        beginGamePhase: props.data.game_phase[0],
        endGamePhase: props.data.game_phase[1],
        visible: props.data.visible
      }
      emit('submit', server_data)
    } else {
      console.log('validation failed')
    }
  })
}

const checkDates = (rule, value, callback) => {
  if (!value)
    callback(new Error(i18n.global.t("TournamentSettings.missing_date")))
  if (props.data.registration_phase === null)
    callback()
  if (props.data.game_phase === null)
    callback()
  if (new Date(props.data.registration_phase[1]) > new Date(props.data.game_phase[0]))
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
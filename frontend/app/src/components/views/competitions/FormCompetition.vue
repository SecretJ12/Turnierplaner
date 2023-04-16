<template>
  <el-row align="middle" class="full-width" justify="center">
    <el-col :lg="10" :md="14" :sm="16" :xl="10" :xs="22">
      <el-form
          ref="formRef"
          :disabled="props.disabled"
          :label-position="(windowWidth > 480) ? 'left' : 'top'"
          :model="props.competition"
          label-width="140px"
          scroll-to-error="scroll-to-error"
          size="large"
          style="width: 100%"
      >
        <!-- Competition name -->
        <el-form-item
            :label="t('general.name')"
            :rules="[
                  {
                    required: true,
                    message: t('general.name_missing'),
                    trigger: 'blur',
                  }
              ]"
            prop="name"
        >
          <el-input
              v-model="props.competition.name"
              maxlength="30"
              show-word-limit
          />
        </el-form-item>

        <!-- Description -->
        <el-form-item
            :label="t('general.description')"
            prop="description"
        >
          <el-input
              v-model="props.competition.description"
              :autosize="{minRows: 3, maxRows: 5}"
              autosize
              maxlength="100"
              show-word-limit
              type="textarea"
          />
        </el-form-item>


        <!-- tournament type -->
        <el-divider>{{ t("CompetitionSettings.tournament_settings") }}</el-divider>
        <el-row :gutter="20" justify="space-between">
          <el-col :span="12" :xs="24">
            <el-form-item
                :label="t('CompetitionSettings.type')"
                prop="tourType"
            >
              <el-select v-model="props.competition.tourType" class="full-width">
                <el-option
                    :key="'KNOCKOUT'"
                    :label='t("CompetitionSettings.knockout")'
                    :value="TourType.KNOCKOUT"/>
                <el-option
                    :key="'GROUPS'"
                    :label='t("CompetitionSettings.groups")'
                    :value="TourType.GROUPS"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20" justify="space-between">
          <el-col :span="12" :xs="24">
            <!-- game mode -->
            <el-form-item
                :label='t("CompetitionSettings.mode")'
                prop="mode">
              <el-select v-model="props.competition.mode"
                         class="full-width"
              >
                <el-option
                    :key="'SINGLE'"
                    :label='t("CompetitionSettings.single")'
                    :value="Mode.SINGLE"/>
                <el-option
                    :key="'DOUBLE'"
                    :label='t("CompetitionSettings.double")'
                    :value="Mode.DOUBLE"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12" :xs="24">
            <!-- signup -->
            <el-form-item
                v-show="props.competition.mode === 'DOUBLE'"
                :label='t("CompetitionSettings.signup")'
                prop="mode">
              <el-select v-model="props.competition.signUp"
                         class="full-width"
              >
                <el-option
                    :key="'INDIVIDUAL'"
                    :label='t("CompetitionSettings.individual")'
                    :value="SignUp.INDIVIDUAL"/>
                <el-option
                    :key="'TOGETHER'"
                    :label='t("CompetitionSettings.together")'
                    :value="SignUp.TOGETHER"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- Player 1 settings -->
        <el-divider>{{ t("CompetitionSettings.player") }}</el-divider>
        <!-- sex -->
        <el-row :gutter="20" justify="space-between">
          <el-col :span="12" :xs="24">
            <el-form-item
                :label='t("CompetitionSettings.sex")'>
              <el-select v-model="props.competition.playerA.sex"
                         class="full-width">
                <el-option
                    key="MALE"
                    :label='t("CompetitionSettings.male")'
                    :value="Sex.MALE"/>
                <el-option
                    :key="'FEMALE'"
                    :label='t("CompetitionSettings.female")'
                    :value="Sex.FEMALE"/>
                <el-option
                    :key="'ANY'"
                    :label='t("CompetitionSettings.any")'
                    :value="Sex.ANY"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <!-- age -->
        <!-- min age -->
        <el-form-item
            :label='t("CompetitionSettings.minAge")'
            :rules="[
                          {
                            required: false,
                            validator: checkAMin,
                            trigger: 'blur',
                          }]"
            prop="playerA.hasMinAge"
        >
          <el-row :gutter="20" justify="space-between">
            <el-col :span="5">
              <el-switch
                  v-model="props.competition.playerA.hasMinAge"/>
            </el-col>
            <el-col :span="19">
              <el-date-picker
                  v-if="props.competition.playerA.hasMinAge"
                  v-model="props.competition.playerA.minAge"
                  style="float: right;"/>
            </el-col>
          </el-row>
        </el-form-item>
        <!-- max age -->
        <el-form-item
            :label='t("CompetitionSettings.maxAge")'
            :rules="[
                          {
                            required: false,
                            validator: checkAMax,
                            trigger: 'blur',
                          }]"
            prop="playerA.hasMaxAge"
        >
          <el-row :gutter="20" justify="space-between">
            <el-col :span="5">
              <el-switch
                  v-model="props.competition.playerA.hasMaxAge"/>
            </el-col>
            <el-col :span="19">
              <el-date-picker
                  v-if="props.competition.playerA.hasMaxAge"
                  v-model="props.competition.playerA.maxAge"
                  style="float: right;"/>
            </el-col>
          </el-row>
        </el-form-item>

        <!-- Player 2 settings -->
        <template v-if="props.competition.mode === 'DOUBLE'">
          <el-switch
              v-model="props.competition.playerB.different"
              :active-text="t('CompetitionSettings.differentB')"
              style="margin-bottom: 22px;"/>
          <template v-if="props.competition.playerB.different">
            <!-- sex -->
            <el-row :gutter="20" justify="space-between">
              <el-col :span="12" :xs="24">
                <el-form-item
                    :label='t("CompetitionSettings.sex")'>
                  <el-select v-model="props.competition.playerB.sex"
                             class="full-width">
                    <el-option
                        :key="'MALE'"
                        :label='t("CompetitionSettings.male")'
                        :value="Sex.MALE"/>
                    <el-option
                        :key="'FEMALE'"
                        :label='t("CompetitionSettings.female")'
                        :value="Sex.FEMALE"/>
                    <el-option
                        :key="'ANY'"
                        :label='t("CompetitionSettings.any")'
                        :value="Sex.ANY"/>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <!-- age -->
            <!-- min age -->
            <el-form-item
                :label='t("CompetitionSettings.minAge")'
                :rules="[
                                  {
                                    required: false,
                                    validator: checkBMin,
                                    trigger: 'blur',
                                  }]"
                prop="playerB.hasMinAge"
            >
              <el-row :gutter="20" justify="space-between">
                <el-col :span="5">
                  <el-switch
                      v-model="props.competition.playerB.hasMinAge"/>
                </el-col>
                <el-col :span="19">
                  <el-date-picker
                      v-if="props.competition.playerB.hasMinAge"
                      v-model="props.competition.playerB.minAge"
                      style="float: right;"/>
                </el-col>
              </el-row>
            </el-form-item>
            <!-- max age -->
            <el-form-item
                :label='t("CompetitionSettings.maxAge")'
                :rules="[
                                  {
                                    required: false,
                                    validator: checkBMax,
                                    trigger: 'blur',
                                  }]"
                prop="playerB.hasMaxAge"
            >
              <el-row :gutter="20" justify="space-between">
                <el-col :span="5">
                  <el-switch
                      v-model="props.competition.playerB.hasMaxAge"/>
                </el-col>
                <el-col :span="19">
                  <el-date-picker
                      v-if="props.competition.playerB.hasMaxAge"
                      v-model="props.competition.playerB.maxAge"
                      style="float: right;"/>
                </el-col>
              </el-row>
            </el-form-item>
          </template>
        </template>

        <el-row justify="end">
          <el-form-item>
            <el-button type="primary" @click="submit(formRef)">
              {{ props.submitText }}
            </el-button>
          </el-form-item>
        </el-row>
      </el-form>
    </el-col>
  </el-row>
</template>

<script lang="ts" setup>
import {ref} from 'vue'
import {useI18n} from "vue-i18n"
import {Competition, competitionClientToServer, Mode, Sex, SignUp, TourType} from "@/interfaces/competition";

const {t} = useI18n({inheritLocale: true})

let windowWidth = ref(window.innerWidth)
window.addEventListener('resize', () => {
  windowWidth.value = window.innerWidth
})

const formRef = ref()
const props = withDefaults(defineProps<{
  submitText: string,
  disabled: boolean,
  competition: Competition
}>(), {
  disabled: false
})

const emit = defineEmits(['submit'])

function submit(formRef: HTMLFormElement | undefined) {
  if (!formRef)
    return
  formRef.validate((valid: boolean) => {
    if (valid) {
      emit('submit', competitionClientToServer(props.competition))
    } else {
      console.log('validation failed')
    }
  })
}

const checkAMin = (rule: any, value: Date, callback: (arg0?: Error) => void) => {
  if (props.competition.playerA.hasMinAge && props.competition.playerA.minAge === null)
    callback(new Error(t("CompetitionSettings.missingAge")))
  callback()
}
const checkAMax = (rule: any, value: Date, callback: (arg0?: Error) => void) => {
  if (props.competition.playerA.hasMaxAge && props.competition.playerA.maxAge === null)
    callback(new Error(t("CompetitionSettings.missingAge")))
  callback()
}
const checkBMin = (rule: any, value: Date, callback: (arg0?: Error) => void) => {
  if (props.competition.playerB.hasMinAge && props.competition.playerB.minAge === null)
    callback(new Error(t("CompetitionSettings.missingAge")))
  callback()
}
const checkBMax = (rule: any, value: Date, callback: (arg0?: Error) => void) => {
  if (props.competition.playerB.hasMaxAge && props.competition.playerB.maxAge === null)
    callback(new Error(t("CompetitionSettings.missingAge")))
  callback()
}
</script>

<style scoped>

.full-width {
  width: 100%;
}
</style>
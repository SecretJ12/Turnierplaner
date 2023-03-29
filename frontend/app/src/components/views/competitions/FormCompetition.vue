<template>
    <el-row align="middle" justify="center" class="full-width">
        <el-col :xl="10" :lg="10" :md="14" :sm="16" :xs="22">
            <el-form
                    style="width: 100%"
                    ref="formRef"
                    :model="props.data"
                    :disabled="props.disabled"
                    :label-position="(windowWidth > 480) ? 'left' : 'top'"
                    label-width="140px"
                    scroll-to-error="scroll-to-error"
                    size="large"
            >
                <!-- Competition name -->
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
                            v-model="props.data.name"
                            maxlength="30"
                            show-word-limit
                    />
                </el-form-item>

                <!-- Description -->
                <el-form-item
                        :label="$t('general.description')"
                        prop="description"
                >
                    <el-input
                            v-model="props.data.description"
                            :autosize="{minRows: 3, maxRows: 5}"
                            autosize
                            maxlength="100"
                            show-word-limit
                            type="textarea"
                    />
                </el-form-item>


                <!-- tournament type -->
                <el-divider>{{ i18n.global.t("CompetitionSettings.tournament_settings") }}</el-divider>
                <el-row :gutter="20" justify="space-between">
                    <el-col :span="12" :xs="24">
                        <el-form-item
                                :label="$t('CompetitionSettings.type')"
                                prop="tourType"
                        >
                            <el-select v-model="props.data.tourType" class="full-width">
                                <el-option
                                    :key="'KNOCKOUT'"
                                    :label='$t("CompetitionSettings.knockout")'
                                    value="KNOCKOUT"/>
                                <el-option
                                    :key="'GROUPS'"
                                    :label='$t("CompetitionSettings.groups")'
                                    value="GROUPS"/>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="20" justify="space-between">
                    <el-col :span="12" :xs="24">
                        <!-- game mode -->
                        <el-form-item
                                :label='$t("CompetitionSettings.mode")'
                                prop="mode">
                            <el-select v-model="props.data.mode"
                                       class="full-width"
                            >
                                <el-option
                                    :key="'SINGLE'"
                                    :label='$t("CompetitionSettings.single")'
                                    value="SINGLE"/>
                                <el-option
                                    :key="'DOUBLE'"
                                    :label='$t("CompetitionSettings.double")'
                                    value="DOUBLE"/>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" :xs="24">
                        <!-- signup -->
                        <el-form-item
                                :label='$t("CompetitionSettings.signup")'
                                prop="mode"
                                v-show="props.data.mode === 'DOUBLE'">
                            <el-select v-model="props.data.signup"
                                       class="full-width"
                            >
                                <el-option
                                    :key="'INDIVIDUAL'"
                                    :label='$t("CompetitionSettings.individual")'
                                    value="INDIVIDUAL"/>
                                <el-option
                                    :key="'TOGETHER'"
                                    :label='$t("CompetitionSettings.together")'
                                    value="TOGETHER"/>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>

                <!-- Player 1 settings -->
                <el-divider>{{ i18n.global.t("CompetitionSettings.player") }}</el-divider>
                <!-- sex -->
                <el-row :gutter="20" justify="space-between">
                    <el-col :span="12" :xs="24">
                        <el-form-item
                                :label='$t("CompetitionSettings.sex")'>
                            <el-select v-model="props.data.playerA.sex"
                                       class="full-width">
                                <el-option
                                    key="MALE"
                                    :label='$t("CompetitionSettings.male")'
                                    value="MALE"/>
                                <el-option
                                    :key="'FEMALE'"
                                    :label='$t("CompetitionSettings.female")'
                                    value="FEMALE"/>
                                <el-option
                                    :key="'ANY'"
                                    :label='$t("CompetitionSettings.any")'
                                    value="ANY"/>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <!-- age -->
                <!-- min age -->
                <el-form-item
                        :label='$t("CompetitionSettings.minAge")'
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
                                    v-model="props.data.playerA.hasMinAge"/>
                        </el-col>
                        <el-col :span="19">
                            <el-date-picker
                                    v-if="props.data.playerA.hasMinAge"
                                    style="float: right;"
                                    v-model="props.data.playerA.minAge"/>
                        </el-col>
                    </el-row>
                </el-form-item>
                <!-- max age -->
                <el-form-item
                        :label='$t("CompetitionSettings.maxAge")'
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
                                    v-model="props.data.playerA.hasMaxAge"/>
                        </el-col>
                        <el-col :span="19">
                            <el-date-picker
                                    v-if="props.data.playerA.hasMaxAge"
                                    style="float: right;"
                                    v-model="props.data.playerA.maxAge"/>
                        </el-col>
                    </el-row>
                </el-form-item>

                <!-- Player 2 settings -->
                <template v-if="props.data.mode === 'DOUBLE'">
                    <el-switch
                            :active-text="$t('CompetitionSettings.differentB')"
                            style="margin-bottom: 22px;"
                            v-model="props.data.playerB.different"/>
                    <template v-if="props.data.playerB.different">
                        <!-- sex -->
                        <el-row :gutter="20" justify="space-between">
                            <el-col :span="12" :xs="24">
                                <el-form-item
                                        :label='$t("CompetitionSettings.sex")'>
                                    <el-select v-model="props.data.playerB.sex"
                                               class="full-width">
                                        <el-option
                                            :key="'MALE'"
                                            :label='$t("CompetitionSettings.male")'
                                            value="MALE"/>
                                        <el-option
                                            :key="'FEMALE'"
                                            :label='$t("CompetitionSettings.female")'
                                            value="FEMALE"/>
                                        <el-option
                                            :key="'ANY'"
                                            :label='$t("CompetitionSettings.any")'
                                            value="ANY"/>
                                    </el-select>
                                </el-form-item>
                            </el-col>
                        </el-row>
                        <!-- age -->
                        <!-- min age -->
                        <el-form-item
                                :label='$t("CompetitionSettings.minAge")'
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
                                            v-model="props.data.playerB.hasMinAge"/>
                                </el-col>
                                <el-col :span="19">
                                    <el-date-picker
                                            v-if="props.data.playerB.hasMinAge"
                                            style="float: right;"
                                            v-model="props.data.playerB.minAge"/>
                                </el-col>
                            </el-row>
                        </el-form-item>
                        <!-- max age -->
                        <el-form-item
                                :label='$t("CompetitionSettings.maxAge")'
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
                                            v-model="props.data.playerB.hasMaxAge"/>
                                </el-col>
                                <el-col :span="19">
                                    <el-date-picker
                                            v-if="props.data.playerB.hasMaxAge"
                                            style="float: right;"
                                            v-model="props.data.playerB.maxAge"/>
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

<script setup>
import {i18n} from "@/main"
import {ref} from 'vue'

let windowWidth = ref(window.innerWidth)
window.addEventListener('resize', () => {
    windowWidth.value = window.innerWidth
})

const formRef = ref()
const props = defineProps({
    submitText: String,
    disabled: Boolean,
    data: {
        type: Object,
        name: String,
        description: String,
        tourType: String,
        mode: String,
        signup: String,
        playerA: {
            type: Object,
            sex: String,
            hasMinAge: Boolean,
            minAge: Date,
            hasMaxAge: Boolean,
            maxAge: Date
        },
        playerB: {
            type: Object,
            different: Boolean,
            sex: String,
            hasMinAge: Boolean,
            minAge: Date,
            hasMaxAge: Boolean,
            maxAge: Date
        }
    }
})

const emit = defineEmits(['submit'])

function submit(formRef) {
  if (!formRef)
    return
  formRef.validate((valid) => {
    if (valid) {
      const server_data = {
          name: props.data.name,
          description: props.data.description,
          type: props.data.tourType,
          mode: props.data.mode,
          signUp: props.data.signup,
          playerA: {
              sex: props.data.playerA.sex,
              hasMinAge: props.data.playerA.hasMinAge,
              minAge: props.data.playerA.minAge,
              hasMaxAge: props.data.playerA.hasMaxAge,
              maxAge: props.data.playerA.maxAge
          },
          playerB: {
              different: props.data.playerB.different,
              sex: props.data.playerB.sex,
              hasMinAge: props.data.playerB.hasMinAge,
              minAge: props.data.playerB.minAge,
              hasMaxAge: props.data.playerB.hasMaxAge,
              maxAge: props.data.playerB.maxAge
          }
      }
      emit('submit', server_data)
    } else {
      console.log('validation failed')
    }
  })
}

const checkAMin = (rule, value, callback) => {
    if (props.data.playerA.hasMinAge && props.data.playerA.minAge === null)
        callback(new Error(i18n.global.t("CompetitionSettings.missingAge")))
    callback()
}
const checkAMax = (rule, value, callback) => {
    if (props.data.playerA.hasMaxAge && props.data.playerA.maxAge === null)
        callback(new Error(i18n.global.t("CompetitionSettings.missingAge")))
    callback()
}
const checkBMin = (rule, value, callback) => {
    if (props.data.playerB.hasMinAge && props.data.playerB.minAge === null)
        callback(new Error(i18n.global.t("CompetitionSettings.missingAge")))
    callback()
}
const checkBMax = (rule, value, callback) => {
    if (props.data.playerB.hasMaxAge && props.data.playerB.maxAge === null)
        callback(new Error(i18n.global.t("CompetitionSettings.missingAge")))
    callback()
}
</script>

<style scoped>

.full-width {
  width: 100%;
}
</style>
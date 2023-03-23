<template>
  <div v-if="!registered" id="container">
    <div>
      <h2>
        {{ $t('ViewPlayerRegistration.headline') }}
      </h2>
    </div>
    <div id="form">
      <el-form
          ref="formRef"
          :model="data"
          label-position="top"
          label-width="120px"
          scroll-to-error="scroll-to-error"
      >
        <el-row :gutter="20" class="row-bg" justify="space-between">
          <el-col :span="12">
            <el-form-item
                :label="$t('ViewPlayerRegistration.first_name.field')"
                :rules="[
              {
                required: true,
                message: i18n.global.t('ViewPlayerRegistration.first_name.prompt'),
                trigger: 'blur',
              }
          ]"
                prop="firstName"
            >
              <el-input v-model="data.firstName"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item
                :label="$t('ViewPlayerRegistration.last_name.field')"
                :rules="[
              {
                required: true,
                message: i18n.global.t('ViewPlayerRegistration.last_name.prompt'),
                trigger: 'blur',
              }
          ]"
                prop="lastName"
            >
              <el-input v-model="data.lastName"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20" class="row-bg" justify="space-between">
          <el-col :span="12">
            <el-form-item
                :label="$t('ViewPlayerRegistration.sex.field')"
                :rules="[
              {
                required: true,
                message: i18n.global.t('ViewPlayerRegistration.sex.prompt'),
                trigger: 'blur',
          }]"
                prop="sex"
            >
              <el-select v-model="data.sex" :placeholder="$t('ViewPlayerRegistration.sex.select')">
                <el-option :label="$t('ViewPlayerRegistration.sex.Option1')" value="male"/>
                <el-option :label="$t('ViewPlayerRegistration.sex.Option2')" value="female"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item
                :label="$t('ViewPlayerRegistration.birthdate.field')"
                required>
              <el-form-item
                  :rules="[
                  {
                    type: 'date',
                    required: true,
                    message: i18n.global.t('ViewPlayerRegistration.birthdate.prompt'),
                    trigger: 'blur',
                  }
                  ]"
                  prop="birthday"
              >
                <el-date-picker
                    v-model="data.birthday"
                    type="date"
                />
              </el-form-item>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item
            :label="$t('ViewPlayerRegistration.email.field')"
            :rules="[
          {
            required: true,
            message: i18n.global.t('ViewPlayerRegistration.email.empty'),
            trigger: 'blur',
          },
          {
            type: 'email',
            message: i18n.global.t('ViewPlayerRegistration.email.correct'),
            trigger: ['blur', 'change'],
          },
        ]"
            prop="email"
        >
          <el-input v-model="data.email"/>
        </el-form-item>

        <el-form-item
            :label="$t('ViewPlayerRegistration.phone.field')"
            :rules="[
          {
            required: true,
            message: i18n.global.t('ViewPlayerRegistration.phone.empty'),
            trigger: 'blur',
          },
          {
            type: 'tel',
            message: i18n.global.t('ViewPlayerRegistration.phone.correct'),
            trigger: ['blur', 'change'],
          },
        ]"
            prop="phone"
        >
          <el-input v-model="data.phone"/>
        </el-form-item>

        <!-- TODO ich bin einverstanden, dass meine Daten gespeichert werden... (checkbox) -->

        <el-row class="row-bg" justify="end">
          <el-form-item>
            <el-button type="primary" @click="submitForm(formRef)">Submit</el-button>
          </el-form-item>
        </el-row>
      </el-form>
    </div>
  </div>
  <div v-else id="container">
    <h2>
      {{ $t('general.success') }}
    </h2>
    <p>
      {{ $t('ViewPlayerRegistration.after') }}
    </p>
  </div>
</template>

<script setup>

import {reactive, ref} from 'vue'
import {i18n} from "@/main";
import axios from "axios";
import {ElMessage} from "element-plus";

const formRef = ref()
const registered = ref(false)
const data = reactive({
  firstName: '',
  lastName: '',
  sex: '',
  birthday: null,
  email: '',
  phone: '',
})
const submitForm = (formEl) => {
  if (!formEl) return
  formEl.validate((valid) => {
    if (valid) {
      axios.post(`/player/registration`, data)
          .then((result) => {
            if (result.status === 200) {
              ElMessage.success(i18n.global.t("ViewPlayerRegistration.registration_successful"))
              registered.value = true
            } else {
              ElMessage.error(i18n.global.t("ViewPlayerRegistration.registration_failed"))
            }
          })
          .catch((error) => {
            console.log(error)
            ElMessage.error(i18n.global.t("ViewPlayerRegistration.registration_failed"))
          })
    } else {
      console.log('error submit!')
      return false
    }
  })
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

#container {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

</style>
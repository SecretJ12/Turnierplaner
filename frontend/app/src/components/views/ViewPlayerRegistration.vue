<script setup>
import {reactive, ref} from 'vue'
import {i18n} from "../../main";
import axios from "axios";


const formRef = ref()

const submitForm = (formEl) => {
  if (!formEl) return
  formEl.validate((valid) => {
    if (valid) {
      console.log('submit!')
      // axios.post()
    } else {
      console.log('error submit!')
      return false
    }
  })
}


const dynamicValidateForm = reactive({
  first_name: '',
  last_name: '',
  sex: '',
  birthdate: '',
  email: '',
})


const resetForm = (formEl) => {
  if (!formEl) return
  formEl.resetFields()
}
</script>


<template>
  <el-form
      ref="formRef"
      :model="dynamicValidateForm"
      label-width="120px"
      class="demo-dynamic"
  >
    <el-form-item
        :label="$t('ViewPlayerRegistration.first_name.field')"
        prop="first_name"
        :rules="[
            {
              required: true,
              message: i18n.global.t('ViewPlayerRegistration.first_name.prompt'),
              trigger: 'blur',
            }
        ]"
    >
      <el-input v-model="dynamicValidateForm.first_name"/>
    </el-form-item>
    <el-form-item
        :label="$t('ViewPlayerRegistration.last_name.field')"
        prop="last_name"
        :rules="[
            {
              required: true,
              message: i18n.global.t('ViewPlayerRegistration.last_name.prompt'),
              trigger: 'blur',
            }
        ]"
    >
      <el-input v-model="dynamicValidateForm.last_name"/>
    </el-form-item>

    <el-form-item
        :label="$t('ViewPlayerRegistration.sex.field')"
        prop="sex"
        :rules="[
            {
              required: true,
              message: i18n.global.t('ViewPlayerRegistration.sex.prompt'),
              trigger: 'blur',
        }]"
    >
      <el-select v-model="dynamicValidateForm.sex" :placeholder="$t('ViewPlayerRegistration.sex.select')">
        <el-option :label="$t('ViewPlayerRegistration.sex.Option1')" value="men" />
        <el-option :label="$t('ViewPlayerRegistration.sex.Option2')" value="woman" />
      </el-select >
    </el-form-item>

    <el-form-item
        label="Birthdate" required>
      <el-col :span="14">
        <el-form-item
            prop="birthdate"
            :rules="[
                {
                  type: 'date',
                  required: true,
                  message: i18n.global.t('ViewPlayerRegistration.birthdate.prompt'),
                 trigger: 'blur',
                }
                ]"
        >
          <el-date-picker
              v-model="dynamicValidateForm.birthdate"
              type="date"
              style="width: 100%"
          />
        </el-form-item>
      </el-col>
    </el-form-item>
    <el-form-item
        prop="email"
        label="Email"
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
    >
      <el-input v-model="dynamicValidateForm.email"/>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="submitForm(formRef)">Submit</el-button>
      <el-button @click="resetForm(formRef)">Reset</el-button>
    </el-form-item>
  </el-form>
</template>





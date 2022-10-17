<script setup>
import {reactive,ref} from 'vue'
import axios from "axios";

// import { useI18n } from 'vue-i18n';
// const {t}  = useI18n.t('title')

const birthdate = ref('')
const first_name = ref('')
const last_name = ref('')
const email = ref('')

const test = ref('')

function registration() {
  axios.post('/player/registration', {
    first_name: first_name.value,
    last_name: last_name.value,
    email : email.value,
    birthdate: birthdate.value
  })
      .then(function (response) {
        console.log(response);
      })
      .catch(function (error) {
        console.log(error);
      });
  first_name.value = ""
  last_name.value = ""
  birthdate.value = ""
}


const formSize = ref('default')
const ruleFormRef = ref('')
const ruleForm = reactive({
  first_name: '',
  last_name: '',
  sex: '',
  birthdate: '',
})

const rules = reactive({
  first_name: [
    { required: true, message: 'Please input Activity name', trigger: 'blur' },
    { min: 3, max: 30, message: 'Length should be 3 to 30', trigger: 'blur' },
  ],
  last_name: [
    { required: true, message: 'Please input Activity name', trigger: 'blur' },
    { min: 3, max: 30, message: 'Length should be 3 to 30', trigger: 'blur' },
  ],
  sex: [
    {
      required: true,
      message: "Please pick your sex",
      trigger: 'change',
    },
  ],
  birthdate: [
    {
      type: 'date',
      required: true,
      message: 'dasf',
      trigger: 'change',
    },
  ],
})

const submitForm = async (formEl) => {
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      console.log('submit!')
    } else {
      console.log('error submit!', fields)
    }
  })
}


const options = Array.from({ length: 10000 }).map((_, idx) => ({
  value: `${idx + 1}`,
  label: `${idx + 1}`,
}))


</script>


<template>
  <el-form
      ref="ruleFormRef"
      :model="ruleForm"
      :rules="rules"
      label-width="120px"
      class="demo-ruleForm"
      :size="formSize"
      status-icon
  >

    <el-form-item :label="$t('ViewPlayerRegistration.first_name')" prop="first_name">
      <el-input v-model="ruleForm.first_name" />
    </el-form-item>

    <el-form-item :label="$t('ViewPlayerRegistration.last_name')" prop="last_name">
      <el-input v-model="ruleForm.last_name" />
    </el-form-item>
    <el-form-item :label="$t('ViewPlayerRegistration.gender')" prop="sex">
      <el-select v-model="ruleForm.sex" placeholder=Please select your gender>
        <el-option label="Men" value="men" />
        <el-option label="Woman" value="woman" />
      </el-select>
    </el-form-item>
    <el-form-item :label="$t('ViewPlayerRegistration.birthdate')" prop="birthdate">
      <el-col :span="14">
        <el-form-item prop="date1">
          <el-date-picker
              v-model="ruleForm.birthdate"
              type="date"
              label="Pick a time afdsafdsaf"
              placeholder="Pick a date"
              style="width: 100%"
          />
        </el-form-item>
      </el-col>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="submitForm(ruleFormRef)">{{$t("ViewPlayerRegistration.sign_up")}}</el-button>
    </el-form-item>
  </el-form>

  <!--  <div>-->
  <!--    <form>-->
  <!--      <input v-model="first_name" placeholder="Vorname ">-->
  <!--      <input v-model="last_name" placeholder="Nachname">-->
  <!--      <input v-model="email" placeholder="Email">-->
  <!--  <el-date-picker-->
  <!--      v-model="birthdate"-->
  <!--      type="date"-->
  <!--      placeholder="Pick a day"-->
  <!--      :size="size"-->
  <!--  />-->
  <!--    </form>-->
  <!--    <button @click="registration"> Sign up</button>-->
  <!--  </div>-->
</template>



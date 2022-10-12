<script setup>
import {ref} from 'vue'
import axios from "axios";
import {router} from '/src/main'
import {useRoute} from 'vue-router'

const birthdate = ref('')
const first_name = ref('')
const last_name = ref('')


function registration() {
  axios.post('/player/registration', {
    first_name: first_name.value,
    last_name: last_name.value,
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

</script>


<template>
  <div>
    <form @submit.prevent="addEntry">
      <input v-model="first_name" placeholder="Vorname ">
    </form>
    <form @submit.prevent="addEntry">
      <input v-model="last_name" placeholder="Nachname">
    </form>
  </div>
  <el-date-picker
      v-model="birthdate"
      type="date"
      placeholder="Pick a day"
      :size="size"
  />
  <div>
    <button @click="registration"> Sign up</button>
  </div>
</template>

<style scoped>
.date-picker {
  display: flex;
  width: 100%;
  padding: 0;
  flex-wrap: wrap;
}

.date-picker .block {
  padding: 30px 0;
  text-align: center;
  border-right: solid 1px var(--el-border-color);
  flex: 1;
}

.date-picker .block:last-child {
  border-right: none;
}

.date-picker .demonstration {
  display: block;
  color: var(--el-text-color-secondary);
  font-size: 14px;
  margin-bottom: 20px;
}
</style>

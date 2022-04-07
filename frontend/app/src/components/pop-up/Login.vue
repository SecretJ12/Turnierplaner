<script setup lang="ts">
import {ref} from 'vue'
import axios from "axios";

const usernameInput = ref("");
const passwordInput = ref("")

const token = "";

let correctInput = false

const visibility = ref("anzeigen")

let passWordFieldType = ref("password");

const switchVisibility = () => {
  if (passWordFieldType.value === "password") {
    passWordFieldType.value = "text";
    visibility.value = "verstecken";
  } else {
    passWordFieldType.value = "password";
    visibility.value = "anzeigen";
  }
}

const sendLoginInformationToBackend = () => {
//  TODO send information to backend
  axios.get('/hello').then(
      response => {
        console.log(response.data)
      }
  ).catch(error => console.error(error))


  usernameInput.value = ""
  passwordInput.value = ""
}

</script>

<template>
  <input v-model="usernameInput">
  <input :type="passWordFieldType" v-model="passwordInput">
  <button @click="switchVisibility"> {{ visibility }}</button>
  <button @click="sendLoginInformationToBackend"> Login</button>
</template>


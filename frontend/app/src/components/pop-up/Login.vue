<script setup>
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
  axios.post('/login', {
          username: usernameInput.value,
          password: passwordInput.value
      }, {
        headers: {
          'Content-Type': 'application/json'
        }
      }
  ).then(function (response) {
    console.log(response)
  }).catch(function (error) {
    console.log(error)
  });

  usernameInput.value = ""
  passwordInput.value = ""
}

</script>

<template>
  <div>
    <h1>Login</h1>
    <input v-model="usernameInput">
    <br><br>
    <input :type="passWordFieldType" v-model="passwordInput">
    <button @click="switchVisibility"> {{ visibility }}</button>
    <br><br>
    <button @click="sendLoginInformationToBackend"> Login</button>

  </div>
</template>


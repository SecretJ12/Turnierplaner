<script setup lang="ts">
import {ref} from 'vue'

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

  usernameInput.value = ""
  passwordInput.value = ""
}

</script>

<template>
  <input v-model="usernameInput">
  <input :type="passWordFieldType" v-model="passwordInput">
  <button @click="switchVisibility"> {{ visibility }}</button>
  <button @click="sendLoginInformationToBackend"> Login</button>
  <div>
    <b-button v-b-modal.modal-1>Launch demo modal</b-button>

    <b-modal id="modal-1" title="BootstrapVue">
      <p class="my-4">Hello from modal!</p>
    </b-modal>
  </div>
</template>


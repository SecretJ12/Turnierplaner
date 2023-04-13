<template>
  <ul>
    <li>
      <Button @click="doUnauthorized">Unauthorized</Button>
      <p>{{ unauthorized }}</p></li>
    <li>
      <Button @click="doAuthorized">Authorized</Button>
      <p>{{ authorized }}</p></li>
    <li>
      <Button @click="doUser">User</Button>
      <p>{{ user }}</p></li>
    <li>
      <Button @click="doReporter">Reporter</Button>
      <p>{{ reporter }}</p></li>
    <li>
      <Button @click="doDirector">Director</Button>
      <p>{{ director }}</p></li>
    <li>
      <Button @click="doAdmin">Admin</Button>
      <p>{{ admin }}</p></li>
  </ul>
</template>

<script setup lang="ts">
import {ref} from "vue"
import axios from "axios"

const unauthorized = ref('');

function doUnauthorized() {
  axios.get("/template/unauthorized")
      .then((response) => {
        unauthorized.value = response.data
      })
      .catch((_) => {
        unauthorized.value = "failed"
      })
}

const authorized = ref('');

function doAuthorized() {
  axios.get("/template/authorized")
      .then((response) => {
        authorized.value = response.data
      })
      .catch((_) => {
        authorized.value = "failed"
      })
}

const user = ref('');

function doUser() {
  axios.get("/template/user")
      .then((response) => {
        user.value = response.data
      })
      .catch((_) => {
        user.value = "failed"
      })
}

const reporter = ref('');

function doReporter() {
  axios.get("/template/reporter")
      .then((response) => {
        reporter.value = response.data
      })
      .catch((_) => {
        reporter.value = "failed"
      })
}

const director = ref('');

function doDirector() {
  axios.get("/template/director")
      .then((response) => {
        director.value = response.data
      })
      .catch((_) => {
        director.value = "failed"
      })
}

const admin = ref('');

function doAdmin() {
  axios.get("/template/admin")
      .then((response) => {
        admin.value = response.data
      })
      .catch((_) => {
        admin.value = "failed"
      })
}

</script>

<style scoped>

</style>
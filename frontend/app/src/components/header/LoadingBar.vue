<template>
  <div v-if="loading" id="progress">
    <div id="progress-bar"></div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from "axios";

let loading = ref(false)

axios.interceptors.request.use(function (config) {
  loading.value = true
  return config
}, function (error) {
  loading.value = false
  return Promise.reject(error)
})
axios.interceptors.response.use(function (config) {
  loading.value = false
  return config
}, function (error) {
  loading.value = false
  return Promise.reject(error);
})
</script>

<style scoped>

#progress {
  margin: 0 0 -4px 0;
  height: 4px;
  background-color: lightblue; /* add8e6 */
}

#progress-bar {
  margin: 0;
  width: 0;
  height: 100%;
  background-color: #8db8c6; /* add8e6 */
  animation: load 0.8s linear infinite;
}

@keyframes load {
  0% {
    width: 0;
    margin-left: 0;
    margin-right: 100%;
  }

  50% {
    width: 100%;
    margin-left: 0;
    margin-right: 0;
  }

  100% {
    width: 0;
    margin-left: 100%;
    margin-right: 0;
  }
}

</style>
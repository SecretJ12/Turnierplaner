<template>
  <div v-if="loading > 0" id="progress">
    <div id="progress-bar"></div>
  </div>
</template>

<script lang="ts" setup>
import {ref} from "vue"
import axios from "axios"

let loading = ref(0)

axios.interceptors.request.use(function (config) {
	loading.value++
	return config
}, function (error) {
	loading.value++
	return Promise.reject(error)
})
axios.interceptors.response.use(function (config) {
	loading.value--
	return config
}, function (error) {
	loading.value--
	return Promise.reject(error)
})
</script>

<style scoped>

#progress {
  margin: 0 0 -6px 0;
  height: 6px;
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
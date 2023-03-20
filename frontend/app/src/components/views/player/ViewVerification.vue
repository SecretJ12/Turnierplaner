<template>
  <div id="container">
    <div >
      <h2 v-if="verified" >
        {{$t('general.success')}}
      </h2>
      <h2 v-else >
        {{$t('general.failure')}}
      </h2>
    </div>
    <p v-if="verified">
      {{$t('ViewPlayerRegistration.verification_success')}}
    </p>
    <p v-else>
      {{$t('ViewPlayerRegistration.verification_failed')}}
    </p>
  </div>
</template>

<script setup>
import {ref} from "vue";

console.log("helo world")
import axios from "axios";
const verified = checkVerificationCode()
const test = ref(false)
function checkVerificationCode(){
  let success = false;
  let verificationCode =new URL(location.href).searchParams.get('code')
  axios.post('/player/verification',verificationCode)
      .then((response) =>{
        success = response.status === 200
          }
      ).catch((error) => {
        success =  false
  })
  return success
}
</script>

<style scoped>
#container {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}
</style>
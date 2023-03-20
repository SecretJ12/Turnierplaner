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
import axios from "axios";
async function checkVerificationCode(){
  let verificationCode =new URL(location.href).searchParams.get('code')
  return axios.get('/player/verification?code='+verificationCode)
      .then((response) =>{
        return  response.status === 202
          }
      ).catch((_) => {
        return   false
  })
}
const verified = checkVerificationCode().then((res) => {return res})
</script>

<style scoped>
#container {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}
</style>
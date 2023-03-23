<template>
  <div id="container">
    <div v-if="verified">
      <h2 v-if="success">
        {{ $t('general.success') }}
      </h2>
      <h2 v-else>
        {{ $t('general.failure') }}
      </h2>
    </div>
    <p v-if="success">
      {{ $t('ViewPlayerRegistration.verification_success') }}
    </p>
    <p v-else>
      {{ $t('ViewPlayerRegistration.verification_failed') }}
    </p>
  </div>
</template>

<script setup>
import axios from "axios";
import {ElLoading} from 'element-plus';
import {ref} from 'vue'
import {useRoute} from "vue-router";
import {i18n} from "@/main";

const route = useRoute()

const loadingAnimation = ElLoading.service({
  lock: true,
  text: i18n.global.t("general.loading"),
  background: 'rgba(0, 0, 0, 0.7)'
})
const verified = ref(false)
const success = ref(false)


let verificationCode = new URL(location.href).searchParams.get('code')

axios.get(`/player/verification?code=${verificationCode}`)
    .then((response) => {
      success.value = response.status === 202
    })
    .catch((_) => {
    })
    .finally(() => {
      verified.value = true
      loadingAnimation.close()
    })
</script>

<style scoped>
#container {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}
</style>
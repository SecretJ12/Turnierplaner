<script setup>
import {ref} from 'vue'

import { router } from '/src/main'
import { useRoute } from 'vue-router'

const value1 = ref('')

function selected(competition) {
  router.push({path: "/player/registration"});
}

</script>

<template>
  <div>
    <button @click="selected"> Registration </button>
  </div>
</template>

<script setup>
import HeadContent from "@/components/header/HeadContent.vue";
import { ref, provide } from 'vue'

let aside = false;

import {access_token, auth} from "@/security/AuthService"
auth.silentLogin()


const loggedIn = ref(false);
provide('loggedIn', loggedIn)
auth.addUserLoadedListener(() => {
  updateToken()
  loggedIn.value = true
})
auth.addUserUnloadedListener(() => {
  updateToken()
  loggedIn.value = false
})

function updateToken() {
  auth.getUser().then((user) => {
    if (user !== null) {
      console.log("token loaded")
      access_token.value = user.access_token
    } else {
      console.log("token unloaded")
      access_token.value = null
    }
  });
}
</script>

<template>
  <HeadContent />

  <div id="body">
    <router-view />
    <aside v-if="aside">
      <h2>Aside content</h2>
    </aside>
  </div>
</template>

<style>
@import 'assets/base.css';
</style>

<style scoped>

#body {
  margin-top: 20px;
  display: flex;
}

main {
  flex-grow: 1;
}

aside {
  height: 100px;
  width: 400px;
  background-color: blue;
}

</style>


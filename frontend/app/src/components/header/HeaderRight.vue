<script setup>
import { auth } from '/src/security/AuthService'
import { ref, inject, watch } from 'vue'

const currentUser = ref('')
const isLoggedIn = inject('loggedIn', ref(false))

watch (isLoggedIn, async () => {
  auth.getUser().then((user) => {
    if (user !== null) {
      currentUser.value = user.profile.preferred_username;
    }
  });
})

function login() {
  auth.login();
}

function logout() {
  auth.logout();
}
</script>

<template>
  <div id="headerRight">
    <font-awesome-icon @click="login" v-if="!isLoggedIn" :icon="['fas', 'right-to-bracket']" class="fa-2x clickable" />
    <p v-if="isLoggedIn">{{ currentUser }}</p>
    <font-awesome-icon @click="logout" v-if="isLoggedIn" :icon="['fas', 'right-from-bracket']" class="fa-2x clickable" />
  </div>
</template>

<style>
  #headerRight {
    padding-right: 10px;
    display: flex;
    align-items: center;
    align-content: flex-end;
    flex-direction: row;
    justify-content: flex-end;
  }

  #headerRight > * {
    margin-right: 20px;
    flex-grow: 0;
    flex-shrink: 1;
  }

  .clickable {
    cursor: pointer;
  }
</style>
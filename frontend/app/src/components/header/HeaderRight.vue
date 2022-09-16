<script setup>
import { auth } from '/src/security/AuthService';
import { ref } from 'vue';

const currentUser = ref('');
const accessTokenExpired = ref(false);
const isLoggedIn = ref(false);

function login() {
  auth.login();
}

function logout() {
  auth.logout();
}

auth.addUserUpdateListener(() => {
  auth.getUser().then((user) => {
    if (user !== null) {
      currentUser.value = user.profile.preferred_username;
      accessTokenExpired.value = user.expired;
    }

    isLoggedIn.value = (user !== null && !user.expired);
  });
});
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
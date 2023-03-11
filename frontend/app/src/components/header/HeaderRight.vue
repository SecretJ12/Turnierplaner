<script setup>
import { auth } from '@/security/AuthService'
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

const test = ref("")

</script>

<script>
export default {
  name: 'locale-changer',
  data () {
    return { langs: ['en', 'en'] }
  }
}
</script>

<template>
  <div id="headerRight">
    <el-select id="language-select" v-model="$i18n.locale" placeholder="ab">
      <el-option
          v-for="locale in $i18n.availableLocales"
          :key="locale"
          :label="locale"
          :value="locale"
      />
    </el-select>
    <font-awesome-icon @click="login" v-if="!isLoggedIn" :icon="['fas', 'right-to-bracket']" class="fa-2x clickable" />
    <p v-if="isLoggedIn">{{ currentUser }}</p>
    <font-awesome-icon @click="logout" v-if="isLoggedIn" :icon="['fas', 'right-from-bracket']" class="fa-2x clickable" />
  </div>
</template>

<style>
  #language-select {
    width: 20px;
  }

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
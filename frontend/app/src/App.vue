<script setup lang="ts">
import { ref } from 'vue';
import HeadContent from "@/components/header/HeadContent.vue";
import Tournament from "@/components/views/ViewTournaments.vue";

let state = ref('tournament');
let aside = true;

let toResetTournaments = function() {}

function home() {
  state.value = 'tournament';
  toResetTournaments()
}

function getToResetTournaments(toReset: () => void) {
  toResetTournaments = toReset;
}
</script>

<template>
  <HeadContent @home="home"/>

  <div id="body">
    <main>
      <tournament ref="viewTournament" v-if="state === 'tournament'" @toReset="getToResetTournaments"/>
    </main>
    <aside v-if="aside">
      <h2>Aside content</h2>
    </aside>
  </div>
</template>

<style>
@import './assets/base.css';
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


<script setup lang="ts">
import { ref } from 'vue';
import HeadContent from "@/components/header/HeadContent.vue";
import tournament from "@/components/views/viewTournaments.vue";
import competition from "@/components/views/viewCompetitions.vue";
import Registration from "@/components/header/Registration.vue"

let state = ref('tournament');
let aside = true;

function clickedTourn() {
  state.value = 'competition';
}

function clickedComp() {
  state.value = 'registration';
}
</script>

<template>
  <HeadContent />

  <div id="body">
    <main>
      <tournament v-if="state === 'tournament'" @clicked="clickedTourn"/>
      <competition v-if="state === 'competition'" @clicked="clickedComp"/>
      <Registration v-if="state === 'registration'"/>
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


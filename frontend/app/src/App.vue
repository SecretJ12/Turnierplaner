<script setup>
import HeadContent from "./components/header/HeadContent.vue";

let aside = true;

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


<script setup lang="ts">
import HeadContent from "@/components/header/HeadContent.vue";
import Registration from "@/components/header/HeadContent.vue"
import test1 from "@/components/views/test1.vue";
import test2 from "@/components/views/test2.vue";

let state = 1;
</script>

<template>
  <HeadContent />

  <div id="body">
    <main>
      <Registration v-if="state === 1" />
      <test2 v-if="state === 2" />
    </main>
    <aside>
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
  flex-direction: row;
  justify-content: space-between;
}

main {
  height: 100px;
  background-color: red;
}

aside {
  height: 100px;
  background-color: blue;
}

</style>


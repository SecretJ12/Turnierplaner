<script setup lang="ts">
import { ref } from 'vue';
import item from '@/components/views/viewItem.vue';

const tournaments = ref([
  {title: "2022", description: "ganz viel Text"},
  {title: "2021", description: "noch mehr Text"},
  {title: "2020", description: "richtig, noch mehr text"}
])
</script>

<template>
  <div id="tournaments">
    <item v-for="tournament in tournaments" :key="tournament.title" :title="tournament.title" :description="tournament.description" />
  </div>
</template>

<style scoped>
  #tournaments {
    margin: 10px;
    display: flex;
    flex-wrap: wrap;
    flex-direction: row;
    justify-content: center;

  }

  #tournaments > * {
    margin: 0 10px 10px 10px;
  }
</style>
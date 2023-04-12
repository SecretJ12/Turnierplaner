<template>
  <ViewGroupSystem v-if="props.type === 'GROUPS'"/>
  <ViewKnockoutSystem v-else-if="props.type === 'KNOCKOUT'"/>
  <p v-else>Invalid type</p>
</template>

<script setup>
import ViewGroupSystem from "@/components/views/competition/ViewGroupSystem.vue";
import ViewKnockoutSystem from "@/components/views/competition/ViewKnockoutSystem.vue";

const props = defineProps({
  type: String
});
</script>

<style scoped>

</style>
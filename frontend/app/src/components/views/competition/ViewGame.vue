<template>
  <ViewGroupSystem v-if="props.tourType === TourType.GROUPS"/>
  <ViewKnockoutSystem v-else-if="props.tourType === TourType.KNOCKOUT"/>
  <p v-else>Invalid type</p>
</template>

<script lang="ts" setup>
import ViewGroupSystem from "@/components/views/competition/groupSystem/ViewGroupSystem.vue"
import ViewKnockoutSystem from "@/components/views/competition/knockoutSystem/ViewKnockoutSystem.vue"
import {TourType} from "@/interfaces/competition";

const props = defineProps<{
  tourType: TourType
}>()
</script>

<style scoped>

</style>
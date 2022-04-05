<script setup lang="ts">
import { ref } from 'vue';
import registration from '@/components/views/ViewRegistration.vue';
import item from '@/components/views/ItemCompetition.vue';

const competitions = ref([
  {idComp: "1", title: "Herren", description: "ganz viel Text"},
  {idComp: "2", title: "Damen", description: "noch mehr Text"},
  {idComp: "3", title: "U18", description: "richtig, noch mehr text"}
])

const props = defineProps({
  idTour: String
})

const emit = defineEmits(['clicked']);

const curCompetition = ref("");

function selected(competition: string) {
  curCompetition.value = competition
}
</script>

<template>
  <div v-if="curCompetition === ''">
    <h2>{{props.idTour}}</h2>
    <div id="competitions">
      <item v-for="competition in competitions" :key="competition.title" :idComp="competition.idComp" :title="competition.title" :description="competition.description" @selected="selected"/>
    </div>
  </div>
  <div id="showCompetition" v-else>
    <registration :idTour="props.idTour" :idComp="curCompetition"/>
  </div>
</template>

<style scoped>
#competitions {
  margin: 10px;
  display: flex;
  flex-wrap: wrap;
  flex-direction: row;
  justify-content: center;
}

#competitions > * {
  margin: 0 10px 10px 10px;
}

h2 {
  text-align: center;
}
</style>
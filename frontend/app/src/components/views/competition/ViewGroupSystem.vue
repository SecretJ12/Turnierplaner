<template id="container">
  <div class="container text-center" v-for="group in groupDetails.groups">
    <div>Group</div>
    <div class="row">
      <div class="col"></div>
      <div class="col" v-for="team in group.teams">
        <!-- TODO second player       -->
        <div>{{ team.playerA.firstName }} {{ team.playerA.lastName }}</div>
      </div>
    </div>
    <div class="row" v-for="team in group.teams">
      <!--      TODO second player-->
      <div class="col">{{ team.playerA.firstName }} {{ team.playerA.lastName }}</div>
      <div class="col" v-for="team in group.teams">
        <div></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {useRoute} from 'vue-router'
import axios from "axios";
import {i18n, router} from "@/main";
import {inject, reactive, ref, watch} from "vue";

const route = useRoute()
const groupDetails = reactive({
  groups: []
})

await axios.get(`tournament/${route.params.tourId}/competition/${route.params.compId}/groupMatches`)
    .then((response) => {
      groupDetails.groups = response.data.groups
    })
    .catch((_) => {
    })

console.log(groupDetails.groups)


</script>

<style lang="scss">

</style>
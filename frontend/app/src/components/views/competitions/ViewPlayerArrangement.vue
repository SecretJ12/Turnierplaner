<template>
  <div class="container" v-if="!completed">
    <h4>Competitions</h4>
    <p>Please finish the administration for all the following competitions</p>
    <ul class="list-group">
      <li class="list-group-item" v-for="(comp, index) in competitions">
        <div class="d-flex">
          <div class="p-2 flex-grow-1">
            <b> {{ comp.name }} </b> {{ comp.playerA.sex }}/
            {{ comp.playerB.sex }} - {{ comp.signUp }}
          </div>
          <div class="p-2">
            <input
              class="form-check-input"
              type="checkbox"
              value=""
              id="flexCheckChecked"
              :checked="checkbox[index]"
              onclick="return false;"
            />
          </div>
        </div>
      </li>
    </ul>
  </div>
  <div v-else>
    <h4>Time slots</h4>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from "vue-router";
import { ref } from "vue";
import axios from "axios";
import { router } from "@/main";
import { ElMessage } from "element-plus";
import {
  Competition,
  CompetitionServer,
  competitionServerToClient,
} from "@/interfaces/competition";
import { useI18n } from "vue-i18n";

const { t } = useI18n({ inheritLocale: true });

const route = useRoute();
const competitions = ref<Competition[]>([]);

const completed = ref<Boolean>(false);

// TODO show message when hovering over checkbox
const checkbox = ref<Boolean[]>([]);

await axios
  .get<CompetitionServer[]>(
    `/tournament/${route.params.tourId}/competition/list`,
  )
  .then((response) => {
    if (response.status === 200)
      competitions.value = response.data.map(competitionServerToClient);
    else {
      ElMessage.error(t("ViewCompetitions.loadingFailed"));
    }
  })
  .catch((error) => {
    ElMessage.error(t("ViewCompetitions.loadingFailed"));
    console.log(error);
    router.push("/");
  });

for (const comp of competitions.value) {
  checkbox.value.push(false);
}

checkbox.value[0] = true;

console.log(competitions.value);

function click() {}

// await axios
//   .get<CompetitionServer>(
//     `/tournament/${route.params.tourId}/competition/${route.params.compId}/details`
//   )
//   .then((response) => {
//     competition.value = competitionServerToClient(response.data);
//   })
//   .catch((error) => {
//     console.log(error);
//     ElMessage.error(t("ViewEditTournament.loadingDetailsFailed"));
//   });
//
// const tourType = ref<TourType | null | undefined>(competition.value?.tourType);
//
// const mode = ref<Mode | null | undefined>(competition.value?.mode);
</script>
<style scoped></style>

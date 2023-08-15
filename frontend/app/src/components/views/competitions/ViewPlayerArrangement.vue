<template>
  <ViewEditGroup v-if="tourType === TourType.GROUPS" />
  <h1 v-else>Type not implemented yet {{ tourType }}</h1>
</template>

<script setup lang="ts">
import { useRoute } from "vue-router";
import { inject, ref, watch } from "vue";
import { auth } from "@/security/AuthService";
import axios from "axios";
import { router } from "@/main";
import ViewSignUp from "@/components/views/competition/signup/ViewSignUp.vue";
import ViewGame from "@/components/views/competition/ViewGame.vue";
import { ElMessage } from "element-plus";
import { Mode, TourType } from "@/interfaces/competition";

import {
  Competition,
  CompetitionServer,
  competitionServerToClient,
} from "@/interfaces/competition";
import {
  Tournament,
  TournamentServer,
  tournamentServerToClient,
} from "@/interfaces/tournament";
import { useI18n } from "vue-i18n";
import ViewEditGroup from "@/components/views/competition/groupSystem/ViewEditGroup.vue";

const competition = ref<Competition | null>(null);
const { t } = useI18n({ inheritLocale: true });

const route = useRoute();
await axios
  .get<CompetitionServer>(
    `/tournament/${route.params.tourId}/competition/${route.params.compId}/details`
  )
  .then((response) => {
    competition.value = competitionServerToClient(response.data);
  })
  .catch((error) => {
    console.log(error);
    ElMessage.error(t("ViewEditTournament.loadingDetailsFailed"));
  });

const tourType = ref<TourType | null | undefined>(competition.value?.tourType);

const mode = ref<Mode | null | undefined>(competition.value?.mode);
</script>

<style scoped></style>

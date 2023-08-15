<template>
  <PlayerArrangement v-if="progress === 1 && statusActive === 'wait'" />
  <FormCompetition
    v-else-if="competition !== null"
    :competition="competition"
    :disabled="disabled"
    :submit-text="t('general.update')"
    @submit="submit"
  />
</template>

<script lang="ts" setup>
import { router } from "@/main";
import { ref } from "vue";
import axios from "axios";
import { ElMessage } from "element-plus";
import { useRoute } from "vue-router";
import FormCompetition from "@/components/views/competitions/ViewFormCompetition.vue";
import { useI18n } from "vue-i18n";
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
import PlayerArrangement from "@/components/views/competitions/ViewPlayerArrangement.vue";

const { t } = useI18n({ inheritLocale: true });

const route = useRoute();

const competition = ref<Competition | null>(null);

const disabled = ref(true);

const tournament = ref<Tournament | null>(null);
const progress = ref(0);
const statusActive = ref<"wait" | "process" | "success" | "error" | "loading">(
  "loading"
);

await axios
  .get<CompetitionServer>(
    `/tournament/${route.params.tourId}/competition/${route.params.compId}/details`
  )
  .then((response) => {
    competition.value = competitionServerToClient(response.data);
    disabled.value = false;
  })
  .catch((error) => {
    ElMessage.error(t("ViewEditCompetition.loadingDetailsFailed"));
    console.log(error);
    router.back();
  });

await axios
  .get<TournamentServer>(`/tournament/${route.params.tourId}/details`)
  .then((response) => {
    const date = new Date();
    tournament.value = tournamentServerToClient(response.data);

    if (date < tournament.value.registration_phase.begin) {
      progress.value = 0;
      statusActive.value = "wait";
    } else if (date < tournament.value.registration_phase.end) {
      progress.value = 0;
      statusActive.value = "process";
    } else if (date < tournament.value.game_phase.begin) {
      progress.value = 1;
      statusActive.value = "wait";
    } else if (date < tournament.value.game_phase.end) {
      progress.value = 1;
      statusActive.value = "process";
    } else {
      progress.value = 1;
      statusActive.value = "success";
    }
  })
  .catch((error) => {
    statusActive.value = "error";
    console.log(error);
  });

function submit(server_data: CompetitionServer) {
  axios
    .post(`/tournament/${route.params.tourId}/competition/update`, server_data)
    .then((_) => {
      ElMessage.success(t("ViewEditCompetition.saved"));
    })
    .catch((_) => {
      ElMessage.error(t("ViewEditCompetition.saving_failed"));
    });
}
</script>

<style scoped></style>

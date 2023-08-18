<template>
  <div class="container">
    <div class="row">
      <form class="row-sm-5 col-form-label">
        <h3 class="form-label">Select number of Groups</h3>
        <div class="form-check col-mb-3" id="radio">
          <input
            type="radio"
            class="btn-check"
            name="options-base"
            id="option5"
            autocomplete="off"
            value="1"
            v-model="radio.selectedChoice"
            :checked="radio.checked[0]"
          />
          <label class="btn" for="option5">One Group</label>
          <input
            type="radio"
            class="btn-check"
            name="options-base"
            id="option7"
            autocomplete="off"
            value="2"
            v-model="radio.selectedChoice"
            :checked="radio.checked[1]"
            :disabled="false"
          />
          <label class="btn" for="option7">Two Groups</label>
          <input
            type="radio"
            class="btn-check"
            name="options-base"
            id="option8"
            autocomplete="off"
            value="4"
            v-model="radio.selectedChoice"
            :checked="radio.checked[2]"
            :disabled="false"
          />
          <label class="btn" for="option8">Four Groups</label>
          <button class="btn btn-primary" type="submit" @click="apply">
            apply
          </button>
        </div>
      </form>
    </div>
    <div class="container">
      <div class="row">
        <div v-for="index in numberOfGroups" class="col-md-6">
          <div class="rounded group">
            <h6>
              <b>
                {{ groupNames[index - 1] }}
              </b>
            </h6>
            <draggable
              class="list-group"
              :list="groups[index - 1]"
              group="teams"
              itemKey="id"
            >
              <template #item="{ element, index }">
                <div class="list-group-item">
                  {{ element.playerA?.firstName }}
                  {{ element.playerA?.lastName }}
                  <br />
                  {{ element.playerB?.firstName }}
                  {{ element.playerB?.lastName }}
                </div>
              </template>
            </draggable>
            <!--            <ul class="list-group">-->
            <!--              <li-->
            <!--                v-for="team in groups[index - 1]"-->
            <!--                :key="team"-->
            <!--                class="list-group-item"-->
            <!--                draggable="true"-->
            <!--                @dragstart="dragstart_handler"-->
            <!--              >-->
            <!--                {{ team.playerA?.firstName }} {{ team.playerA?.lastName }}-->
            <!--                <br />-->
            <!--                {{ team.playerB?.firstName }} {{ team.playerB?.lastName }}-->
            <!--              </li>-->
            <!--            </ul>-->
          </div>
        </div>
      </div>
    </div>
    <div class="container">
      <div class="row-sm-5">
        <button class="btn btn-primary" @click="save">save</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import {
  GroupSystem,
  GroupSystemServer,
  groupSystemServerToClient,
} from "@/interfaces/groupSystem";
import axios from "axios";

import { useRoute } from "vue-router";
import { Team, TeamWithId } from "@/interfaces/registration/team";
import { ElMessage } from "element-plus";
import { useI18n } from "vue-i18n";
import draggable from "vuedraggable";
// import bootstrap from "bootstrap";

const route = useRoute();

const props = defineProps({
  competition: String,
});

const groupSystem = ref<GroupSystem | undefined>();

const teams = ref<Team[]>([]);
const radio = reactive<{
  checked: boolean[];
  selectedChoice: number;
}>({
  checked: [true, false, false],
  selectedChoice: 1,
});

const numberOfGroups = ref<number>(1);

const { t } = useI18n({ inheritLocale: true });

const groups = ref<TeamWithId[][]>([[], [], [], []]);
const groupNames = [
  "First Group",
  "Second Group",
  "Third Group",
  "Fourth Group",
];

await axios
  .get<Team[]>(
    `/tournament/${route.params.tourId}/competition/${props.competition}/signedUpTeams`,
  )
  .then((response) => {
    teams.value = response.data;
  })
  .catch((error) => {
    teams.value = [];
    ElMessage.error(t("ViewCompetition.query_player_failed"));
    console.log(error);
  });

await axios
  .get<GroupSystemServer>(
    `tournament/${route.params.tourId}/competition/${route.params.compId}/groupMatches`,
  )
  .then((response) => {
    // TODO needed? maybe remove later
    groupSystem.value = groupSystemServerToClient(response.data);
    numberOfGroups.value = groupSystem.value?.groups.length || 1;
    let i: number = 0;
    for (const group of groupSystem.value?.groups) {
      for (const team of group.teams) {
        groups.value[i].push({
          id: i,
          playerA: team.playerA,
          playerB: team.playerB,
        });
      }
      i++;
    }

    radio.selectedChoice = numberOfGroups.value;
    switch (numberOfGroups.value) {
      case 1:
        radio.checked = [true, false, false];
        break;
      case 2:
        radio.checked = [false, true, false];
        break;
      case 4:
        radio.checked = [false, false, true];
        break;
      default:
        throw new Error("Invalid number of groups");
    }
  })
  .catch((_) => {});

if (!groupSystem.value || groupSystem.value?.groups.length === 0) {
  assignTeamsToGroups();
}

console.log(groups.value);
console.log(numberOfGroups.value);
console.log(radio.selectedChoice);

function assignTeamsToGroups() {
  let i: number = 0;
  const newGroups: TeamWithId[][] = [[], [], [], []];
  for (const team of teams.value) {
    newGroups[i % numberOfGroups.value].push({
      id: i,
      playerA: team.playerA,
      playerB: team.playerB,
    });
    i++;
    i %= numberOfGroups.value;
  }
  groups.value = newGroups;
  console.log("Groups: ", groups.value);
}

function apply(e: HTMLFormElement | undefined) {
  if (e === undefined) return;
  e.preventDefault();

  // TODO weird behaviour when leaving parseInt out (This bug was so annoying....)
  numberOfGroups.value = parseInt(radio.selectedChoice);

  assignTeamsToGroups();
  console.log(numberOfGroups.value);
  console.log(radio.selectedChoice);
}

function save() {
  // axios.post();
}
</script>
<style scoped>
.group {
  padding: 10px;
}
</style>

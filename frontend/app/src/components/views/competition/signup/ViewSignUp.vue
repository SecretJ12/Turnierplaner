<template>
    <el-space direction="vertical" fill>
        <el-descriptions border :column="windowWidth < 600 ? 1 : 2"
            :direction="windowWidth < 400 ? 'vertical' : 'horizontal'">
            <el-descriptions-item :span="windowWidth < 600 ? 1 : 2">
                <template #label>
                    {{ t("general.description") }}
                </template>
                {{ props.competition.description }}
            </el-descriptions-item>
            <el-descriptions-item>
                <template #label>
                    {{ t("ViewCompetition.tournament_system") }}
                </template>
                {{ t("CompetitionSettings." + props.competition.tourType.toLowerCase()) }}
            </el-descriptions-item>
            <el-descriptions-item>
                <template #label>
                    {{ t("ViewCompetition.game_mode") }}
                </template>
                {{ t("CompetitionSettings." + props.competition.mode.toLowerCase()) }}
            </el-descriptions-item>
        </el-descriptions>

        <el-space />
        <el-space />
        <el-space />

        <!-- Show registration is over -->
        <p v-if="!allowRegistration" style="text-align: center">
            {{ t("ViewCompetition.registration_over") }}
        </p>
        <template v-else>

            <ViewSignUpForm :beginGamePhase="props.beginGamePhase" :competition="props.competition"
                @registered="childUpdate" />

            <ViewRegistrationNotice v-if="false" :compDetails="props.competition" />
        </template>

        <ViewTable :competition="props.competition" :update="updateChildren" />

        <!-- TODO add options for admin -->
    </el-space>
</template>

<script setup lang="ts">
import {inject, ref, watch} from "vue"
import {auth} from "@/security/AuthService"
import axios from "axios"
import {useRoute} from "vue-router"
import ViewTable from "@/components/views/competition/signup/ViewTable.vue"
import ViewRegistrationNotice from "@/components/views/competition/signup/ViewRegistrationNotice.vue"
import ViewSignUpForm from "@/components/views/competition/signup/ViewSignUpForm.vue"
import {Competition} from "@/interfaces/competition";
import {useI18n} from "vue-i18n"
const { t } = useI18n({inheritLocale: true})

let windowWidth = ref(window.innerWidth)
window.addEventListener('resize', () => {
    windowWidth.value = window.innerWidth
})

const props = defineProps<{
    allowRegistration: boolean,
    beginGamePhase: Date,
    competition: Competition
    }>()

const route = useRoute()
const isLoggedIn = inject('loggedIn', ref(false))
const canEdit = ref(false)

const updateChildren = ref(0)
watch(isLoggedIn, async () => {
  update()
})
update()

function update() {
  canEdit.value = false
  auth.getUser().then((user) => {
    if (user !== null) {
      axios.get<boolean>(`/tournament/${route.params.tourId}/competition/canEdit`)
          .then((response) => {
            canEdit.value = response.data
          })
          .catch((_) => {
            canEdit.value = false
          })
    }
  })

  updateChildren.value++
}
function childUpdate() {
    updateChildren.value++
}
</script>

<style scoped>
</style>
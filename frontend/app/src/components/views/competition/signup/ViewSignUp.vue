<template>
    <el-space direction="vertical" fill>
        <!-- Show registration is over -->
        <p v-if="!allowRegistration" style="text-align: center">
            {{ i18n.global.t("ViewCompetition.registration_over") }}
        </p>
        <template v-else>

            <ViewSignUpForm :compDetails="props.compDetails"
                @registered="childUpdate" />

            <ViewRegistrationNotice v-if="false" :compDetails="props.compDetails" />
        </template>

        <ViewTable :compDetails="props.compDetails" :update="updateChildren" />

        <!-- TODO add options for admin -->
    </el-space>
</template>

<script setup>
import {inject, ref, watch} from "vue"
import {i18n} from '@/main'
import {auth} from "@/security/AuthService";
import axios from "axios";
import {useRoute} from "vue-router";
import ViewTable from "@/components/views/competition/signup/ViewTable.vue";
import ViewRegistrationNotice from "@/components/views/competition/signup/ViewRegistrationNotice.vue";
import ViewSignUpForm from "@/components/views/competition/signup/ViewSignUpForm.vue";

const props = defineProps({
    allowRegistration: Boolean,
    compDetails: {
        type: Object,
        name: String,
        description: String,
        tourType: String,
        mode: String,
        signup: String,
        playerA: {
            type: Object,
            sex: String,
            hasMinAge: Boolean,
            minAge: Date,
            hasMaxAge: Boolean,
            maxAge: Date
        },
        playerB: {
            type: Object,
            different: Boolean,
            sex: String,
            hasMinAge: Boolean,
            minAge: Date,
            hasMaxAge: Boolean,
            maxAge: Date
        }
    }
})

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
      axios.get(`/tournament/${route.params.tourId}/competition/canEdit`)
          .then((response) => {
            canEdit.value = response.data
          })
          .catch((_) => {
            canEdit.value = false
          })
    }
  });

  updateChildren.value++
}
function childUpdate() {
    updateChildren.value++
}

</script>

<style scoped>
</style>
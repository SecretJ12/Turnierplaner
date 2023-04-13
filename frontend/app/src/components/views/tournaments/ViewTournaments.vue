<template>
    <div id="tournaments">
        <item v-for="tournament in tournaments" :key="tournament.name"
              :tournament="tournament"
              :canCreate=canCreate
              @selected="selected"
              @settings="settingsItem"/>
        <AddItem v-if="canCreate" @selected="addTournament"/>
    </div>
</template>

<script setup lang="ts">
import {inject, ref, watch} from 'vue'
import Item from '../../items/ItemTournament.vue'
import AddItem from '../../items/ItemAdd.vue'
import {router} from '@/main'
import axios from "axios"
import {auth} from "@/security/AuthService"
import {ElMessage} from "element-plus"
import {Tournament, TournamentServer, tournamentServerToClient} from "@/interfaces/tournament";
import {useI18n} from "vue-i18n"
const { t } = useI18n({inheritLocale: true})

const tournaments = ref<Tournament[]>([])

const isLoggedIn = inject('loggedIn', ref(false))
const canCreate = ref<boolean>(false)

watch(isLoggedIn, async () => {
    update()
})
update()

function update() {
    canCreate.value = false
    axios.get<TournamentServer[]>('/tournament/list')
        .then((response) => {
            tournaments.value = response.data.map(tournamentServerToClient)
        })
        .catch((error) => {
            ElMessage.error(t("ViewTournaments.loadingFailed"))
            console.log(error)
        })
    auth.getUser().then((user) => {
        if (user !== null) {
            axios.get<boolean>('/tournament/canCreate')
                .then((response) => {
                    canCreate.value = response.data
                })
                .catch((_) => {
                    canCreate.value = false
                })
        }
    })
}

function selected(tournament: string) {
    router.push({path: '/tournament/' + tournament})
}

function settingsItem(tournament: string) {
    router.push({path: '/tournament/' + tournament + '/edit'})
}

function addTournament() {
    router.push({path: '/createTournament'})
}
</script>

<style scoped>
#tournaments {
    width: calc(100% - 20px);
    margin: 10px;
    display: flex;
    flex-wrap: wrap;
    flex-direction: row;
    justify-content: center;
}

#tournaments > * {
    margin: 0 10px 20px 10px;
}
</style>

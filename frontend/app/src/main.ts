import {createApp} from 'vue'
import App from './App.vue'
import {createI18n} from "vue-i18n"
import * as VueRouter from 'vue-router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

/* import the fontawesome core */
import {settings} from "@/settings"

/* https request */
import VueAxios from 'vue-axios'
import axios from 'axios'

import {access_token} from '@/security/AuthService'
import {library} from '@fortawesome/fontawesome-svg-core'
/* import font awesome icon component */
import {FontAwesomeIcon} from '@fortawesome/vue-fontawesome'
/* import specific icons */
import {
    faEyeSlash,
    faFlag,
    faGear,
    faPlus,
    faRightFromBracket,
    faRightToBracket,
    faTableCellsLarge,
    faUserGroup,
    faUserSecret
} from '@fortawesome/free-solid-svg-icons'
import languages from './i18n'
import viewTournaments from '@/components/views/tournaments/ViewTournaments.vue'
import createTournament from '@/components/views/tournaments/ViewCreateTournament.vue'
import editTournament from '@/components/views/tournaments/ViewEditTournament.vue'

import viewCompetitions from '@/components/views/competitions/ViewCompetitions.vue'
import createCompetition from '@/components/views/competitions/ViewCreateCompetition.vue'
import editCompetition from '@/components/views/competitions/ViewEditCompetition.vue'

import viewCompetition from "@/components/views/competition/ViewCompetition.vue"

import viewPlayerRegistration from '@/components/views/player/ViewPlayerRegistration.vue'

import viewTemplates from '@/components/views/ViewTemplates.vue'
import viewVerification from "@/components/views/player/ViewVerification.vue"
// Bootstrap
import './scss/style.scss'

axios.defaults.baseURL = settings.BACKEND

axios.interceptors.request.use(function (config) {
    if (access_token.value !== null)
        config.headers.Authorization = `Bearer ${access_token.value}`
    return config
}, function (error) {
    return Promise.reject(error)
})

/* add icons to the library */
library.add(faFlag, faRightToBracket, faRightFromBracket, faTableCellsLarge, faUserGroup, faPlus, faGear, faEyeSlash, faUserSecret)


const messages = Object.assign(languages)
const i18n = createI18n({
    locale: 'de', // set locale
    fallbackLocale: 'en', // set fallback locale
    warnHtmlMessage: false,
    missingWarn: false,
    fallbackWarn: false,
    messages,
    legacy: false
})

/* add font awesome icon component */
const app = createApp(App)

const routes = [
    {
        path: "/",
        name: "Tournaments",
        component: viewTournaments
    },
    {
        path: "/createTournament",
        name: "Create tournament",
        component: createTournament
    },
    {
        path: "/tournament/:tourId/edit",
        name: "Edit tournament",
        component: editTournament
    },
    {
        path: "/tournament/:tourId",
        name: "Competitions",
        component: viewCompetitions
    },
    {
        path: "/tournament/:tourId/createCompetition",
        name: "Create competition",
        component: createCompetition
    },
    {
        path: "/tournament/:tourId/competition/:compId/edit",
        name: "Edit competition",
        component: editCompetition
    },
    {
        path: "/tournament/:tourId/competition/:compId",
        name: "Competition",
        component: viewCompetition
    },
    {
        path: "/player/registration",
        name: "Player Registration",
        component: viewPlayerRegistration
    },
    {
        path: "/player/verification",
        name: "Player verified",
        component: viewVerification
    },
    {
        path: "/templates",
        name: "Templates",
        component: viewTemplates
    }
]

const router = VueRouter.createRouter({
    history: VueRouter.createWebHashHistory(),
    routes
})

app
    .use(i18n)
    .component('font-awesome-icon', FontAwesomeIcon)
    .use(VueAxios, axios)
    .use(router)
    .use(ElementPlus)
    .mount('#app')

export {router}

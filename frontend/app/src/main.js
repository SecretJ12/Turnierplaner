import { createApp } from 'vue'
import App from './App.vue'
import { createI18n } from "vue-i18n"
import * as VueRouter from 'vue-router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

/* import the fontawesome core */
import { library } from '@fortawesome/fontawesome-svg-core'

/* https request */
import VueAxios from 'vue-axios'
import axios from 'axios'

import { access_token } from '@/security/AuthService'
axios.defaults.baseURL = 'http://localhost:2000'

axios.interceptors.request.use(function (config) {
    if (access_token.value !== null)
        config.headers.Authorization = `Bearer ${access_token.value}`
    return config;
}, function (error) {
    return Promise.reject(error);
});

/* import specific icons */
import { faFlag, faRightToBracket, faRightFromBracket, faTableCellsLarge, faUserGroup, faPlus, faGear, faEyeSlash }
    from '@fortawesome/free-solid-svg-icons'
import { } from '@fortawesome/free-regular-svg-icons'

/* add icons to the library */
library.add(faFlag, faRightToBracket, faRightFromBracket, faTableCellsLarge, faUserGroup, faPlus, faGear, faEyeSlash)

/* import font awesome icon component */
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'

import languages from './i18n';

const messages = Object.assign(languages);
const i18n = createI18n({
    locale: 'de', // set locale
    fallbackLocale: 'en', // set fallback locale
    messages,
    legacy: false
})

/* add font awesome icon component */
const app = createApp(App);

/* add alle global variables here */
app.config.globalProperties.backend = "http://localhost:2000"

import viewTournaments from '@/components/views/tournaments/ViewTournaments.vue'
import viewCompetitions from '@/components/views/competitions/ViewCompetitions.vue'
import viewRegistration from '@/components/views/ViewRegistration.vue'
import viewTemplates from '@/components/views/ViewTemplates.vue'
import viewPlayerRegistration from '@/components/views/ViewPlayerRegistration.vue'
import createTournament from '@/components/views/tournaments/ViewCreateTournament.vue'
import editTournament from '@/components/views/tournaments/ViewEditTournament.vue'
import createCompetition from '@/components/views/competitions/ViewCreateCompetition.vue'
import editCompetition from '@/components/views/competitions/ViewEditCompetition.vue'

const routes = [
    {
        path: "/",
        name: "Home",
        component: viewTournaments
    },
    {
        path: "/tournament/:tourId",
        name: "Tournaments",
        component: viewCompetitions
    },
    {
        path: "/tournament/:tourId/competition/:compId",
        name: "Competition",
        component: viewRegistration
    },
    {
        path: "/templates",
        name: "Templates",
        component: viewTemplates
    },
    {
        path: "/player/registration",
        name: "Player Registration",
        component: viewPlayerRegistration
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
        path: "/tournament/:tourId/createCompetition",
        name: "Create competition",
        component: createCompetition
    },
    {
        path: "/tournament/:tourId/competition/:compId/edit",
        name: "Edit competition",
        component: editCompetition
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

export { router, i18n }

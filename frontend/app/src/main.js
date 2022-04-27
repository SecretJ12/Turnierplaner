import {createApp} from 'vue'
import App from './App.vue'
import {createI18n} from "vue-i18n"
import * as VueRouter from 'vue-router'

/* import the fontawesome core */
import {library} from '@fortawesome/fontawesome-svg-core'

/* https request */
import VueAxios from 'vue-axios'
import axios from 'axios'

axios.defaults.baseURL = 'http://localhost:2000'

/* import specific icons */
import {faFlag, faRightToBracket} from '@fortawesome/free-solid-svg-icons'
import {} from '@fortawesome/free-regular-svg-icons'

/* add icons to the library */
library.add(faFlag, faRightToBracket)

/* import font awesome icon component */
import {FontAwesomeIcon} from '@fortawesome/vue-fontawesome'

import languages from './i18n';

const messages = Object.assign(languages);
const i18n = createI18n({
    locale: 'de', // set locale
    fallbackLocale: 'en', // set fallback locale
    messages
})

/* add font awesome icon component */
const app = createApp(App);

/* add alle global variables here */
app.config.globalProperties.backend = "http://localhost:2000"

import viewTournaments from './components/views/ViewTournaments.vue'
import viewCompetitions from './components/views/ViewCompetitions.vue'
import viewRegistration from './components/views/ViewRegistration.vue'

const routes = [
    {
        path: "/",
        name: "Home",
        component: viewTournaments
    },
    {
        path: "/tournaments/:tourId",
        name: "Tournaments",
        component: viewCompetitions
    },
    {
        path: "/tournaments/:tourId/competitions/:compId",
        name: "Competition",
        component: viewRegistration
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
    .mount('#app')

export {router}
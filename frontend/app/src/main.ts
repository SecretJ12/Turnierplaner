import { createApp } from "vue"
import App from "./App.vue"
import { createI18n, I18nOptions } from "vue-i18n"
import * as VueRouter from "vue-router"

/* import the fontawesome core */
import { settings } from "@/settings"

/* https request */
import VueAxios from "vue-axios"
import axios from "axios"

import { access_token } from "@/security/AuthService"
import { library } from "@fortawesome/fontawesome-svg-core"
/* import font awesome icon component */
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
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
	faUserSecret,
} from "@fortawesome/free-solid-svg-icons"

axios.defaults.baseURL = settings.BACKEND

axios.interceptors.request.use(
	function (config) {
		if (access_token.value !== null)
			config.headers.Authorization = `Bearer ${access_token.value}`
		return config
	},
	function (error) {
		return Promise.reject(error)
	},
)

/* add icons to the library */
library.add(
	faFlag,
	faRightToBracket,
	faRightFromBracket,
	faTableCellsLarge,
	faUserGroup,
	faPlus,
	faGear,
	faEyeSlash,
	faUserSecret,
)

/* i18n */
import languages from "./i18n"
const messages = languages

export type MessageSchema = (typeof messages)["de"]

declare module "vue-i18n" {
	export interface DefineLocaleMessage extends MessageSchema {}
}

const options: I18nOptions = {
	locale: "de", // set locale
	fallbackLocale: "en", // set fallback locale
	warnHtmlMessage: false,
	missingWarn: false,
	fallbackWarn: false,
	messages,
	legacy: false,
}

const i18n = createI18n<false, typeof options>(options)

/* add font awesome icon component */
const app = createApp(App)

import routes from "./routes"

const router = VueRouter.createRouter({
	history: VueRouter.createWebHashHistory(),
	routes,
})

import "element-plus/dist/index.css"
import PrimeVue from "primevue/config"
import 'primeicons/primeicons.css';
import "primevue/resources/themes/md-light-indigo/theme.css"

app
	.use(i18n)
	.component("font-awesome-icon", FontAwesomeIcon)
	.use(VueAxios, axios)
	.use(PrimeVue)
	.use(router)
	.mount("#app")

export { router }

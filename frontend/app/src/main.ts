import { createApp } from "vue"
import App from "./App.vue"
import { createI18n } from "vue-i18n"
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
import languages from "./i18n"
// Bootstrap
import "./scss/style.scss"

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

const messages = Object.assign(languages)
const i18n = createI18n({
	locale: "de", // set locale
	fallbackLocale: "en", // set fallback locale
	warnHtmlMessage: false,
	missingWarn: false,
	fallbackWarn: false,
	messages,
	legacy: false,
})

/* add font awesome icon component */
const app = createApp(App)

import routes from "./routes"

const router = VueRouter.createRouter({
	history: VueRouter.createWebHashHistory(),
	routes,
})

import "element-plus/dist/index.css"

app
	.use(i18n)
	.component("font-awesome-icon", FontAwesomeIcon)
	.use(VueAxios, axios)
	.use(router)
	.mount("#app")

export { router }

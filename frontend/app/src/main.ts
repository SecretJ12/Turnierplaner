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
	// @ts-ignore
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

/* Primevue components*/
import PrimeVue from "primevue/config"
import ToastService from "primevue/toastservice"
import Tooltip from "primevue/tooltip"
import FocusTrap from "primevue/focustrap"
import Steps from "primevue/steps"
import TabView from "primevue/tabview"
import TabPanel from "primevue/tabpanel"
import TabMenu from "primevue/tabmenu"
import Card from "primevue/card"
import Button from "primevue/button"
import Toolbar from "primevue/toolbar"
import InputText from "primevue/inputtext"
import Textarea from "primevue/textarea"
import Calendar from "primevue/calendar"
import InputSwitch from "primevue/inputswitch"
import Dropdown from "primevue/dropdown"
import Divider from "primevue/divider"
import InlineMessage from "primevue/inlinemessage"
import Toast from "primevue/toast"
import SelectButton from "primevue/selectbutton"
import Skeleton from "primevue/skeleton"
import Panel from "primevue/panel"
import ScrollPanel from "primevue/scrollpanel"
import DataTable from "primevue/datatable"
import Column from "primevue/column"
import Tag from "primevue/tag"
import AutoComplete from "primevue/autocomplete"
import Checkbox from "primevue/checkbox"
import Fieldset from "primevue/fieldset"
import SplitButton from "primevue/splitbutton"
import OverlayPanel from "primevue/overlaypanel"
import Dialog from "primevue/dialog"

/* Primevue styling */
import "primeflex/primeflex.css"
import "primevue/resources/themes/lara-light-blue/theme.css"
import "primeicons/primeicons.css"
import "./style.css"

/* Icons */
import "material-icons/iconfont/material-icons.css"
import "material-symbols"

/* yup validation */
import { setLocale } from "yup"

setLocale({
	// use constant translation keys for messages without values
	mixed: {
		default: "field_invalid",
		required: "validation.field_required",
	},
	// use functions to generate an error object that includes the value from the schema
	string: {
		min: "validation.field_too_short",
		max: "validation.field_too_big",
	},
})

import { VueQueryPlugin } from 'vue-query'

app
	.use(i18n)
	.use(VueQueryPlugin)
	.use(VueAxios, axios)
	.use(router)
	/* Primevue */
	.use(PrimeVue, { ripple: true })
	.use(ToastService)
	.component("Steps", Steps)
	.component("TabView", TabView)
	.component("TabMenu", TabMenu)
	.component("TabPanel", TabPanel)
	.component("Card", Card)
	.component("Button", Button)
	.component("Toolbar", Toolbar)
	.component("InputText", InputText)
	.component("Textarea", Textarea)
	.component("Calendar", Calendar)
	.component("InputSwitch", InputSwitch)
	.component("Dropdown", Dropdown)
	.component("Divider", Divider)
	.component("InlineMessage", InlineMessage)
	.component("Toast", Toast)
	.component("SelectButton", SelectButton)
	.component("Skeleton", Skeleton)
	.component("Panel", Panel)
	.component("ScrollPanel", ScrollPanel)
	.component("DataTable", DataTable)
	.component("Column", Column)
	.component("Tag", Tag)
	.component("AutoComplete", AutoComplete)
	.component("Checkbox", Checkbox)
	.component("Fieldset", Fieldset)
	.component("SplitButton", SplitButton)
	.component("OverlayPanel", OverlayPanel)
	.component("Dialog", Dialog)
	.directive("tooltip", Tooltip)
	.directive("focustrap", FocusTrap)
	.mount("#app")

export { router }

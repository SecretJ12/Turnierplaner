import {User, UserManager} from "oidc-client-ts"
import {auth_settings, popup} from "./settings"
import {ref} from "vue"
import {ElLoading} from "element-plus"
import {ComposerTranslation} from "vue-i18n"

class AuthService {
	userManager

	constructor() {
		this.userManager = new UserManager(auth_settings)
	}

	silentLogin(t: ComposerTranslation) {
		return new Promise<void>((resolve, reject) => {
			this.userManager.getUser().then((user) => {
				if (user != null && !user.expired) {
					const loadingAnimation = ElLoading.service({
						lock: true,
						text: t("general.loading"),
						background: "rgba(0, 0, 0, 0.7)"
					})
					this.userManager.signinSilent()
						.then(() => {
							console.log("update token silently")
							access_token.value = user.access_token
							loadingAnimation.close()
							resolve()
						})
						.catch(() => {
							access_token.value = null
							loadingAnimation.close()
							reject()
						})
				} else {
					resolve()
				}
			})
		})
	}

	getUser() {
		return this.userManager.getUser()
	}

	login() {
		if (popup)
			return this.userManager.signinPopup()
		else
			return this.userManager.signinRedirect()
	}

	logout() {
		access_token.value = null
		if (popup)
			return this.userManager.signoutPopup()
		else
			return this.userManager.signoutRedirect()
	}

	addUserLoadedListener(cb: (user: User) => void) {
		this.userManager.events.addUserLoaded(cb)
	}

	addUserUnloadedListener(cb: () => void) {
		this.userManager.events.addUserUnloaded(cb)
	}
}

export const auth = new AuthService()
export const access_token = ref<string | null>(null)

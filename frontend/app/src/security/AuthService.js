import {UserManager} from "oidc-client-ts";
import {auth_settings, popup} from "./settings";
import {ref} from 'vue'
import {ElLoading} from "element-plus";
import {i18n} from "@/main";

class AuthService {
    userManager

    constructor() {
        this.userManager = new UserManager(auth_settings)
    }

    silentLogin() {
        return new Promise((resolve, reject) => {
            this.userManager.getUser().then((user) => {
                if (user != null && !user.expired) {
                    const loadingAnimation = ElLoading.service({
                        lock: true,
                        text: i18n.global.t("general.loading"),
                        background: 'rgba(0, 0, 0, 0.7)'
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
                } else
                    resolve()
            })
        })
    }

    getUser() {
        return this.userManager.getUser()
    }

    getName() {
        return this.userManager.getName()
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

    addUserLoadedListener(cb) {
        this.userManager.events.addUserLoaded(cb)
    }

    addUserUnloadedListener(cb) {
        this.userManager.events.addUserUnloaded(cb)
    }
}

export const auth = new AuthService()
export const access_token = ref(null)

import { UserManager } from "oidc-client-ts";
import { auth_settings, popup } from "./settings";
import { ref } from 'vue'

class AuthService {
    userManager

    constructor() {
        this.userManager = new UserManager(auth_settings)
    }

    silentLogin() {
        this.userManager.getUser().then((user) => {
            if (user != null && !user.expired)
                this.userManager.signinSilent()
                    .then(() => {
                        console.log("successfully logged in")
                    })
                    .catch(() => {
                        access_token.value = null
                    })
            // already load before logging in to avoid errors
            if (user !== null)
                access_token.value = user.access_token
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

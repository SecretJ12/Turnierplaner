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

auth.addUserLoadedListener(updateToken)
auth.addUserUnloadedListener(updateToken)

function updateToken() {
    auth.getUser().then((user) => {
        if (user !== null) {
            console.log("token loaded")
            access_token.value = user.access_token
        } else {
            console.log("token unloaded")
            access_token.value = null
        }
    });
}
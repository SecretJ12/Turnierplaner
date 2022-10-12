import { UserManager } from "oidc-client-ts";
import { settings, popup } from "./settings";
import { ref } from 'vue'

class AuthService {
    userManager;

    constructor() {
        this.userManager = new UserManager(settings);
    }

    silentLogin() {
        this.userManager.signinSilent()
            .then(r => {
                console.log("successfully logged in")
            })
    }

    getUser() {
        return this.userManager.getUser();
    }

    getName() {
        return this.userManager.getName();
    }

    login() {
        if (popup)
            return this.userManager.signinPopup();
        else
            return this.userManager.signinRedirect();
    }

    logout() {
        if (popup)
            return this.userManager.signoutPopup();
        else
            return this.userManager.signoutRedirect();
    }

    addUserUpdateListener(cb) {
        this.userManager.events.addUserLoaded(cb);
        this.userManager.events.addUserUnloaded(cb);
        cb.call();
    }
}

export const auth = new AuthService()
export const access_token = ref(null)

auth.addUserUpdateListener(() => {
    auth.getUser().then((user) => {
        if (user !== null) {
            access_token.value = user.access_token
        } else {
            access_token.value = null
        }
    });
})

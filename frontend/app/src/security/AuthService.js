import { UserManager } from "oidc-client-ts";
import { settings, popup } from "./settings";

export default class AuthService {
    userManager;

    constructor() {
        this.userManager = new UserManager(settings);
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
        if (!popup)
            cb.call();
    }
}
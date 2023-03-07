import { UserManager } from "oidc-client-ts";
import { auth_settings } from "./settings";

new UserManager(auth_settings).signinSilentCallback().then(function() {
    log("signin silent callback response success");
}).catch(function(err) {
    console.error(err);
    log(err);
});
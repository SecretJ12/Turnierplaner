import { UserManager } from "oidc-client-ts";
import { settings } from "./settings";

new UserManager(settings).signinSilentCallback().then(function() {
    log("signin silent callback response success");
}).catch(function(err) {
    console.error(err);
    log(err);
});
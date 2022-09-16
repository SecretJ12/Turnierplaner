import { UserManager } from "oidc-client-ts";
import { settings, popup } from "./settings";

const mgr = new UserManager(settings);

if (window.location.href.includes('code')) {
    if (popup)
        mgr.signinPopupCallback(undefined, false).then(() => {
        }).catch((err) => {
            console.log(err);
        })
    else
        mgr.signinCallback().then(function () {
            window.location.href = '../';
        }).catch(function (err) {
            console.log(err);
        });
}

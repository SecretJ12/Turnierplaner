import { UserManager } from "oidc-client-ts";
import { settings, popup } from "./settings";

const mgr = new UserManager(settings);

if (popup)
    mgr.signoutPopupCallback(undefined, false).then(() => {
        console.log("sign-out")
    }).catch((err) => {
        console.log(err);
    })
else
    mgr.signoutCallback().then(function () {
        window.location.href = '../';
    }).catch(function (err) {
        console.log(err);
    });

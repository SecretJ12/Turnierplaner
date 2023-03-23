import {UserManager} from "oidc-client-ts";
import {auth_settings, popup} from "./settings";

const mgr = new UserManager(auth_settings);

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

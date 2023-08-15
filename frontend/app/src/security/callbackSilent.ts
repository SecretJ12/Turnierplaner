import { UserManager } from "oidc-client-ts"
import { auth_settings } from "./settings"

new UserManager(auth_settings)
	.signinSilentCallback()
	.then(function () {
		console.log("signin silent callback response success")
	})
	.catch(function (err) {
		console.error(err)
		console.log(err)
	})

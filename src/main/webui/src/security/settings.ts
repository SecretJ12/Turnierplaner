import { settings } from "@/settings"

function buildSettings() {
	return {
		authority: settings.AUTH_DOMAIN,
		client_id: "frontend-vue",
		response_type: "code",
		scope: "openid profile",

		redirect_uri: settings.FRONTEND + "/callbackSignIn.html",
		post_logout_redirect_uri: settings.FRONTEND + "/callbackSignOut.html",

		popup_redirect_uri: settings.FRONTEND + "/callbackSignIn.html",
		popup_post_logout_redirect_uri: settings.FRONTEND + "/callbackSignOut.html",
		registration_endpoint:
			settings.AUTH_DOMAIN + "/clients-registrations/openid-connect",

		automaticSilentRenew: true,
		filterProtocolClaims: true,
	}
}

export const auth_settings = buildSettings()

export function rebuildAuthSettings() {
	Object.assign(auth_settings, buildSettings())
}

rebuildAuthSettings()

export const popup = true

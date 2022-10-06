const AUTH_DOMAIN = "http://localhost:7777/realms/Quarkus";
const FRONTEND = "http://localhost:3000"

export const settings = {
    authority: AUTH_DOMAIN,
    client_id: "frontend-vue",
    response_type: "code",
    scope: "openid profile",

    redirect_uri: FRONTEND + "/callbackSignIn.html",
    post_logout_redirect_uri: FRONTEND + "/callbackSignOut.html",

    popup_redirect_uri: FRONTEND + "/callbackSignIn.html",
    popup_post_logout_redirect_uri: FRONTEND + "/callbackSignOut.html",
    registration_endpoint: AUTH_DOMAIN + "/clients-registrations/openid-connect",

    automaticSilentRenew: true,
    filterProtocolClaims: true
};

export const popup = true;

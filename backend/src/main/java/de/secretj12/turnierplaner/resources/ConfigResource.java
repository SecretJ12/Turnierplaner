package de.secretj12.turnierplaner.resources;

import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserConfig;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/config")
public class ConfigResource {

    @ConfigProperty(name = "club.name")
    String name;
    @ConfigProperty(name = "language.default")
    String language;

    @GET
    @Path("/default")
    public jUserConfig getDefaultConfig() {
        var config = new jUserConfig();
        config.setName(name);
        config.setLanguage(language);
        return config;
    }
}

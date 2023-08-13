package de.secretj12.turnierplaner.startup;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class Startup {
    @Inject
    TestdataGenerator testdataGenerator;

    @Inject
    @ConfigProperty(name = "turnierplaner.testdata", defaultValue = "false")
    Boolean addTestdata;

    void onStart(@Observes StartupEvent ev) {
        if (addTestdata) {
            testdataGenerator.generateData();
        }
    }
}

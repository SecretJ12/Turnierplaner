package de.secretj12.turnierplaner.startup;

import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class Startup {
    @Inject
    Testdata testdata;

    @ConfigProperty(name = "turnierplaner.testdata", defaultValue = "false")
    Boolean addTestdata;

    void onStart(@Observes StartupEvent ev) {
        if (addTestdata)
            testdata.createData();
    }
}

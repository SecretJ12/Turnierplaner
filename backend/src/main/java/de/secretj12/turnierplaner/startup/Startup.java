package de.secretj12.turnierplaner.startup;

import de.secretj12.turnierplaner.startup.testdata.TestdataGenerator;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class Startup {
  @Inject Defaults defaults;
  @Inject TestdataGenerator testdataGenerator;

  @ConfigProperty(name = "turnierplaner.testdata", defaultValue = "false")
  Boolean addTestdata;

  void onStart(@Observes StartupEvent ev) {
    defaults.addDefault();
    if (addTestdata) {
      testdataGenerator.generateData();
    }
  }
}

package de.secretj12.turnierplaner.startup;

import de.secretj12.turnierplaner.startup.testdata.TestdataGenerator;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class TestStartup {

    @Inject
    TestdataGenerator testdataGenerator;

    @Test
    public void generateTestdata() {
        testdataGenerator.generateData();
    }
}

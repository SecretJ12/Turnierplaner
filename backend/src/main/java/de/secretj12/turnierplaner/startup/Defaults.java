package de.secretj12.turnierplaner.startup;

import de.secretj12.turnierplaner.db.entities.DefaultConfig;
import de.secretj12.turnierplaner.db.repositories.DefaultConfigRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class Defaults {
    @ConfigProperty(name = "turnierplaner.club-name")
    String clubName;
    @ConfigProperty(name = "turnierplaner.language")
    String language;
    @Inject
    DefaultConfigRepository defaultConfigRepository;

    @Transactional
    void addDefault() {
        if (defaultConfigRepository.findById(0L) != null)
            return;

        var defaultConfig = new DefaultConfig();
        defaultConfig.setId(0);
        defaultConfig.setTitle(clubName);
        if (language != null && !language.isEmpty() && !language.equals("title"))
            defaultConfig.setLanguage(language);
        defaultConfigRepository.persist(defaultConfig);
    }
}

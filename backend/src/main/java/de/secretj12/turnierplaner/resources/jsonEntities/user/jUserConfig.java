package de.secretj12.turnierplaner.resources.jsonEntities.user;

import de.secretj12.turnierplaner.db.entities.Config;
import de.secretj12.turnierplaner.db.entities.DefaultConfig;

public class jUserConfig {
    private String title;
    private String language;

    public jUserConfig() {
    }

    public jUserConfig(DefaultConfig config) {
        this(config, null);
    }

    public jUserConfig(DefaultConfig defConfig, Config config) {
        this.title = defConfig.getTitle();
        if (config == null)
            this.language = defConfig.getLanguage();
        else
            this.language = config.getLanguage();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}

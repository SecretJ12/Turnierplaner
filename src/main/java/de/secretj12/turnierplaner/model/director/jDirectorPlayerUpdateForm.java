package de.secretj12.turnierplaner.model.director;

import de.secretj12.turnierplaner.db.entities.Player;
import de.secretj12.turnierplaner.model.user.jUserPlayerRegistrationForm;

import java.util.UUID;

public class jDirectorPlayerUpdateForm extends jUserPlayerRegistrationForm {

    private UUID id;

    public jDirectorPlayerUpdateForm() {
    }

    public jDirectorPlayerUpdateForm(Player player) {
        super(player);
        this.id = player.getId();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}

package de.secretj12.turnierplaner.db.repositories;

import de.secretj12.turnierplaner.db.entities.VerificationCode;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class VerificationCodeRepository implements PanacheRepository<VerificationCode> {

    public VerificationCode findByUUID(UUID uuid) {
        return find("FROM VerificationCode v WHERE v.id = ?1", uuid).firstResultOptional().orElse(null);
    }
}

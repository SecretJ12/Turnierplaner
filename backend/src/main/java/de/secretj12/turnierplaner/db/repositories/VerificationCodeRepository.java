package de.secretj12.turnierplaner.db.repositories;

import de.secretj12.turnierplaner.db.entities.VerificationCode;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VerificationCodeRepository implements PanacheRepository<VerificationCode> {

}
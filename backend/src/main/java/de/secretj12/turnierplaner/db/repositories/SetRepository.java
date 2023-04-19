package de.secretj12.turnierplaner.db.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import de.secretj12.turnierplaner.db.entities.Set;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SetRepository implements PanacheRepository<Set> {

}

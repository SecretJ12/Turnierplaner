package de.secretj12.turnierplaner.db.repositories;

import de.secretj12.turnierplaner.db.entities.groups.MatchOfGroup;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MatchOfGroupRepository implements PanacheRepository<MatchOfGroup> {
}

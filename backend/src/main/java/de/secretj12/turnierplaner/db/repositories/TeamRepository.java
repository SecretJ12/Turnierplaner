package de.secretj12.turnierplaner.db.repositories;

import de.secretj12.turnierplaner.db.entities.competition.Team;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TeamRepository implements PanacheRepository<Team> {
}

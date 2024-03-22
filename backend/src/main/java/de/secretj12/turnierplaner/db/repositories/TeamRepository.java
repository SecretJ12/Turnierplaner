package de.secretj12.turnierplaner.db.repositories;

import de.secretj12.turnierplaner.db.entities.competition.Team;
import de.secretj12.turnierplaner.db.entities.groups.Group;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class TeamRepository implements PanacheRepository<Team> {
    public Team getById(UUID uuid) {
        return find("id", uuid).firstResultOptional().orElse(null);
    }

    public List<Team> teamsOfGroup(Group group) {
        return find("#findTeams", Parameters.with("group", group)).list();
    }
}

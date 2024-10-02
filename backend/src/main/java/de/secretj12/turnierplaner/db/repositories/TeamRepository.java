package de.secretj12.turnierplaner.db.repositories;

import de.secretj12.turnierplaner.db.entities.competition.Team;
import de.secretj12.turnierplaner.db.entities.groups.Group;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class TeamRepository implements PanacheRepository<Team> {
  public Team findById(UUID uuid) {
    return find("id", uuid).firstResultOptional().orElse(null);
  }

  public Set<Team> teamsOfGroup(Group group) {
    return find("#findTeams", Parameters.with("group", group)).stream()
        .collect(Collectors.toCollection(HashSet::new));
  }
}

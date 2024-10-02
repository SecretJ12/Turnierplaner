package de.secretj12.turnierplaner.db.repositories;

import de.secretj12.turnierplaner.db.entities.competition.Competition;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.QueryParam;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CompetitionRepository implements PanacheRepository<Competition> {

  public Competition findById(UUID uuid) {
    return find("id", uuid).firstResultOptional().orElse(null);
  }

  public Competition getByName(String tourName, String compName) {
    return find("#compByName", tourName, compName).firstResultOptional().orElse(null);
  }

  public List<Competition> listByName(@QueryParam("tourName") String tourName) {
    return find("#listByName", tourName).list();
  }
}

package de.secretj12.turnierplaner.db.repositories;

import de.secretj12.turnierplaner.db.entities.Court;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CourtRepositiory implements PanacheRepository<Court> {

  public Court findByName(String name) {
    return find("name", name).firstResult();
  }
}

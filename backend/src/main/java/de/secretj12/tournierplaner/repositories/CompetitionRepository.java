package de.secretj12.tournierplaner.repositories;

import de.secretj12.tournierplaner.entities.Competition;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CompetitionRepository implements PanacheRepository<Competition>  {

    public Competition getByName(String tourName, String compName) {
        return find("tournament = ?1 and name = ?2", tourName, compName).firstResultOptional().orElse(null);
    }
}

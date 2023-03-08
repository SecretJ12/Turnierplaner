package de.secretj12.tournierplaner.repositories;

import de.secretj12.tournierplaner.entities.Competition;
import de.secretj12.tournierplaner.entities.Tournament;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.jboss.resteasy.annotations.Query;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CompetitionRepository implements PanacheRepository<Competition>  {

    public Competition getByName(String tourName, String compName) {
        return find("#compByName",
                tourName, compName)
                .firstResultOptional().orElse(null);
    }

    public List<Competition> listByName(@QueryParam("tourName") String tourName) {
        return find("#listByName", tourName)
                .list();
    }
}

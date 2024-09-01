package de.secretj12.turnierplaner.tools;

import de.secretj12.turnierplaner.db.entities.Tournament;
import de.secretj12.turnierplaner.db.repositories.TournamentRepository;
import io.quarkus.security.UnauthorizedException;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class CommonHelpers {
    @Inject
    TournamentRepository tournaments;
    
    @Inject
    SecurityIdentity securityIdentity;

    public void checkTournamentAccessibility(String tourName) {
        Tournament tournament = tournaments.getByName(tourName);
        if (tournament == null) throw new NotFoundException("Tournament could not be found");
        if (!securityIdentity.hasRole("director") && !tournament.isVisible())
            throw new UnauthorizedException("Cannot access tournament");
    }
}

package de.secretj12.turnierplaner.resources;


import de.secretj12.turnierplaner.db.entities.Match;
import de.secretj12.turnierplaner.db.entities.Tournament;
import de.secretj12.turnierplaner.db.entities.competition.Competition;
import de.secretj12.turnierplaner.db.repositories.CompetitionRepository;
import de.secretj12.turnierplaner.db.repositories.CourtRepositiory;
import de.secretj12.turnierplaner.db.repositories.MatchRepository;
import de.secretj12.turnierplaner.db.repositories.TournamentRepository;
import de.secretj12.turnierplaner.resources.jsonEntities.director.jDirectorScheduleMatch;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@RolesAllowed("director")
@Path("/tournament/{tourName}/competition/{compName}/match")
public class MatchRessource {

    @Inject
    TournamentRepository tournaments;
    @Inject
    CompetitionRepository competitions;
    @Inject
    MatchRepository matchRepository;
    @Inject
    CourtRepositiory courtRepositiory;
    @Inject
    SecurityIdentity securityIdentity;

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public String updateMatches(@PathParam("tourName") String tourName, @PathParam("compName") String compName,
                                List<jDirectorScheduleMatch> matches) {
        checkTournamentAccessibility(tourName);
        Competition competition = competitions.getByName(tourName, compName);
        if (competition == null) throw new NotFoundException("Competition could not be found");

        for (var cMatch : matches) {
            Match match = matchRepository.findById(cMatch.getId());
            if (match == null)
                throw new NotFoundException("Could not find match");
            if (match.getCompetition().getId() != competition.getId())
                throw new BadRequestException("Match does not belong to specified competition");
            match.setCourt(courtRepositiory.findByName(cMatch.getCourt()));
            match.setBegin(cMatch.getBegin());
            match.setEnd(cMatch.getEnd());

            matchRepository.persist(match);
        }
        return "Updated matches";
    }

    private void checkTournamentAccessibility(String tourName) {
        Tournament tournament = tournaments.getByName(tourName);
        if (tournament == null) throw new NotFoundException("Tournament could not be found");
    }
}

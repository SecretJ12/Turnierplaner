package de.secretj12.tournierplaner.resources;

import de.secretj12.tournierplaner.entities.Competition;
import de.secretj12.tournierplaner.repositories.CompetitionRepository;
import de.secretj12.tournierplaner.repositories.TournamentRepository;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/tournaments/{tourName}/competitions")
public class CompetitionResource {

    @Inject
    CompetitionRepository competitions;

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Competition> getAllCompetitions(@PathParam("tourName") String tourName) {
        return competitions.list("tournament", tourName);
    }

    @GET
    @Path("/{compName}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Competition getTournament(@PathParam("tourName") String tourNamme, @PathParam("compName") String compName) {
        return competitions.getByName(tourNamme, compName);
    }
}

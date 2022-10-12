package de.secretj12.tournierplaner.resources;

import de.secretj12.tournierplaner.entities.Competition;
import de.secretj12.tournierplaner.repositories.CompetitionRepository;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/competition")
public class CompetitionResource {

    @Inject
    CompetitionRepository competitions;

    @GET
    @Path("/list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Competition> getAllCompetitions(@QueryParam("tourName") String tourName) {
        return competitions.list("tournament", tourName);
    }

    @GET
    @Path("/details")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Competition getTournament(@QueryParam("tourName") String tourNamme, @QueryParam("compName") String compName) {
        return competitions.getByName(tourNamme, compName);
    }
}

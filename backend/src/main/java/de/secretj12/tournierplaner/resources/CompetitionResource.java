package de.secretj12.tournierplaner.resources;

import de.secretj12.tournierplaner.entities.Competition;
import de.secretj12.tournierplaner.repositories.CompetitionRepository;
import io.quarkus.security.identity.SecurityIdentity;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/competition")
public class CompetitionResource {

    @Inject
    CompetitionRepository competitions;
    @Inject
    SecurityIdentity securityIdentity;

    @GET
    @Path("/list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Competition> getAllCompetitions(@QueryParam("tourName") String tourName) {
        return competitions.list("tournament", tourName);
    }

    @GET
    @Path("/canEdit")
    @Produces(MediaType.TEXT_PLAIN)
    public Response canCreate() {
        // TODO muss auch can_edit erfüllen
        for (String key : securityIdentity.getAttributes().keySet())
            System.out.println(key + ": " + securityIdentity.getAttribute(key));
        if (securityIdentity.hasRole("director"))
            return Response.ok("Authorized").build();
        else
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized").build();
    }

    @GET
    @Path("/details")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Competition getTournament(@QueryParam("tourName") String tourNamme, @QueryParam("compName") String compName) {
        return competitions.getByName(tourNamme, compName);
    }
}

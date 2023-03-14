package de.secretj12.tournierplaner.resources;

import de.secretj12.tournierplaner.entities.Player;
import de.secretj12.tournierplaner.repositories.PlayerRepository;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/player")
public class PlayerResource {
    @Inject
    PlayerRepository playerRepository;

    // TODO rework, use for playersearch
    // TODO add search parameters like name and sex
    @GET
    @Path("/players")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Player> getAllCompetitions() {
        return playerRepository.listAll();
    }

    @POST
    @Transactional
    @Path("/registration")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response playerRegistration(Player player) {
        // TODO check mail
        // TODO check phone number

        player.setMailVerified(false);
        player.setAdminVerified(false);
        playerRepository.persist(player);

        // TODO mail verification and verification by admin?

        return Response.ok("successfully added").build();
    }
}
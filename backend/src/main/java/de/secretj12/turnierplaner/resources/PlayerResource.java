package de.secretj12.turnierplaner.resources;

import de.secretj12.turnierplaner.entities.Player;
import de.secretj12.turnierplaner.repositories.PlayerRepository;
import de.secretj12.turnierplaner.resources.FormEntities.ReducedPlayer;

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

    // TODO add search parameters like name and sex
    // TODO only return verified accounts (except for admins)
    // TODO add endpoint for players to competition
    @GET
    @Transactional
    @Path("/players")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ReducedPlayer> listPlayer(@QueryParam("search") String search) {
        if (search.length() == 0)
            return List.of();
        return playerRepository
                .filter(search).map(ReducedPlayer::new)
                .toList();
    }

    @POST
    @Transactional
    @Path("/registration")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response playerRegistration(Player player) {
        if (playerRepository.getByName(player.getFirstName(), player.getLastName()) != null)
            return Response.status(Response.Status.CONFLICT.getStatusCode(),
                    "A player already exists with this name").build();
        // TODO check mail
        // TODO check phone number

        player.setMailVerified(false);
        player.setAdminVerified(false);
        playerRepository.persist(player);

        // TODO mail verification and verification by admin?

        return Response.ok("successfully added").build();
    }
}
package de.secretj12.turnierplaner.resources;

import de.secretj12.turnierplaner.db.entities.Player;
import de.secretj12.turnierplaner.db.entities.SexType;
import de.secretj12.turnierplaner.db.repositories.PlayerRepository;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserPlayerRegistrationForm;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserPlayer;

import io.quarkus.mailer.Mailer;
import io.quarkus.mailer.Mail;
import io.smallrye.common.annotation.Blocking;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Objects;

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
    public List<jUserPlayer> listPlayer(@QueryParam("search") String search) {
        if (search.length() == 0)
            return List.of();
        return playerRepository
                .filter(search).map(jUserPlayer::new)
                .toList();
    }

    @Inject
    Mailer mailer;

    @POST
    @Transactional
    @Path("/registration")
    @Blocking
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response playerRegistration(jUserPlayerRegistrationForm playerForm) {
        if (playerRepository.getByName(playerForm.getFirstName(), playerForm.getLastName()) != null)
            return Response.status(Response.Status.CONFLICT.getStatusCode(),
                    "A player already exists with this name").build();
        // TODO check mail
        // TODO check phone number
        Player newPlayer = new Player();
        if (Objects.equals(playerForm.getSex(), "woman")){
            newPlayer.setSex(SexType.male);
        }else if (Objects.equals(playerForm.getSex(),"men")){
            newPlayer.setSex(SexType.male);
        }else{
            return Response.status(Response.Status.CONFLICT.getStatusCode(),
                    "Gender not recognized").build();
        }
        newPlayer.setFirstName(playerForm.getFirstName());
        newPlayer.setLastName(playerForm.getLastName());
        newPlayer.setBirthday(playerForm.getBirthdate());
        newPlayer.setEmail(playerForm.getEmail());
        newPlayer.setPhone(playerForm.getPhone());
        newPlayer.setMailVerified(false);
        newPlayer.setAdminVerified(false);
        try{
            mailer.send(Mail.withText(newPlayer.getEmail(),"Verification Mail","Please click this link to verify yourself:"));
        }catch (Exception e){
            return Response.status(Response.Status.CONFLICT.getStatusCode(),
                    "Problem sending you the verification mail. Please try again later.").build();
        }

        playerRepository.persist(newPlayer);

        // TODO mail verification and verification by admin?

        return Response.ok("successfully added").build();
    }



}
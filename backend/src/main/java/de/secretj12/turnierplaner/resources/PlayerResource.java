package de.secretj12.turnierplaner.resources;

import de.secretj12.turnierplaner.db.entities.Player;
import de.secretj12.turnierplaner.db.entities.SexType;
import de.secretj12.turnierplaner.db.entities.VerificationCode;
import de.secretj12.turnierplaner.db.repositories.PlayerRepository;
import de.secretj12.turnierplaner.db.repositories.VerificationCodeRepository;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserPlayerRegistrationForm;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserPlayer;

import io.quarkus.mailer.Mailer;
import io.quarkus.panache.common.Parameters;
import io.smallrye.common.annotation.Blocking;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Path("/player")
public class PlayerResource {
    @Inject
    PlayerRepository playerRepository;
    @Inject
    VerificationCodeRepository verificationCodeRepository;

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

    MailTemplates mailTemplates = new MailTemplates();

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
        newPlayer.setBirthday(playerForm.getBirthday());
        newPlayer.setEmail(playerForm.getEmail());
        newPlayer.setPhone(playerForm.getPhone());
        newPlayer.setMailVerified(false);
        newPlayer.setAdminVerified(false);
        playerRepository.persist(newPlayer);

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setPlayer(newPlayer);
        verificationCode.setExpiration_date(LocalDateTime.now().plusMinutes(10));
        verificationCodeRepository.persist(verificationCode);

        try{
            mailer.send(mailTemplates.verificationMail(newPlayer.getEmail(),verificationCode.getId().toString() ));
        }catch (Exception e){
            e.printStackTrace();
            return Response.status(Response.Status.CONFLICT.getStatusCode(),
                    "Problem sending you the verification mail. Please try again later.").build();
        }



        // TODO mail verification and verification by admin?

        return Response.ok("successfully added").build();
    }


    @GET
    @Transactional
    @Path("/verification")
    @Produces(MediaType.TEXT_PLAIN)
    public Response verification(@QueryParam("code") String code) {
        System.out.println(code);
        VerificationCode verificationCode = verificationCodeRepository.find("FROM VerificationCode v WHERE v.id = :code",
                Parameters.with("code", UUID.fromString(code)).map()).firstResultOptional().orElse(null);
        if (verificationCode == null){
            System.out.println("ERROR no matching verification code");
            return Response.status(Response.Status.CONFLICT.getStatusCode(),"This verification code is not correct!").build();
        }
        Player player = verificationCode.getPlayer();
        player.setMailVerified(true);
        playerRepository.persist(player);
        verificationCodeRepository.delete(verificationCode);
        System.out.println("SUCCESS");
        return Response.status(Response.Status.ACCEPTED.getStatusCode(),"Successfully verified!").build();
    }

}
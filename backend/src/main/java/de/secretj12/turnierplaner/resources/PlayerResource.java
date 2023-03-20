package de.secretj12.turnierplaner.resources;

import de.secretj12.turnierplaner.db.entities.Player;
import de.secretj12.turnierplaner.db.entities.SexType;
import de.secretj12.turnierplaner.db.entities.VerificationCode;
import de.secretj12.turnierplaner.db.repositories.PlayerRepository;
import de.secretj12.turnierplaner.db.repositories.VerificationCodeRepository;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserPlayer;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserPlayerRegistrationForm;
import io.quarkus.mailer.Mailer;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.Blocking;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;
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
        // TODO check phone number (only valid phone number)
        Player newPlayer = new Player();
        newPlayer.setFirstName(playerForm.getFirstName());
        newPlayer.setLastName(playerForm.getLastName());
        newPlayer.setBirthday(playerForm.getBirthday());
        switch (playerForm.getSex()) {
            case male -> newPlayer.setSex(SexType.male);
            case female -> newPlayer.setSex(SexType.female);
            default -> {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
        newPlayer.setEmail(playerForm.getEmail());
        newPlayer.setPhone(playerForm.getPhone());
        newPlayer.setMailVerified(false);
        // TODO Admin verification
        newPlayer.setAdminVerified(false);
        playerRepository.persist(newPlayer);

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setPlayer(newPlayer);
        verificationCode.setExpiration_date(LocalDateTime.now().plusMinutes(30));
        verificationCodeRepository.persist(verificationCode);

        // TODO check for valid mail
        try {
            mailer.send(mailTemplates.verificationMail(newPlayer.getEmail(), verificationCode.getId().toString()));
        } catch (Exception e) {
            // TODO print by logger
            e.printStackTrace();
            return Response.status(Response.Status.CONFLICT.getStatusCode(),
                    "Problem sending you the verification mail. Please try again later.").build();
        }

        return Response.ok("successfully added").build();
    }


    @GET
    @Transactional
    @Path("/verification")
    @Produces(MediaType.TEXT_PLAIN)
    public Response verification(@QueryParam("code") String code) {
        VerificationCode verificationCode = verificationCodeRepository.findByUUID(UUID.fromString(code));
        if (verificationCode == null) {
            return Response.status(Response.Status.CONFLICT.getStatusCode(),
                    "This verification code is not correct!").build();
        }

        Player player = verificationCode.getPlayer();
        player.setMailVerified(true);
        playerRepository.persist(player);
        verificationCodeRepository.delete(verificationCode);
        return Response.status(Response.Status.ACCEPTED.getStatusCode(), "Successfully verified!").build();
    }

    @Transactional
    @Scheduled(every = "30m", identity = "clear-verification-code")
    void clear() {
        verificationCodeRepository.delete("FROM VerificationCode v WHERE v.expiration_date < NOW()");
    }

}
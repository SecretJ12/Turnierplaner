package de.secretj12.turnierplaner.resources;

import com.fasterxml.jackson.annotation.JsonFormat;
import de.secretj12.turnierplaner.db.entities.Player;
import de.secretj12.turnierplaner.db.entities.SexType;
import de.secretj12.turnierplaner.db.entities.VerificationCode;
import de.secretj12.turnierplaner.db.repositories.PlayerRepository;
import de.secretj12.turnierplaner.db.repositories.VerificationCodeRepository;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserPlayer;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserPlayerRegistrationForm;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserSex;
import io.quarkus.mailer.Mailer;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Path("/player")
public class PlayerResource {
    @Inject
    PlayerRepository playerRepository;
    @Inject
    VerificationCodeRepository verificationCodeRepository;
    @Inject
    Mailer mailer;
    MailTemplates mailTemplates = new MailTemplates();


    @GET
    @Transactional
    @Path("/find")
    @Produces(MediaType.APPLICATION_JSON)
    public List<jUserPlayer> listPlayer(@QueryParam("search") String search, @QueryParam("sex") jUserSex sex,
                                        @QueryParam("minAge") @JsonFormat(pattern = "yyyy-MM-dd") String minAgeS,
                                        @QueryParam("maxAge") @JsonFormat(pattern = "yyyy-MM-dd") String maxAgeS) {
        // TODO instead of filtering by params of client, better filter by given competition, prevents leak of birthday
        // TODO only return verified accounts (except for admins)
        LocalDate minAge = minAgeS != null ? LocalDate.parse(minAgeS) : null;
        LocalDate maxAge = maxAgeS != null ? LocalDate.parse(maxAgeS) : null;

        SexType dbSex = sex == null ? null : switch (sex) {
            case MALE -> SexType.MALE;
            case FEMALE -> SexType.FEMALE;
        };
        if (search.isEmpty()) {
            return List.of();
        }
        return playerRepository.filter(search, dbSex, minAge, maxAge).map(jUserPlayer::new).toList();
    }

    @POST
    @Transactional
    @Path("/registration")
    @Blocking
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String playerRegistration(jUserPlayerRegistrationForm playerForm) {
        if (playerRepository.getByName(playerForm.getFirstName(), playerForm.getLastName()) != null)
            throw new WebApplicationException("A player already exists with this name", Response.Status.CONFLICT);

        // TODO check phone number (only valid phone number)
        // TODO check fields are not empty!
        Player newPlayer = new Player();
        newPlayer.setFirstName(playerForm.getFirstName());
        newPlayer.setLastName(playerForm.getLastName());
        newPlayer.setBirthday(playerForm.getBirthday());
        if (playerForm.getSex() == null) throw new BadRequestException("Sex is null");
        switch (playerForm.getSex()) {
            case MALE -> newPlayer.setSex(SexType.MALE);
            case FEMALE -> newPlayer.setSex(SexType.FEMALE);
        }
        newPlayer.setEmail(playerForm.getEmail());
        newPlayer.setPhone(playerForm.getPhone());
        newPlayer.setMailVerified(false);
        // TODO Admin verification
        newPlayer.setAdminVerified(false);
        playerRepository.persist(newPlayer);

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setPlayer(newPlayer);
        verificationCode.setExpirationDate(LocalDateTime.now().plusMinutes(30));
        verificationCodeRepository.persist(verificationCode);

        // TODO check for valid mail
        try {
            mailer.send(mailTemplates.verificationMail(newPlayer.getEmail(), verificationCode.getId().toString()));
        } catch (Exception e) {
            // TODO print by logger
            throw new BadRequestException("Problem sending you the verification mail. Please try again later.");
        }

        return "successfully added";
    }


    @POST
    @Transactional
    @Path("/admin/registration")
    @Blocking
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public jUserPlayer adminPlayerRegistration(jUserPlayerRegistrationForm playerForm) {
        if (playerRepository.getByName(playerForm.getFirstName(), playerForm.getLastName()) != null)
            throw new WebApplicationException("A player already exists with this name", Response.Status.CONFLICT);

        // TODO check phone number (only valid phone number)
        // TODO check fields are not empty!
        Player newPlayer = new Player();
        newPlayer.setFirstName(playerForm.getFirstName());
        newPlayer.setLastName(playerForm.getLastName());
        newPlayer.setBirthday(playerForm.getBirthday());
        if (playerForm.getSex() == null) throw new BadRequestException("Sex is null");
        switch (playerForm.getSex()) {
            case MALE -> newPlayer.setSex(SexType.MALE);
            case FEMALE -> newPlayer.setSex(SexType.FEMALE);
        }
        newPlayer.setEmail(playerForm.getEmail());
        newPlayer.setPhone(playerForm.getPhone());
        newPlayer.setMailVerified(true);
        // TODO Admin verification
        newPlayer.setAdminVerified(true);
        playerRepository.persist(newPlayer);

        return new jUserPlayer(newPlayer);
    }

    @GET
    @Transactional
    @Path("/verification")
    @Produces(MediaType.TEXT_PLAIN)
    public String verification(@QueryParam("code") String code) {
        VerificationCode verificationCode = verificationCodeRepository.findByUUID(UUID.fromString(code));
        if (verificationCode == null) throw new BadRequestException("This verification code is not correct!");

        Player player = verificationCode.getPlayer();
        player.setMailVerified(true);
        playerRepository.persist(player);
        verificationCodeRepository.delete(verificationCode);
        return "Successfully verified!";
    }

    @Transactional
    @Scheduled(every = "30m", identity = "clear-verification-code")
    void clear() {
        // TODO also delete player? otherwise not verified player, but no new one can be created because of duplicate
        verificationCodeRepository.delete("FROM VerificationCode v WHERE v.expiration_date < NOW()");
    }

}
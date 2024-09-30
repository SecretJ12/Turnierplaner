package de.secretj12.turnierplaner.resources;

import de.secretj12.turnierplaner.db.entities.DefaultConfig;
import de.secretj12.turnierplaner.db.entities.Player;
import de.secretj12.turnierplaner.db.entities.Sex;
import de.secretj12.turnierplaner.db.entities.VerificationCode;
import de.secretj12.turnierplaner.db.entities.competition.Competition;
import de.secretj12.turnierplaner.db.repositories.CompetitionRepository;
import de.secretj12.turnierplaner.db.repositories.DefaultConfigRepository;
import de.secretj12.turnierplaner.db.repositories.PlayerRepository;
import de.secretj12.turnierplaner.db.repositories.VerificationCodeRepository;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserPlayer;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserPlayerRegistrationForm;
import io.quarkus.mailer.Mailer;
import io.quarkus.scheduler.Scheduled;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.common.annotation.Blocking;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Path("/player")
public class PlayerResource {
    @Inject
    CompetitionRepository competitionRepository;
    @Inject
    PlayerRepository playerRepository;
    @Inject
    VerificationCodeRepository verificationCodeRepository;
    @Inject
    Mailer mailer;
    MailTemplates mailTemplates = new MailTemplates();

    @Inject
    DefaultConfigRepository defaultConfigRepository;
    @Inject
    SecurityIdentity securityIdentity;

    @ConfigProperty(name = "turnierplaner.registration.expire")
    public int expire;

    @GET
    @Path("/compFind/{tourId}/{compId}/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<jUserPlayer> listCompPlayer(@PathParam("tourId") String tourId, @PathParam("compId") String compId,
                                            @QueryParam("search") String search,
                                            @DefaultValue("false") @QueryParam("playerB") boolean playerB) {
        Competition competition = competitionRepository.getByName(tourId, compId);
        if (competition == null)
            throw new BadRequestException("Invalid competition");

        LocalDate minAge = playerB ? (competition.playerBhasMinAge() ? competition
            .getPlayerBminAge() : null) : (competition.playerAhasMinAge() ? competition.getPlayerAminAge() : null);
        LocalDate maxAge = playerB ? (competition.playerBhasMaxAge() ? competition
            .getPlayerBmaxAge() : null) : (competition.playerAhasMaxAge() ? competition.getPlayerAmaxAge() : null);

        Sex dbSex = switch (playerB ? competition.getPlayerBSex() : competition.getPlayerASex()) {
            case MALE -> Sex.MALE;
            case FEMALE -> Sex.FEMALE;
            case ANY -> null;
        };
        if (search.isEmpty()) {
            return List.of();
        }

        DefaultConfig defConfig = defaultConfigRepository.findById(0L);
        boolean admin = securityIdentity.hasRole("director") || !defConfig.isAdminVerificationNeeded();
        return playerRepository.filter(search, dbSex, minAge, maxAge, admin, 0, 10).map(jUserPlayer::new).toList();
    }

    @GET
    @Path("/find")
    @RolesAllowed("director")
    @Produces(MediaType.APPLICATION_JSON)
    public List<jUserPlayer> listPlayer(@QueryParam("search") String search,
                                        @DefaultValue("0") @QueryParam("page") int page,
                                        @DefaultValue("5") @QueryParam("pageSize") int pageSize) {
        return playerRepository.filter(search, null, null, null, true, page, pageSize).map(jUserPlayer::new).toList();
    }

    @GET
    @Path("/listUnverified")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("director")
    public List<jUserPlayer> listUnverified() {
        return playerRepository.adminUnverified().map(jUserPlayer::new).toList();
    }

    @GET
    @Path("/{playerId}/details")
    @Produces(MediaType.APPLICATION_JSON)
    public jUserPlayer getPlayer(@PathParam("playerId") UUID playerId) {
        Player player = playerRepository.getById(playerId);
        if (player == null)
            throw new NotFoundException("Player not found");
        return new jUserPlayer(player);
    }

    @POST
    @Transactional
    @Path("/registration")
    @Blocking
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String playerRegistration(jUserPlayerRegistrationForm playerForm) {
        Player exPlayer = playerRepository.getByName(playerForm.getFirstName(), playerForm.getLastName());
        if (exPlayer != null &&
            (exPlayer.getVerificationCode() == null
                || (exPlayer.getVerificationCode() != null && exPlayer.getVerificationCode().getExpirationDate()
                    .isBefore(Instant.now()))))
            throw new WebApplicationException("A player already exists with this name", Response.Status.CONFLICT);

        // TODO check phone number (only valid phone number)
        // TODO check fields are not empty!
        Player newPlayer = new Player();
        newPlayer.setFirstName(playerForm.getFirstName());
        newPlayer.setLastName(playerForm.getLastName());
        newPlayer.setBirthday(playerForm.getBirthday());
        if (playerForm.getSex() == null) throw new BadRequestException("Sex is null");
        switch (playerForm.getSex()) {
            case MALE -> newPlayer.setSex(Sex.MALE);
            case FEMALE -> newPlayer.setSex(Sex.FEMALE);
        }
        newPlayer.setEmail(playerForm.getEmail());
        newPlayer.setPhone(playerForm.getPhone());
        newPlayer.setMailVerified(securityIdentity.hasRole("director"));
        newPlayer.setAdminVerified(securityIdentity.hasRole("director"));
        playerRepository.persist(newPlayer);

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setPlayer(newPlayer);
        verificationCode.setExpirationDate(Instant.now().plus(expire, ChronoUnit.MINUTES));
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

    @GET
    @Transactional
    @Path("/verification")
    @Produces(MediaType.TEXT_PLAIN)
    public String verification(@QueryParam("code") String code) {
        VerificationCode verificationCode = verificationCodeRepository.findByUUID(UUID.fromString(code));
        if (verificationCode == null) throw new BadRequestException("This verification code is not correct!");

        Player player = verificationCode.getPlayer();
        player.setMailVerified(true);
        player.setVerificationCode(null);
        playerRepository.persist(player);
        verificationCodeRepository.delete(verificationCode);
        return "Successfully verified!";
    }

    @POST
    @RolesAllowed("director")
    @Transactional
    @Path("/adminVerify")
    @Produces(MediaType.TEXT_PLAIN)
    public String adminVerify(Player player) {
        Player dbPlayer = playerRepository.getById(player.getId());
        dbPlayer.setAdminVerified(true);
        playerRepository.persist(dbPlayer);
        return "Successfully verified!";
    }

    @POST
    @RolesAllowed("director")
    @Transactional
    @Path("/delete")
    @Produces(MediaType.TEXT_PLAIN)
    public String deletePlayer(Player player) {
        Player dbPlayer = playerRepository.getById(player.getId());
        playerRepository.delete(dbPlayer);
        return "Successfully verified!";
    }

    @Transactional
    @Scheduled(every = "30m", identity = "clear-verification-code")
    void clear() {
        List<VerificationCode> codes = verificationCodeRepository.find(
            "FROM VerificationCode v WHERE v.expiration_date < " + "current_timestamp()").list();
        for (var code : codes) {
            if (code.getPlayer() != null)
                playerRepository.delete(code.getPlayer());
            verificationCodeRepository.delete(code);
        }
    }
}
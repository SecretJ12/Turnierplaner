package de.secretj12.tournierplaner.resources;

import de.secretj12.tournierplaner.entities.Player;
import de.secretj12.tournierplaner.entities.Tournament;
import de.secretj12.tournierplaner.repositories.PlayerRepository;
import de.secretj12.tournierplaner.repositories.TournamentRepository;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Path("/player/")
public class PlayerResource {

    public static class RegistrationData {
        private String first_name;
        private String last_name;

        private String email;
        private LocalDate birthdate;


        public RegistrationData() {
        }

        public RegistrationData(String first_name, String last_name, String email, LocalDate birthdate) {
            this.first_name = first_name;
            this.last_name = last_name;
            this.email = email;
            this.birthdate = birthdate;
        }

        public String getFirst_name() {
            return first_name;
        }

        public String getLast_name() {
            return last_name;
        }


        public LocalDate getBirthdate() {
            return birthdate;
        }
    }


    @Inject
    PlayerRepository playerRepository;


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
    public Response playerRegistration(RegistrationData registrationdata) {
        Player new_Player = new Player();
        new_Player.setBirthday(registrationdata.birthdate);
        new_Player.setFirstName(registrationdata.getFirst_name());
        new_Player.setLastName(registrationdata.getLast_name());

        playerRepository.persist(new_Player);

        return Response.ok("successfully added").build();
    }

}
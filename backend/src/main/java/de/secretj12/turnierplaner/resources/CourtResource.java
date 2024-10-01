package de.secretj12.turnierplaner.resources;

import de.secretj12.turnierplaner.db.entities.Court;
import de.secretj12.turnierplaner.db.repositories.CourtRepositiory;
import de.secretj12.turnierplaner.model.user.jUserCourt;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/court")
public class CourtResource {

    @Inject
    CourtRepositiory courtRepositiory;

    @GET
    public List<jUserCourt> getCourts() {
        return courtRepositiory.findAll().stream().map(jUserCourt::new).toList();
    }

    @PUT
    @Transactional
    @RolesAllowed("director")
    public String create(jUserCourt court) {
        Court exCourt = courtRepositiory.findByName(court.getName());
        if (exCourt != null)
            throw new WebApplicationException("Court already exists", Response.Status.CONFLICT);

        if (court.getName() == null || court.getName().isEmpty())
            throw new BadRequestException("Court name is required");
        if (court.getCourtType() == null)
            throw new BadRequestException("Court type is required");
        courtRepositiory.persist(court.toDB());
        return "Created";
    }

    @POST
    @Transactional
    @RolesAllowed("director")
    public String update(jUserCourt court) {
        Court exCourt = courtRepositiory.findByName(court.getName());
        if (exCourt == null)
            throw new NotFoundException("Court not found");

        if (court.getName() == null || court.getName().isEmpty())
            throw new BadRequestException("Court name is required");
        if (court.getCourtType() == null)
            throw new BadRequestException("Court type is required");
        exCourt.setName(court.getName());
        exCourt.setCourtType(court.getCourtType());
        courtRepositiory.persist(exCourt);
        return "Updated";
    }

    @DELETE
    @Path("/{name}")
    @Transactional
    @RolesAllowed("director")
    public String delete(@PathParam("name") String courtName) {
        Court exCourt = courtRepositiory.findByName(courtName);
        if (exCourt == null)
            throw new NotFoundException("Court does not exist");

        courtRepositiory.delete(exCourt);
        return "Deleted";
    }
}

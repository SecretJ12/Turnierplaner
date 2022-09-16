package template;

import io.quarkus.security.Authenticated;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/template")
public class Example {

    @GET
    @Path("/unauthorized")
    @Produces(MediaType.TEXT_PLAIN)
    @NoCache
    public String hello() {
        return "Hello unauthorized";
    }

    @GET
    @Path("/authorized")
    @Produces(MediaType.TEXT_PLAIN)
    @Authenticated
    @NoCache
    public String helloAuthorized() {
        return "Hello authorized";
    }

    @GET
    @Path("/user")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed("user")
    @NoCache
    public String helloUser() {
        return "Hello user!";
    }

    @GET
    @Path("/reporter")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed("reporter")
    @NoCache
    public String helloReporter() {
        return "Hello reporter!";
    }

    @GET
    @Path("/director")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed("director")
    @NoCache
    public String helloDirector() {
        return "Hello director!";
    }

    @GET
    @Path("/admin")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed("admin")
    @NoCache
    public String helloAdmin() {
        return "Hello admin!";
    }
}
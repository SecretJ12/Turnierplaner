package template;

import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/template")
public class Example {

    @GET
    @Path("/unauthorized")
    @Produces(MediaType.TEXT_PLAIN)
    public RestResponse<String> hello() {
        return ResponseBuilder.ok("Hello unauthorized").build();
    }

    @GET
    @Path("/authorized")
    @Produces(MediaType.TEXT_PLAIN)
    @Authenticated
    public RestResponse<String> helloAuthorized() {
        return ResponseBuilder.ok("Hello authorized").build();
    }

    @GET
    @Path("/user")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed("user")
    public RestResponse<String> helloUser() {
        return ResponseBuilder.ok("Hello user!").build();
    }

    @GET
    @Path("/reporter")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed("reporter")
    public RestResponse<String> helloReporter() {
        return ResponseBuilder.ok("Hello reporter!").build();
    }

    @GET
    @Path("/director")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed("director")
    public RestResponse<String> helloDirector() {
        return ResponseBuilder.ok("Hello director!").build();
    }

    @GET
    @Path("/admin")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed("admin")
    public RestResponse<String> helloAdmin() {
        return ResponseBuilder.ok("Hello admin!").build();
    }
}
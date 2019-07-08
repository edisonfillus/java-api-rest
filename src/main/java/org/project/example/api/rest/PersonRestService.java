package org.project.example.api.rest;

import java.net.URI;
import java.sql.SQLException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.project.example.model.Person;
import org.project.example.service.PersonService;

@Path("/persons")
public class PersonRestService {

    private static Logger log = LogManager.getLogger(PersonRestService.class);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(Person person, @Context UriInfo uriInfo) {
                
        // create person
        try {
            person = new PersonService().create(person);
        } catch (SQLException e) {
            log.error("Error trying to persist Person",e);
            JsonObject error = Json.createObjectBuilder()
                .add("type", "sqlException")
                .add("code", 500)
                .add("message", e.getMessage())
                .build();
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(error).build();
        }

        URI location = uriInfo.getAbsolutePathBuilder()
            .path(String.valueOf(person.getId())).build();

        Response response = Response.created(location).entity(person).build();

        return response;

    }
    
}
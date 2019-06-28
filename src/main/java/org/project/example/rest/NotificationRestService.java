package org.project.example.rest;

import java.io.File;
import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.security.RolesAllowed;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.project.example.dao.NotificationDAO;
import org.project.example.dto.Notification;

@Path("/notifications")
public class NotificationRestService {

    @Context
    SecurityContext securityContext;

    @GET
    @Produces({ MediaType.APPLICATION_JSON, "application/vnd.example.v2+json" })
    // @RolesAllowed({"admin"})
    public Response findAllv2() {
        // Principal principal = securityContext.getUserPrincipal();
        // String username = principal.getName();
        // System.out.println("User Principal: " + username);
        // fetch all notifications
        List<Notification> notifications = NotificationDAO.findAll();

        GenericEntity entity = new GenericEntity<List<Notification>>(notifications) {
        };

        Response response = Response.ok(entity).build();

        return response;
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ "application/vnd.example.v1+json; qs=0.8" })
    // @RolesAllowed({"admin"})
    public List<Notification> findAllv1() {
        // Principal principal = securityContext.getUserPrincipal();
        // String username = principal.getName();
        // System.out.println("User Principal: " + username);
        // fetch all notifications
        return NotificationDAO.findAll();
    }

    @GET
    @Path("{id: \\d+}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response fetchBy(@PathParam("id") long id) {

        // fetch notification by id
        Notification notification = NotificationDAO.find(id);
        if (notification == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        Response response = Response.ok(notification).build();
        return response;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(Notification notification, @Context UriInfo uriInfo) {

        // create notification
        notification = NotificationDAO.add(notification);

        URI location = uriInfo.getAbsolutePathBuilder().path(String.valueOf(notification.getId())).build();

        /*
         * Link link = Link.fromUri("http://{host}/v1/notifications/{id}")
         * .rel("current").type("application/json") .build("localhost", "1234");
         */

        Response response = Response.created(location).entity(notification).build();

        return response;

    }

    @PUT
    @Path("{id: \\d+}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(Notification notification, @PathParam("id") long id) {

        if (notification.getId() == null || notification.getId() != id) {
            JsonObject myObject = Json.createObjectBuilder()
                .add("error", "Notification ID invalid")
                .build();
            return Response.status(Status.BAD_REQUEST).entity(myObject).build();
        }

        Notification toUpdate = NotificationDAO.find(notification.getId());
        if (toUpdate == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        // update notification
        notification = NotificationDAO.update(notification);

        Response response = Response.noContent().build();

        return response;
    }

    @DELETE
    @Path("{id: \\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") int id) {
        // deleting notification
        boolean deleted = NotificationDAO.remove(id);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Status.GONE).build();
        }

    }

    

}
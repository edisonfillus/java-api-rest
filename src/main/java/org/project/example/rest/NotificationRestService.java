package org.project.example.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.project.example.dto.Notification;

@Path("/notifications")
public class NotificationRestService {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Notification> fetchAll() {
        // fetch all notifications
        List<Notification> notifications = new ArrayList<>();
        notifications.add(new Notification(1, "New user created"));
        notifications.add(new Notification(2, "New order created"));
        return notifications;
    }

    @GET
    @Path("{id: \\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Notification fetchBy(@PathParam("id") int id) {
        // fetch notification by id
        return new Notification(id, "Rise and shine.");
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Notification create(Notification notification) {
        // create notification
        return notification;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void update(Notification notification) {
        // update notification
    }

    @DELETE
    @Path("{id: \\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public void delete(@PathParam("id") int id) {
        // deleting notification
    }

}
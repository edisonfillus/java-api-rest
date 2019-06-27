package org.project.example.rest;

import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
	@Consumes({"application/vnd.example.v1+json", "application/vnd.example.v1+xml"})
	@Produces({"application/vnd.example.v1+json", "application/vnd.example.v1+xml"})
    //@RolesAllowed({"admin"})
    public List<Notification> findAllv1() {
   	  	//Principal principal = securityContext.getUserPrincipal();
    	//String username = principal.getName();
    	//System.out.println("User Principal: " + username);
         // fetch all notifications
         return NotificationDAO.findAll();
    }
	
	@GET
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/vnd.example.v2+json", "application/vnd.example.v2+xml"})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/vnd.example.v2+json", "application/vnd.example.v2+xml"})
    //@RolesAllowed({"admin"})
    public Response findAllv2() {
   	  	//Principal principal = securityContext.getUserPrincipal();
    	//String username = principal.getName();
    	//System.out.println("User Principal: " + username);
         // fetch all notifications
		 List<Notification> notifications = NotificationDAO.findAll();
		 
		 GenericEntity entity = new GenericEntity<List<Notification>>(notifications){};
		 
	     Response response = Response.ok(entity).build();
	       
	     return response;
    }
	
	
	

	@GET
    @Path("/v1/notifications/{id: \\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Notification fetchBy(@PathParam("id") int id) {
        // fetch notification by id
        return NotificationDAO.find(id);
    }

    @POST
    @Path("/v1/notifications")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(Notification notification, @Context UriInfo uriInfo) {

    	// create notification
    	notification = NotificationDAO.add(notification);
    	
    	URI location = uriInfo.getAbsolutePathBuilder().path(String.valueOf(notification.getId())).build();
    	
    	/*Link link = Link.fromUri("http://{host}/v1/notifications/{id}")
                .rel("current").type("application/json")
                .build("localhost", "1234"); */
    	
    	
        
        
        Response response = Response.created(location).build();
        
        return response;
        
    }

    @PUT
    @Path("/v1/notifications/{id: \\d+}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Notification update(Notification notification) {
        // update notification
    	return NotificationDAO.update(notification);
    }

    @DELETE
    @Path("{id: \\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") int id) {
        // deleting notification
    	boolean deleted = NotificationDAO.remove(id);
    	if(deleted) {
    		return Response.ok().build();
    	} else {
    		return Response.status(Status.GONE).build();
    	}
    	
    }

}
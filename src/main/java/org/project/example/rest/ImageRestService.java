package org.project.example.rest;

import java.io.File;

import javax.activation.MimetypesFileTypeMap;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/images")
public class ImageRestService {
    
    @GET @Path("{image}")
    @Produces("image/*")
    public Response getImage(@PathParam("image") String image) {
        File f = new File(image);

        if (!f.exists()) {
            JsonObject myObject = Json.createObjectBuilder()
            .add("error", "Image Not Found")
            .build();
            return Response.status(Status.NOT_FOUND).entity(myObject).build();
        }

        String mt = new MimetypesFileTypeMap().getContentType(f);
        return Response.ok(f, mt).build();
    }

    
}
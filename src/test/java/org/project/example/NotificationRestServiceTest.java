package org.project.example;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;
import org.project.example.dto.Notification;
import org.project.example.rest.AuthenticationRestService;
import org.project.example.rest.NotificationRestService;
import org.project.example.security.AuthenticationFilter;

public class NotificationRestServiceTest extends JerseyTest {

	static Logger log = LogManager.getLogger(NotificationRestServiceTest.class);
	
    @Override
    public Application configure() {
        ResourceConfig resourceConfig = new ResourceConfig();
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        resourceConfig.register(NotificationRestService.class);
        //resourceConfig.register(AuthenticationRestService.class);
        //resourceConfig.register(AuthenticationFilter.class);
        //resourceConfig.register(AuthorizationFilter.class);
        //resourceConfig.register(RolesAllowedDynamicFeature.class);     
        return resourceConfig;
    }


    
  

    //@Test
    public void testFetchAll() {
    	
    	//curl -i -H "Accept: application/vnd.example.v1+json" http://localhost:8080/notifications

        /*
    	// Get Authentication Token
    	MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
    	formData.add("username", "edisonkf");
    	formData.add("password", "123456");
    	Response authenticationResponse = target("/authentication").request().post(Entity.form(formData));
    	String token = authenticationResponse.readEntity(String.class);
    	assertEquals("should return status 200 on authentication", 200, authenticationResponse.getStatus());
    	assertNotNull("Should return token", token);
        */
        //Response output = target("/notifications").request().header(HttpHeaders.AUTHORIZATION, "Bearer " + token).get();
        //assertEquals("should return status 200", 200, output.getStatus());
        //assertNotNull("Should return list", output.getEntity());
    }

    @Test
    public void testFetchBy(){
        Response output = target("/notifications/1").request().get();
        assertEquals("Should return status 200", 200, output.getStatus());
        assertNotNull("Should return notification", output.getEntity());
    }

    //@Test
    public void testFetchByFail_DoesNotHaveDigit(){
        Response output = target("/notifications/no-id-digit").request().get();
        assertEquals("Should return status 404", 404, output.getStatus());
    }

    //@Test
    public void testCreate(){
        Notification notification = new Notification(null, "Invoice was deleted");
        Response output = target("/notifications")
                .request()
                .post(Entity.entity(notification, MediaType.APPLICATION_JSON));

        assertEquals("Should return status 200", 200, output.getStatus());
        assertNotNull("Should return notification", output.getEntity());
    }

    //@Test
    public void testUpdate(){
        Notification notification = new Notification(1l, "New user created at Antwerp");
        Response output = target("/notifications")
                .request()
                .put(Entity.entity(notification, MediaType.APPLICATION_JSON));
        assertEquals("Should return status 204", 204, output.getStatus());
    }

    //@Test
    public void testDelete(){
        Response output = target("/notifications/1").request().delete();
        assertEquals("Should return status 204", 204, output.getStatus());
    }

}
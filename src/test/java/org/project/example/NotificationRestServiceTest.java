package org.project.example;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;
import org.project.example.dto.Notification;
import org.project.example.rest.NotificationRestService;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

public class NotificationRestServiceTest extends JerseyTest {

    @Override
    public Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(NotificationRestService.class);
    }

    @Test
    public void testFetchAll() {
        Response output = target("/notifications").request().get();
        assertEquals("should return status 200", 200, output.getStatus());
        assertNotNull("Should return list", output.getEntity());
    }

    @Test
    public void testFetchBy(){
        Response output = target("/notifications/1").request().get();
        assertEquals("Should return status 200", 200, output.getStatus());
        assertNotNull("Should return notification", output.getEntity());
    }

    @Test
    public void testFetchByFail_DoesNotHaveDigit(){
        Response output = target("/notifications/no-id-digit").request().get();
        assertEquals("Should return status 404", 404, output.getStatus());
    }

    @Test
    public void testCreate(){
        Notification notification = new Notification(null, "Invoice was deleted");
        Response output = target("/notifications")
                .request()
                .post(Entity.entity(notification, MediaType.APPLICATION_JSON));

        assertEquals("Should return status 200", 200, output.getStatus());
        assertNotNull("Should return notification", output.getEntity());
    }

    @Test
    public void testUpdate(){
        Notification notification = new Notification(1, "New user created at Antwerp");
        Response output = target("/notifications")
                .request()
                .put(Entity.entity(notification, MediaType.APPLICATION_JSON));
        assertEquals("Should return status 204", 204, output.getStatus());
    }

    @Test
    public void testDelete(){
        Response output = target("/notifications/1").request().delete();
        assertEquals("Should return status 204", 204, output.getStatus());
    }

}
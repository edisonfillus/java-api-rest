package org.project.example.api.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.project.example.api.rest.JerseyApplicationConfig;
import org.project.example.model.Notification;

@TestMethodOrder(OrderAnnotation.class)
public class NotificationRestServiceTest {

    private static Logger log = LogManager.getLogger(NotificationRestServiceTest.class);

    private static Server server;
    private static URI serverUri;

    @BeforeAll
    public static void startJetty() throws Exception {
        // Create Server
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(0); // auto-bind to available port
        server.addConnector(connector);

        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        servletContextHandler.setContextPath("/");
        server.setHandler(servletContextHandler);
        ServletHolder servletHolder = servletContextHandler.addServlet(ServletContainer.class, "/api/*");
        servletHolder.setInitOrder(0);
        servletHolder.setInitParameter("javax.ws.rs.Application",
                JerseyApplicationConfig.class.getName());
        // Start Server
        server.start();

        // Determine Base URI for Server
        String host = connector.getHost() == null ? "localhost" : connector.getHost();
        int port = connector.getLocalPort();
        serverUri = new URI(String.format("http://%s:%d", host, port));
    }

    @AfterAll
    public static void stopJetty()
    {
        try {
            server.stop();
        } catch (Exception e) {
            log.error(e);
        }
    }


    @Test
    @Order(1) 
    public void testCreate() {
        Notification notification = new Notification(1l, "Invoice was deleted");
        Response output = ClientBuilder.newClient()
                            .target(serverUri).path("/api").path("/notifications").request()
                .post(Entity.entity(notification, MediaType.APPLICATION_JSON));

        assertEquals(201, output.getStatus(),"Should return status 201");
        assertNotNull(output.getEntity(), "Should return notification");
    }

    @Test
    @Order(2) 
    public void testFetchBy() {
        Response output = ClientBuilder.newClient().target(serverUri).path("/api").path("/notifications/1").request()
                .get();
        assertEquals(200, output.getStatus(), "Should return status 200");
        assertNotNull(output.getEntity(),"Should return notification");
    }

    @Test
    @Order(3) 
    public void testFetchByHtml() {
        Response output = ClientBuilder.newClient().target(serverUri).path("/api").path("/notifications/1/html").request()
                .get();
        assertEquals(200, output.getStatus(), "Should return status 200");
        assertNotNull(output.getEntity(),"Should return notification");
    }

    @Test
    @Order(4) 
    public void testFetchByFail_DoesNotHaveDigit() {
        Response output = ClientBuilder.newClient().target(serverUri).path("/api").path("/notifications/no-id-digit")
                .request().get();
        assertEquals(404, output.getStatus(),"Should return status 404");
    }

    @Test
    @Order(5) 
    public void testUpdate() {
        Notification notification = new Notification(1l, "New user created at Antwerp");
        Response output = ClientBuilder.newClient()
                            .target(serverUri).path("/api").path("/notifications/1").request()
                .put(Entity.entity(notification, MediaType.APPLICATION_JSON));
        assertEquals(204, output.getStatus(),"Should return status 204");
    }


    @Test
    @Order(6) 
    public void testDelete() {
        Response output = ClientBuilder.newClient().target(serverUri).path("/api").path("/notifications/1").request()
                .delete();
        assertEquals(204, output.getStatus(),"Should return status 204");

    }





    // @Test
    public void testFetchAll() {

        // curl -i -H "Accept: application/vnd.example.v1+json"
        // http://localhost:8080/notifications

        /*
         * // Get Authentication Token MultivaluedMap<String, String> formData = new
         * MultivaluedHashMap<String, String>(); formData.add("username", "edisonkf");
         * formData.add("password", "123456"); Response authenticationResponse =
         * target("/authentication").request().post(Entity.form(formData)); String token
         * = authenticationResponse.readEntity(String.class);
         * assertEquals("should return status 200 on authentication", 200,
         * authenticationResponse.getStatus()); assertNotNull("Should return token",
         * token);
         */
        // Response output =
        // target("/notifications").request().header(HttpHeaders.AUTHORIZATION, "Bearer
        // " + token).get();
        // assertEquals("should return status 200", 200, output.getStatus());
        // assertNotNull("Should return list", output.getEntity());
    }

    

  

   

   

    

}
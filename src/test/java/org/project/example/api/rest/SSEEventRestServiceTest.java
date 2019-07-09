package org.project.example.api.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.sse.SseEventSource;

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

public class SSEEventRestServiceTest {

    private static Logger log = LogManager.getLogger(SSEEventRestServiceTest.class);

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
        servletHolder.setInitParameter("javax.ws.rs.Application", JerseyApplicationConfig.class.getName());
        // Start Server
        server.start();

        // Determine Base URI for Server
        String host = connector.getHost() == null ? "localhost" : connector.getHost();
        int port = connector.getLocalPort();
        serverUri = new URI(String.format("http://%s:%d", host, port));
    }

    @AfterAll
    public static void stopJetty() {
        try {
            server.stop();
        } catch (Exception e) {
            log.error(e);
        }
    }

    @Test
    @Order(1)
    public void testSSE() throws InterruptedException {

        WebTarget target = ClientBuilder.newClient().target(serverUri).path("/api").path("/events");
        SseEventSource sseEventSource = SseEventSource.target(target).build();

        List<String> events = new ArrayList<String>();
        CountDownLatch countDownLatch = new CountDownLatch(10);
        
        sseEventSource.register(
            (event) -> {
                log.info(event);
                events.add(event.readData(String.class));
                countDownLatch.countDown();}
        );
        sseEventSource.open();

        countDownLatch.await(11, TimeUnit.SECONDS);

        sseEventSource.close();

        assertNotNull(events);
        assertEquals(10,events.size());
    }

}
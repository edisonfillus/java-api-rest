package org.project.example.api.servlets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.project.example.api.servlets.AsyncServlet;

public class AsyncServletTest {
    private static Server server;
    private static URI serverUri;
    private static Logger log = LogManager.getLogger(AsyncServletTest.class);

    @BeforeAll
    public static void startJetty() throws Exception {
        // Create Server
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(0); // auto-bind to available port
        server.addConnector(connector);

        // Add Servlets
        ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(AsyncServlet.class, "/async");
        server.setHandler(servletHandler);

        // Start Server
        server.start();

        // Determine Base URI for Server
        String host = connector.getHost();
        if (host == null) {
            host = "localhost";
        }
        int port = connector.getLocalPort();
        serverUri = new URI(String.format("http://%s:%d", host, port));
    }

    @AfterAll
    public static void stopJetty()
    {
        try
        {
            server.stop();
        }
        catch (Exception e)
        {
            log.error(e);
        }
    }

    @Test
    public void testGet() throws Exception
    {
      
        Response output = ClientBuilder.newClient()
                            .target(serverUri).path("/async")
                            .request().get();
      
        assertEquals(200, output.getStatus(),"Should return status 200");
        assertTrue(output.readEntity(String.class).contains("async"),"Should return string async on reponse");
        
    }
}
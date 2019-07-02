package org.project.example.servlets;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import javax.json.JsonObject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletHandler;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class BlockingServletTest
{
    private static Server server;
    private static URI serverUri;
    private static Logger log = LogManager.getLogger(BlockingServletTest.class);
	

    @BeforeClass
    public static void startJetty() throws Exception
    {
        // Create Server
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(0); // auto-bind to available port
		server.addConnector(connector);

        // Add Servlets
        HandlerList handlerList=new HandlerList();
        ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(BlockingServlet.class, "/blocking");
		handlerList.addHandler(servletHandler);
		server.setHandler(handlerList);

        // Start Server
        server.start();

        // Determine Base URI for Server
        String host = connector.getHost() == null ? "localhost" : connector.getHost();
        int port = connector.getLocalPort();
        serverUri = new URI(String.format("http://%s:%d",host,port));
    }

    @AfterClass
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
                            .target(serverUri).path("/blocking")
                            .request().get();
      
        assertEquals("Should return status 200", 200, output.getStatus());
        assertEquals("Should return status=blocking", "blocking", output.readEntity(JsonObject.class).getString("status"));
        
    }
}
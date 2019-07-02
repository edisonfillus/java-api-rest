package org.project.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.net.URI;
import javax.json.JsonObject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.JettyWebXmlConfiguration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.project.example.dto.Notification;

public class JettyIntegrationTest {
    private static Server server;
    private static URI serverUri;
    private static Logger log = LogManager.getLogger(JettyIntegrationTest.class);

    @BeforeAll
    public static void startJetty() throws Exception {
        // Create Server
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(0); // auto-bind to available port
        server.addConnector(connector);

        // Load webapp context on /
        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");
        webapp.setExtractWAR(false);
        webapp.setResourceBase(new File("src/main/webapp/").getAbsolutePath());

        // Set the compiled classes directory
        // URL[] urls = new URL[]{
        // new File("src/main/webapp").toPath().toFile().toURI().toURL(),
        // new File("target/classes").toPath().toFile().toURI().toURL()};
        // webapp.setClassLoader(new URLClassLoader(urls));
        webapp.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*/classes/.*");

        // Start Full Servlet Container
        webapp.setConfigurations(new Configuration[] { new AnnotationConfiguration(), new WebInfConfiguration(),
                new WebXmlConfiguration(), new MetaInfConfiguration(), new FragmentConfiguration(),
                new EnvConfiguration(), new PlusConfiguration(), new JettyWebXmlConfiguration() });

        // Include webapp handler on server
        HandlerList handlerList = new HandlerList();
        handlerList.addHandler(webapp);
        server.setHandler(handlerList);

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
    public void testJSP() throws Exception
    {
      
        Response output = ClientBuilder.newClient()
                            .target(serverUri).path("/")
                            .request().get();
      
        assertEquals(200, output.getStatus(),"Should return status 200");
        assertTrue(output.readEntity(String.class).contains("<h2>Hello World!</h2>"),"Should return index.jsp");
        
    }

    @Test
    public void testServlet() throws Exception
    {
      
        Response output = ClientBuilder.newClient()
                            .target(serverUri).path("/blocking")
                            .request().get();
      
        assertEquals(200, output.getStatus(),"Should return status 200");
        assertEquals("blocking", output.readEntity(JsonObject.class).getString("status"),"Should return status=blocking");
        
    }

    @Test
    public void testServletAsync() throws Exception
    {
      
        Response output = ClientBuilder.newClient()
                            .target(serverUri).path("/async")
                            .request().get();
      
        assertEquals(200, output.getStatus(),"Should return status 200");
        assertTrue(output.readEntity(String.class).contains("async"),"Should return string async on reponse");
        
    }

    @Test
    public void testJerseyAPIRest(){
        Response output = ClientBuilder.newClient()
                            .target(serverUri).path("/api/notifications/1")
                            .request().get();
        assertEquals(200, output.getStatus(),"Should return status 200");
        assertNotNull(output.getEntity(),"Should return notification");

        assertEquals(1,output.readEntity(Notification.class).getId().longValue(),"Should return notification 1");


    }

}
package org.project.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.JettyWebXmlConfiguration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebAppClassLoader;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.project.example.dto.Notification;
import org.project.example.servlets.BlockingServlet;

public class JettyIntegrationTest
{
    private static Server server;
    private static URI serverUri;
    private static Logger log = LogManager.getLogger(JettyIntegrationTest.class);
	

    @BeforeClass
    public static void startJetty() throws Exception
    {
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
        URL[] urls = new URL[]{
                new File("src/main/webapp").toPath().toFile().toURI().toURL(),
                new File("target/classes").toPath().toFile().toURI().toURL()};
        webapp.setClassLoader(new URLClassLoader(urls));
        webapp.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*/classes/.*");
        

        // Start Full Servlet Container
        webapp.setConfigurations(new Configuration[] 
        { 
            new AnnotationConfiguration(),
            new WebInfConfiguration(), 
            new WebXmlConfiguration(),
            new MetaInfConfiguration(), 
            new FragmentConfiguration(), 
            new EnvConfiguration(),
            new PlusConfiguration(), 
            new JettyWebXmlConfiguration() 
        });

      
        

        // Include webapp handler on server
		HandlerList handlerList=new HandlerList();
        handlerList.addHandler(webapp);
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
      
        assertEquals("Should return status 200", 200, output.getStatus());
        assertTrue("Should return index.jsp", output.readEntity(String.class).contains("<h2>Hello World!</h2>"));
        
    }

    @Test
    public void testServlet() throws Exception
    {
      
        Response output = ClientBuilder.newClient()
                            .target(serverUri).path("/blocking")
                            .request().get();
      
        assertEquals("Should return status 200", 200, output.getStatus());
        assertEquals("Should return status=blocking", "blocking", output.readEntity(JsonObject.class).getString("status"));
        
    }

    @Test
    public void testServletAsync() throws Exception
    {
      
        Response output = ClientBuilder.newClient()
                            .target(serverUri).path("/async")
                            .request().get();
      
        assertEquals("Should return status 200", 200, output.getStatus());
        assertTrue("Should return string async on reponse", output.readEntity(String.class).contains("async"));
        
    }

    @Test
    public void testJerseyAPIRest(){
        Response output = ClientBuilder.newClient()
                            .target(serverUri).path("/api/notifications/1")
                            .request().get();
        assertEquals("Should return status 200", 200, output.getStatus());
        assertNotNull("Should return notification", output.getEntity());

        assertEquals("Should return notification 1",1,output.readEntity(Notification.class).getId().longValue());


    }

}
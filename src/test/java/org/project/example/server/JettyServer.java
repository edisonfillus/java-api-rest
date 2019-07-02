package org.project.example.server;

import java.io.File;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.JettyWebXmlConfiguration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;

public class JettyServer {

    

	public static void main(String[] args) throws Exception {

        // Create ThreadPool
		int maxThreads = 100;
		int minThreads = 10;
		int idleTimeout = 120;
		QueuedThreadPool threadPool = new QueuedThreadPool(maxThreads, minThreads, idleTimeout);

		// Create Server
		Server server = new Server(threadPool);
		ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
		server.addConnector(connector);

		// Load webapp context on /
		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath("/");
		webapp.setExtractWAR(false);
		webapp.setResourceBase(new File("src/main/webapp/").getAbsolutePath());
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
        
		// Set the compiled classes directory
		webapp.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*/classes/.*");
        
        // Include webapp handler on server
		HandlerList handlerList=new HandlerList();
		handlerList.addHandler(webapp);
		server.setHandler(handlerList);

		// Flip the classloader priority from servlet spec where webapp is first to
		// Standard java behavior of parent (aka Server classloader) is first.
		//webapp.setParentLoaderPriority(false);

        // Start Server
		server.start();

        // Join current thread
        server.join();
        
        /*
		// This webapp will use jsps and jstl. We need to enable the
        // AnnotationConfiguration in order to correctly
		// set up the jsp container
        Configuration.ClassList classlist = Configuration.ClassList.setServerDefault( server );
		// Enable JNDI
		classlist.addAfter(
				"org.eclipse.jetty.webapp.FragmentConfiguration",
				"org.eclipse.jetty.plus.webapp.EnvConfiguration",
				"org.eclipse.jetty.plus.webapp.PlusConfiguration");
		// Enable Annotation Scanning
		classlist.addBefore(
                "org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
                "org.eclipse.jetty.annotations.AnnotationConfiguration" );
		*/
 		// Set the ContainerIncludeJarPattern so that jetty examines these
        // container-path jars for tlds, web-fragments etc.
        // If you omit the jar that contains the jstl .tlds, the jsp engine will
        // scan for them instead.
        //webapp.setAttribute(
        //        "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
        //        ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/[^/]*taglibs.*\\.jar$" );
        
	}

}

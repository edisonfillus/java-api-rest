package org.project.example.server;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.plus.jndi.Transaction;
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
import org.eclipse.jetty.util.resource.Resource;

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


	public void main2(String[] args) throws Exception
    {
        int port = 8080;
        Server server = new Server(port);

      	//URI webResourceBase = findWebResourceBase(server.getClass().getClassLoader());
		//System.err.println("Using BaseResource: " + webResourceBase);
		String webResourceBase = new File("src/main/webapp/").getAbsolutePath();

        WebAppContext context = new WebAppContext();
        context.setBaseResource(Resource.newResource(webResourceBase));
        context.setConfigurations(new Configuration[] 
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
		context.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*/classes/.*");
        context.setContextPath("/");
        context.setParentLoaderPriority(true);
        server.setHandler(context);
        server.start();
        server.dump(System.err);
        server.join();
    }

    private static URI findWebResourceBase(ClassLoader classLoader)
    {
        String webResourceRef = "WEB-INF/web.xml";

        try
        {
            // Look for resource in classpath (best choice when working with archive jar/war file)
            URL webXml = classLoader.getResource('/'+webResourceRef);
            if (webXml != null)
            {
                URI uri = webXml.toURI().resolve("..").normalize();
                System.err.printf("WebResourceBase (Using ClassLoader reference) %s%n", uri);
                return uri;
            }
        }
        catch (URISyntaxException e)
        {
            throw new RuntimeException("Bad ClassPath reference for: " + webResourceRef,e);
        }
        
        // Look for resource in common file system paths
        try
        {
            Path pwd = new File(System.getProperty("user.dir")).toPath().toRealPath();
            FileSystem fs = pwd.getFileSystem();
            
            // Try the generated maven path first
            PathMatcher matcher = fs.getPathMatcher("glob:**/embedded-servlet-*");
            try (DirectoryStream<Path> dir = Files.newDirectoryStream(pwd.resolve("target")))
            {
                for(Path path: dir)
                {
                    if(Files.isDirectory(path) && matcher.matches(path))
                    {
                        // Found a potential directory
                        Path possible = path.resolve(webResourceRef);
                        // Does it have what we need?
                        if(Files.exists(possible))
                        {
                            URI uri = path.toUri();
                            System.err.printf("WebResourceBase (Using discovered /target/ Path) %s%n", uri);
                            return uri;
                        }
                    }
                }
            }
            
            // Try the source path next
            Path srcWebapp = pwd.resolve("src/main/webapp/" + webResourceRef);
            if(Files.exists(srcWebapp))
            {
                URI uri = srcWebapp.getParent().toUri();
                System.err.printf("WebResourceBase (Using /src/main/webapp/ Path) %s%n", uri);
                return uri;
            }
        }
        catch (Throwable t)
        {
            throw new RuntimeException("Unable to find web resource in file system: " + webResourceRef, t);
        }
        
        throw new RuntimeException("Unable to find web resource ref: " + webResourceRef);
    }
	
}

package org.project.example.server;

import java.net.URI;

import javax.json.stream.JsonGenerator;
import javax.ws.rs.core.UriBuilder;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.jsonp.JsonProcessingFeature;
import org.glassfish.jersey.server.ResourceConfig;


public class ServerHTTP {

	public static void main(String[] args) throws Exception {
		ResourceConfig config = new ResourceConfig()
			.packages("org.project.example.rest")
			.register(JsonProcessingFeature.class)
			
			.property(JsonGenerator.PRETTY_PRINTING, true);
		URI uri = UriBuilder.fromUri("http://localhost/").port(8080).build();
		Server server = JettyHttpContainerFactory.createServer(uri, config);
		System.out.println("Server running on URI " + server.getURI().toString());
	}

}

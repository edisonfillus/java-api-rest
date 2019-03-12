package org.project.example;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Jersey application Configuration. This configures jersey so we don’t need a web.xml file.
 * @author c084242
 *
 */
@ApplicationPath("/api")
public class ApplicationConfig extends Application {

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("jersey.config.server.provider.packages", "com.memorynotfound.rs");
        //properties.put("com.sun.jersey.spi.container.ResourceFilters", "com.sun.jersey.api.container.filter.RolesAllowedResourcFilterFactory");
        
        return properties;
    }
    
}
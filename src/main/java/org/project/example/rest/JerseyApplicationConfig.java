package org.project.example.rest;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.freemarker.FreemarkerMvcFeature;

public class JerseyApplicationConfig extends ResourceConfig {
    public JerseyApplicationConfig() {
        packages("org.project.example.rest");
        property(FreemarkerMvcFeature.TEMPLATE_BASE_PATH, "templates");
        register(FreemarkerMvcFeature.class);;
    }
}
package org.project.example.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {

    public Properties readProperties(String file) throws IOException {
        InputStream inputStream = PropertiesReader.class.getClassLoader().getResourceAsStream(file);
        Properties prop = new Properties();
        if (inputStream == null) {
            new FileNotFoundException("property file '" + file + "' not found in the classpath");
        }
        prop.load(inputStream);
        return prop;
    }
}
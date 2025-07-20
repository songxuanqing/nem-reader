package org.nemreader.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static Properties props = new Properties();

    static {
        try (InputStream is = Config.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (is == null) {
                throw new RuntimeException("Can not find application.properties.");
            }
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Fail to load application.properties", e);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}

package com.github.bmsantos.core.cola.config;

import static java.io.File.separator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public enum ConfigurationManager {

    config;

    private final Properties props = new Properties();

    private ConfigurationManager() {
        loadProperties("application.properties");
        loadProperties("messages.properties");
    }

    public void loadProperties(final String name) {
        InputStream in = null;
        try {
            in = ConfigurationManager.class.getResourceAsStream(separator + name);
            props.load(in);
            in.close();
        } catch (final IOException e) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (final Exception e) {
                    // empty
                }
            }
        }
    }

    public String getProperty(final String key) {
        return props.getProperty(key);
    }

    public String error(final String error) {
        return getProperty("error." + error);
    }

    public String warn(final String warn) {
        return getProperty("warn." + warn);
    }

    public String info(final String info) {
        return getProperty("info." + info);
    }
}

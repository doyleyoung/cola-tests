/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.bmsantos.core.cola.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public enum ConfigurationManager {

    config;

    private static Logger log = LoggerFactory.getLogger(ConfigurationManager.class);

    private final Properties props = new Properties();

    private ConfigurationManager() {
        loadProperties("cola-tests-application.properties");
        loadProperties("cola-tests-messages.properties");
    }

    public void loadProperties(final String name) {
        try (final InputStream in = ConfigurationManager.class.getResourceAsStream("/" + name)) {
            props.load(in);
        } catch (final IOException e) {
            log.error("Failed to load configuration properties file: " + name);
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

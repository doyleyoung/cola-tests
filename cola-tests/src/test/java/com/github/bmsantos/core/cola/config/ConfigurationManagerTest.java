package com.github.bmsantos.core.cola.config;

import static com.github.bmsantos.core.cola.config.ConfigurationManager.config;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ConfigurationManagerTest {

    private static final String ERROR_PROCESSING = "There were errors while processing COLA JUnit Tests.";
    private static final String WARN_TEST = "warning...";
    private static final String INFO_TEST = "info...";

    @Test
    public void shouldLoadProperties() {
        // When
        final String name = config.getProperty("app.name");

        // Then
        assertThat(name, is("cola-tests"));
    }

    @Test
    public void shouldGetError() {
        // When
        final String name = config.error("processing");

        // Then
        assertThat(name, is(ERROR_PROCESSING));
    }

    @Test
    public void shouldGetWarn() {
        // When
        final String name = config.warn("test");

        // Then
        assertThat(name, is(WARN_TEST));
    }

    @Test
    public void shouldGetInfo() {
        // When
        final String name = config.info("test");

        // Then
        assertThat(name, is(INFO_TEST));
    }
}

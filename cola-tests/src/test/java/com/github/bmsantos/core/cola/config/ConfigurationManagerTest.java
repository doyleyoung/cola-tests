package com.github.bmsantos.core.cola.config;

import static com.github.bmsantos.core.cola.config.ConfigurationManager.config;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ConfigurationManagerTest {

    private static final String ERROR_PROCESSING = "There were errors while processing COLA JUnit Tests.";
    private static final String WARN_MISSING_IDE_TEST = "ideBaseClassTest method not set. Will look for default JUnit Test named 'iWillBeRemoved' method and remove if available.";
    private static final String INFO_DEFAULT_CLASS = "Found default ideBaseClass class. Proceeding...";

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
        final String name = config.warn("missing.ide.test");

        // Then
        assertThat(name, is(WARN_MISSING_IDE_TEST));
    }

    @Test
    public void shouldGetInfo() {
        // When
        final String name = config.info("found.default.ide.class");

        // Then
        assertThat(name, is(INFO_DEFAULT_CLASS));
    }
}

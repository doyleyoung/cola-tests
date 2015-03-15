package com.github.bmsantos.core.cola.story.processor;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import static uk.org.lidalia.slf4jtest.LoggingEvent.warn;

import org.junit.Test;

import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

public class StoryProcessorTest {

    private static final String FEATURE = "Feature";
    private static final String SCENARIO = "Scenario";

    private final TestLogger logger = TestLoggerFactory.getTestLogger(StoryProcessor.class);

    @Test
    public void shouldLogOnIgnore() {
        // When
        StoryProcessor.ignore(FEATURE, SCENARIO);

        // Then
        assertThat(logger.getLoggingEvents(), hasItem(warn("Feature: " + FEATURE + " - Scenario: " + SCENARIO
            + " (@ignored)")));
    }
}

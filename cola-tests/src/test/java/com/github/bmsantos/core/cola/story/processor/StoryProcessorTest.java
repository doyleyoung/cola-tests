package com.github.bmsantos.core.cola.story.processor;

import org.junit.Test;
import test.utils.Story;
import test.utils.StoryDependencies;
import test.utils.StoryDependsOn;
import uk.org.lidalia.slf4jtest.TestLogger;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static uk.org.lidalia.slf4jtest.LoggingEvent.warn;
import static uk.org.lidalia.slf4jtest.TestLoggerFactory.getTestLogger;

public class StoryProcessorTest {

    private static final String FEATURE = "Feature";
    private static final String SCENARIO = "Scenario";

    private final TestLogger logger = getTestLogger(StoryProcessor.class);

    @Test
    public void shouldLogOnIgnore() {
        // When
        StoryProcessor.ignore(FEATURE, SCENARIO);

        // Then
        assertThat(logger.getLoggingEvents(), hasItem(warn("Feature: " + FEATURE + " - Scenario: " + SCENARIO
            + " (@ignored)")));
    }

    @Test
    public void shouldProcessStory() throws Throwable {
        // Given
        final Story story = new Story();

        // When
        StoryProcessor.process(FEATURE, SCENARIO, story.getStory(), null, null, story);

        // Then
        assertThat(story.wasCalled(), is(true));
    }

    @Test
    public void shouldProcessDependsOn() throws Throwable {
        // Given
        final StoryDependsOn story = new StoryDependsOn();
        StoryDependsOn.resetTimesCount();

        // When
        StoryProcessor.process(FEATURE, SCENARIO, story.getStory(), null, null, story);

        // Then
        assertThat(story.wasCalled(), is(true));
        assertThat(story.howOften(), is(4));
    }

    @Test
    public void shouldProcessDependencies() throws Throwable {
        // Given
        final StoryDependencies story = new StoryDependencies();
        StoryDependencies.resetTimesCount();

        // When
        StoryProcessor.process(FEATURE, SCENARIO, story.getStory(), null, null, story);

        // Then
        assertThat(story.wasCalled(), is(true));
        assertThat(story.howOften(), is(10));
    }
}

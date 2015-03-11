package com.github.bmsantos.core.cola.formatter;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import gherkin.formatter.model.Comment;
import gherkin.formatter.model.Examples;
import gherkin.formatter.model.ExamplesTableRow;
import gherkin.formatter.model.Step;
import gherkin.formatter.model.Tag;
import gherkin.formatter.model.TagStatement;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class ScenarioDetailsTest {

    private static final List<Comment> NO_COMMENTS = Collections.<Comment> emptyList();

    private ScenarioDetails uut;

    @Mock
    private TagStatement scenario;
    @Mock
    private Examples examples;

    @Before
    public void setUp() {
        initMocks(this);

        final ExamplesTableRow header = new ExamplesTableRow(NO_COMMENTS, asList("beers"), 1, "1");
        final ExamplesTableRow value = new ExamplesTableRow(NO_COMMENTS, asList("100"), 1, "1");
        when(examples.getRows()).thenReturn(asList(header, value));

        uut = new ScenarioDetails(scenario);
    }

    @Test
    public void shouldHaveScenario() {
        // When
        final TagStatement result = uut.getScenario();

        // Then
        assertThat(result, equalTo(scenario));
    }

    @Test
    public void shouldInitializeStepsList() {
        // When
        final List<Step> steps = uut.getSteps();

        // Then
        assertThat(steps, notNullValue());
    }

    @Test
    public void shouldHaveProjectionValues() {
        // When
        uut.setExamples(examples);

        // Then
        assertThat(uut.hasProjectionValues(), equalTo(true));
    }

    @Test
    public void shouldSetProjectionValues() {
        // When
        uut.setExamples(examples);

        // Then
        assertThat(uut.getProjectionValues(), notNullValue());
    }

    @Test
    public void shouldIgnore() {
        // Given
        final Tag tag = new Tag("@IgNore", 1);
        when(scenario.getTags()).thenReturn(Arrays.asList(tag));

        // When
        final boolean result = uut.ignore();

        // Then
        assertThat(result, is(true));
    }

    @Test
    public void shouldNotIgnore() {
        // Given
        final Tag tag = new Tag("@OtheR", 1);
        when(scenario.getTags()).thenReturn(Arrays.asList(tag));

        // When
        final boolean result = uut.ignore();

        // Then
        assertThat(result, is(false));
    }
}

package com.github.bmsantos.core.cola.formatter;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import gherkin.formatter.model.Step;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class FeatureFormatterExamplesTest {

    private static final String PATH_TO_FEATURE = "/path/to/feature";
    private static final String GIVEN = "there are <start> cucumbers";
    private static final String WHEN = "I eat <eat> cucumbers";
    private static final String THEN = "I should have <left> cucumbers";

    private final String feature =
        "Feature: An example feature\n"
            + "Scenario Outline: Should parse examples\n"
            + "Given " + GIVEN + "\n"
            + "When " + WHEN + "\n"
            + "Then " + THEN + "\n"
            + "\n"
            + "Examples:\n"
            + " | start | eat | left |\n"
            + " | 12    | 5   | 7    |\n"
            + " | 20    | 5   | 15   |";

    private FeatureDetails featureDetails;

    @Before
    public void setUp() {
        featureDetails = FeatureFormatter.parse(feature, PATH_TO_FEATURE);
    }

    @Test
    public void shoulParseExamples() {
        // Then
        assertThat(featureDetails.getScenarios(), notNullValue());
    }

    @Test
    public void shouldIncludeSteps() {
        // When
        final List<Step> steps = featureDetails.getScenarios().get(0).getSteps();

        // Then
        final List<String> stepNames = asList(steps.get(0).getName(), steps.get(1).getName(), steps.get(2).getName());
        assertThat(stepNames, contains(GIVEN, WHEN, THEN));
    }

    @Test
    public void shouldDoProjectionForGivenRow() {
        // When
        final String result = featureDetails.getScenarios().get(0).getProjectionValues().doRowProjection(0);

        // Then
        assertThat(result, containsString("\"start\":\"12\""));
        assertThat(result, containsString("\"eat\":\"5\""));
        assertThat(result, containsString("\"left\":\"7\""));
    }
}

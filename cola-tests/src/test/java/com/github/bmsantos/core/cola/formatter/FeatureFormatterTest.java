package com.github.bmsantos.core.cola.formatter;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.Step;
import gherkin.lexer.LexingError;

import java.util.List;

import org.junit.Test;

import com.github.bmsantos.core.cola.exceptions.InvalidFeature;
import com.github.bmsantos.core.cola.exceptions.InvalidFeatureUri;
import com.github.bmsantos.core.cola.formatter.FeatureDetails;
import com.github.bmsantos.core.cola.formatter.FeatureFormatter;
import com.github.bmsantos.core.cola.formatter.ScenarioDetails;

public class FeatureFormatterTest {

    private static final String STEP_SIX = "the result will be addition of both numbers";
    private static final String STEP_FIVE = "added together";
    private static final String STEP_FOUR = "B";
    private static final String STEP_THREE = "A";
    private static final String FEATURE_NAME = "Introduce addition";
    private static final String BACKGROUND_NAME = "Should run before scenarios";
    private static final String SCENARIO_NAME = "Should add two numbers";
    private static final String GIVEN = "Given ";
    private static final String AND = "And ";
    private static final String THEN = "Then ";
    private static final String WHEN = "When ";
    private static final String STEP_ONE = "a step";
    private static final String STEP_TWO = "another step";

    private static final String PATH_TO_FEATURE = "/path/to/feature";

    private final String feature =
        "Feature: " + FEATURE_NAME +"\n"
            + "Background: " + BACKGROUND_NAME + "\n"
            + GIVEN + STEP_ONE + "\n"
            + AND + STEP_TWO + "\n"
            + "\n"
            + "Scenario: " + SCENARIO_NAME + "\n"
            + GIVEN + STEP_THREE + "\n"
            + AND + STEP_FOUR + "\n"
            + WHEN + STEP_FIVE + "\n"
            + THEN + STEP_SIX;

    @Test
    public void shoulParseUri() {
        // When
        final FeatureDetails featureDetails = FeatureFormatter.parse(feature, PATH_TO_FEATURE);

        // Then
        assertThat(featureDetails.getUri(), equalTo(PATH_TO_FEATURE));
    }

    @Test
    public void shoulParseFeature() {
        // When
        final FeatureDetails featureDetails = FeatureFormatter.parse(feature, PATH_TO_FEATURE);

        // Then
        assertThat(featureDetails.getFeature().getName(), equalTo(FEATURE_NAME));
    }

    @Test
    public void shoulParseBackground() {
        // When
        final FeatureDetails featureDetails = FeatureFormatter.parse(feature, PATH_TO_FEATURE);

        // Then
        assertThat(featureDetails.getBackground().getName(), equalTo(BACKGROUND_NAME));

        assertThat(featureDetails.getBackgroundSteps().get(0).getKeyword(), equalTo(GIVEN));
        assertThat(featureDetails.getBackgroundSteps().get(0).getName(), equalTo(STEP_ONE));

        assertThat(featureDetails.getBackgroundSteps().get(1).getKeyword(), equalTo(AND));
        assertThat(featureDetails.getBackgroundSteps().get(1).getName(), equalTo(STEP_TWO));
    }

    @Test
    public void shoulParseScenario() {
        // When
        final FeatureDetails featureDetails = FeatureFormatter.parse(feature, PATH_TO_FEATURE);

        // Then
        assertThat(featureDetails.getScenarios().size(), equalTo(1));

        final List<ScenarioDetails> scenarios = featureDetails.getScenarios();
        final Scenario scenario = (Scenario) scenarios.iterator().next().getScenario();
        assertThat(scenario.getName(), equalTo(SCENARIO_NAME));
    }

    @Test
    public void shoulParseSteps() {
        // When
        final FeatureDetails featureDetails = FeatureFormatter.parse(feature, PATH_TO_FEATURE);

        // Then
        final List<ScenarioDetails> scenarios = featureDetails.getScenarios();
        final List<Step> steps = scenarios.iterator().next().getSteps();
        assertThat(steps.get(0).getKeyword(), equalTo(GIVEN));
        assertThat(steps.get(0).getName(), equalTo(STEP_THREE));

        assertThat(steps.get(1).getKeyword(), equalTo(AND));
        assertThat(steps.get(1).getName(), equalTo(STEP_FOUR));

        assertThat(steps.get(2).getKeyword(), equalTo(WHEN));
        assertThat(steps.get(2).getName(), equalTo(STEP_FIVE));

        assertThat(steps.get(3).getKeyword(), equalTo(THEN));
        assertThat(steps.get(3).getName(), equalTo(STEP_SIX));
    }

    @Test(expected = InvalidFeature.class)
    public void shouldFailOnNullFeature() {
        // When
        FeatureFormatter.parse(null, PATH_TO_FEATURE);
    }

    @Test(expected = InvalidFeature.class)
    public void shouldFailOnEmptyFeature() {
        // When
        FeatureFormatter.parse("", PATH_TO_FEATURE);
    }

    @Test(expected = LexingError.class)
    public void shouldFailOnInvalidFeature() {
        // When
        FeatureFormatter.parse("this is not a bdd feature", PATH_TO_FEATURE);
    }

    @Test(expected = InvalidFeatureUri.class)
    public void shouldFailOnNullUri() {
        // When
        FeatureFormatter.parse(feature, null);
    }

    @Test(expected = InvalidFeatureUri.class)
    public void shouldFailOnEmptyUri() {
        // When
        FeatureFormatter.parse(feature, "");
    }
}

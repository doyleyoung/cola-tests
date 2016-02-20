package com.github.bmsantos.core.cola.formatter;

import com.github.bmsantos.core.cola.exceptions.InvalidFeature;
import gherkin.formatter.model.Background;
import gherkin.formatter.model.Feature;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.Step;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FeatureValidatorTest {

    private static final String URI = "uri";
    public static final String NO_STEP_EXCEPTION = "uri - Cause: No steps found for Feature: Foo - Scenario: should Baa";
    public static final String NO_SCENARIO_EXCEPTION = "uri - Cause: No scenarios found for Feature: Foo";
    public static final String NO_BACKGROUND_STEPS = "uri - Cause: No background steps for Feature: Foo - Background: Boo";

    @Rule
    public ExpectedException exception = none();
    @Mock
    private Feature feature;
    @Mock
    private Background background;
    @Mock
    private Scenario scenario;
    @Mock
    private Step step;

    private ScenarioDetails scenarioDetails;
    private FeatureDetails featureDetails = new FeatureDetails(URI);

    @Before
    public void setUp() {
        initMocks(this);

        when(feature.getName()).thenReturn("Foo");
        when(background.getName()).thenReturn("Boo");
        when(scenario.getName()).thenReturn("should Baa");

        scenarioDetails = new ScenarioDetails(scenario);
        scenarioDetails.getSteps().add(step);
        featureDetails.setFeature(feature);
        featureDetails.getScenarios().add(scenarioDetails);
    }

    @Test
    public void shouldRaiseExceptionWhenScenarioHasNoSteps() {
        // Given
        scenarioDetails.getSteps().clear();
        exception.expect(InvalidFeature.class);
        exception.expectMessage(NO_STEP_EXCEPTION);

        // When
        FeatureValidator.validate(featureDetails, URI);
    }

    @Test
    public void shouldRaiseExceptionWhenFeatureHasNoScenarios() {
        // Given
        featureDetails.getScenarios().clear();
        exception.expect(InvalidFeature.class);
        exception.expectMessage(NO_SCENARIO_EXCEPTION);

        // When
        FeatureValidator.validate(featureDetails, URI);
    }

    @Test
    public void shouldRaiseExceptionWhenBackgroundHasNoSteps() {
        // Given
        featureDetails.setBackground(background);
        exception.expect(InvalidFeature.class);
        exception.expectMessage(NO_BACKGROUND_STEPS);

        // When
        FeatureValidator.validate(featureDetails, URI);
    }

    @Test
    public void shouldPassValidation() {
        // When
        final FeatureDetails result = FeatureValidator.validate(featureDetails, URI);

        // Then
        assertThat(result, is(featureDetails));
    }
}
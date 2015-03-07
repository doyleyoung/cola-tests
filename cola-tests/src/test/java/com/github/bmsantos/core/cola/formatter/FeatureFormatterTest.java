package com.github.bmsantos.core.cola.formatter;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import gherkin.formatter.model.Comment;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.Step;
import gherkin.formatter.model.Tag;
import gherkin.formatter.model.TagStatement;
import gherkin.lexer.LexingError;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.github.bmsantos.core.cola.exceptions.InvalidFeature;
import com.github.bmsantos.core.cola.exceptions.InvalidFeatureUri;

public class FeatureFormatterTest {

    private static final String CR = "\n";
    private static final String TAG1 = "@skip";
    private static final String TAG2 = "@catalog";
    private static final String COMMENT1 = "#Comment 1";
    private static final String COMMENT2 = "# Comment 2";
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
        COMMENT1 + CR
        + COMMENT2 + CR
        + TAG1 + " " + TAG2 + CR
        + "Feature: " + FEATURE_NAME + CR
        + "Background: " + BACKGROUND_NAME + CR
        + GIVEN + STEP_ONE + CR
        + AND + STEP_TWO + CR
        + CR
        + COMMENT1 + CR
        + COMMENT2 + CR
        + TAG1 + " " + TAG2 + CR
        + "Scenario: " + SCENARIO_NAME + CR
        + GIVEN + STEP_THREE + CR
        + AND + STEP_FOUR + CR
        + WHEN + STEP_FIVE + CR
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

    @Test
    public void shouldParseFeatureTags() {
        // When
        final FeatureDetails featureDetails = FeatureFormatter.parse(feature, PATH_TO_FEATURE);

        // Then
        final List<String> tags = getTags(featureDetails.getFeature());
        assertThat(tags, contains(TAG1, TAG2));
    }

    @Test
    public void shouldParseScenarioTags() {
        // When
        final FeatureDetails featureDetails = FeatureFormatter.parse(feature, PATH_TO_FEATURE);

        // Then
        final List<String> tags = getTags(featureDetails.getScenarios().get(0).getScenario());
        assertThat(tags, contains(TAG1, TAG2));
    }

    @Test
    public void shouldParseFeatureComments() {
        // When
        final FeatureDetails featureDetails = FeatureFormatter.parse(feature, PATH_TO_FEATURE);

        // Then
        final List<String> comments = getComments(featureDetails.getFeature());
        assertThat(comments, contains(COMMENT1, COMMENT2));
    }

    @Test
    public void shouldParseScenarioComments() {
        // When
        final FeatureDetails featureDetails = FeatureFormatter.parse(feature, PATH_TO_FEATURE);

        // Then
        final List<String> comments = getComments(featureDetails.getScenarios().get(0).getScenario());
        assertThat(comments, contains(COMMENT1, COMMENT2));
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

    private final List<String> getComments(final TagStatement tagStatement) {
        final List<String> comments = new ArrayList<>();
        for (final Comment comment : tagStatement.getComments()) {
            comments.add(comment.getValue());
        }
        return comments;
    }

    private final List<String> getTags(final TagStatement tagStatement) {
        final List<String> tags = new ArrayList<>();
        for (final Tag tag : tagStatement.getTags()) {
            tags.add(tag.getName());
        }
        return tags;
    }
}

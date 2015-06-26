package com.github.bmsantos.core.cola.filters;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import test.utils.TestUtils;

import com.github.bmsantos.core.cola.formatter.FeatureDetails;
import com.github.bmsantos.core.cola.story.annotations.Feature;

public class TagFilterTest {

    private TagFilter uut;

    @Before
    public void setUp() {
        uut = new TagFilter();
    }

    @Test
    public void shouldFilterFeaturesWithSkipTag() throws IOException {
        // Given
        final FeatureDetails feature = TestUtils.loadFeatures("com.github.bmsantos.core.cola.filters.TagFilterTest$SkipFeatureClass").get(0);

        // When
        final boolean result = uut.filtrate(feature);

        // Then
        assertThat(result, is(true));
    }

    @Test
    public void shouldFilterFeaturesWithSkipTagAndSingleScenario() throws IOException {
        // Given
        final FeatureDetails feature = TestUtils.loadFeatures("com.github.bmsantos.core.cola.filters.TagFilterTest$SkipFeatureWithSingleScenarioClass").get(0);

        // When
        final boolean result = uut.filtrate(feature);

        // Then
        assertThat(result, is(true));
    }

    @Test
    public void shouldKeepFeature() throws IOException {
        // Given
        final FeatureDetails feature = TestUtils.loadFeatures("com.github.bmsantos.core.cola.filters.TagFilterTest$NormalFeatureClass").get(0);

        // When
        final boolean result = uut.filtrate(feature);

        // Then
        assertThat(result, is(false));
    }

    private static class SkipFeatureClass {
        @Feature
        private final String skippedFeature =
        "@skip\n"
            + "Feature: Load feature\n"
            + "Scenario: Should have scenario steps\n"
            + "Given A\n"
            + "When B\n"
            + "Then C\n";
    }

    private static class SkipFeatureWithSingleScenarioClass {
        @Feature
        private final String skippedScenario =
        "Feature: Load feature\n"
            + "@skip\n"
            + "Scenario: Should have scenario steps\n"
            + "Given A\n"
            + "When B\n"
            + "Then C\n";
    }

    private static class NormalFeatureClass {
        @Feature
        private final String normalFeature =
        "Feature: Load feature\n"
            + "Scenario: Should have scenario steps\n"
            + "Given A\n"
            + "When B\n"
            + "Then C\n";
    }
}

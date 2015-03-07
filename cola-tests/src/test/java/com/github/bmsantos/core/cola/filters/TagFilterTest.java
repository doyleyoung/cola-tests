package com.github.bmsantos.core.cola.filters;

import static com.github.bmsantos.core.cola.filters.TagFilter.filterTags;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import com.github.bmsantos.core.cola.formatter.FeatureDetails;
import com.github.bmsantos.core.cola.formatter.FeaturesLoader;
import com.github.bmsantos.core.cola.story.annotations.Feature;

public class TagFilterTest {

    @Test
    public void shouldFilterFeaturesWithSkipTag() {
        final List<FeatureDetails> features = FeaturesLoader.loadFeaturesFrom(SkipFeatureClass.class);

        // When
        filterTags(features);

        // Then
        assertThat(features.size(), is(0));
    }

    @Test
    public void shouldFilterFeaturesWithSingleScenarioAndSkipTag() {
        final List<FeatureDetails> features = FeaturesLoader.loadFeaturesFrom(SkipFeatureWithSingleScenarioClass.class);

        // When
        filterTags(features);

        // Then
        assertThat(features.size(), is(0));
    }

    @Test
    public void shouldKeepFeature() {
        final List<FeatureDetails> features = FeaturesLoader.loadFeaturesFrom(NormalFeatureClass.class);

        // When
        filterTags(features);

        // Then
        assertThat(features.size(), is(1));
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

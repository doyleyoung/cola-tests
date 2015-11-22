package com.github.bmsantos.core.cola.filters;

import com.github.bmsantos.core.cola.formatter.FeatureDetails;
import com.github.bmsantos.core.cola.story.annotations.Feature;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static java.lang.System.getProperties;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static test.utils.TestUtils.loadFeatures;

public class TagFilterTest {

    private TagFilter uut;

    @Before
    public void setUp() {
        uut = new TagFilter();
    }

    @After
    public void tearDown() {
        getProperties().remove("cola.group");
        getProperties().remove("~cola.group");
    }

    @Test
    public void shouldFilterFeaturesWithSkipTag() throws IOException {
        // Given
        final FeatureDetails feature = loadFeatures("com.github.bmsantos.core.cola.filters.TagFilterTest$SkipFeatureClass").get(0);

        // When
        final boolean result = uut.filtrate(feature);

        // Then
        assertThat(result, is(true));
    }

    @Test
    public void shouldFilterFeaturesWithSkipTagAndSingleScenario() throws IOException {
        // Given
        final FeatureDetails feature = loadFeatures("com.github.bmsantos.core.cola.filters.TagFilterTest$SkipFeatureWithSingleScenarioClass").get(0);

        // When
        final boolean result = uut.filtrate(feature);

        // Then
        assertThat(result, is(true));
    }

    @Test
    public void shouldKeepFeature() throws IOException {
        // Given
        final FeatureDetails feature = loadFeatures("com.github.bmsantos.core.cola.filters.TagFilterTest$NormalFeatureClass").get(0);

        // When
        final boolean result = uut.filtrate(feature);

        // Then
        assertThat(result, is(false));
    }

    @Test
    public void shouldGroupByFeature() throws IOException {
        // Given
        getProperties().setProperty("cola.group", "group");
        uut = new TagFilter();
        final FeatureDetails feature = loadFeatures("com.github.bmsantos.core.cola.filters.TagFilterTest$GroupFeatureClass").get(0);

        // When
        final boolean result = uut.filtrate(feature);

        // Then
        assertThat(result, is(false));
    }

    @Test
    public void shouldGroupByScenario() throws IOException {
        // Given
        getProperties().setProperty("cola.group", "group");
        uut = new TagFilter();
        final FeatureDetails feature = loadFeatures("com.github.bmsantos.core.cola.filters.TagFilterTest$GroupScenarioClass").get(0);

        // When
        final boolean result = uut.filtrate(feature);

        // Then
        assertThat(result, is(false));
    }

    @Test
    public void shouldExcludeGroupByFeature() throws IOException {
        // Given
        getProperties().setProperty("~cola.group", "group");
        uut = new TagFilter();
        final FeatureDetails feature = loadFeatures("com.github.bmsantos.core.cola.filters.TagFilterTest$GroupFeatureClass").get(0);

        // When
        final boolean result = uut.filtrate(feature);

        // Then
        assertThat(result, is(true));
    }

    @Test
    public void shouldExcludeGroupByScenario() throws IOException {
        // Given
        getProperties().setProperty("~cola.group", "group");
        uut = new TagFilter();
        final FeatureDetails feature = loadFeatures("com.github.bmsantos.core.cola.filters.TagFilterTest$GroupScenarioClass").get(0);

        // When
        final boolean result = uut.filtrate(feature);

        // Then
        assertThat(result, is(true));
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

    private static class GroupFeatureClass {
        @Feature
        private final String groupFeature =
          "@group\n"
            + "Feature: Load feature\n"
            + "Scenario: Should have scenario steps\n"
            + "Given A\n"
            + "When B\n"
            + "Then C\n";
    }

    private static class GroupScenarioClass {
        @Feature
        private final String groupScenario =
          "Feature: Load feature\n"
            + "@group\n"
            + "Scenario: Should have scenario steps\n"
            + "Given A\n"
            + "When B\n"
            + "Then C\n";
    }
}

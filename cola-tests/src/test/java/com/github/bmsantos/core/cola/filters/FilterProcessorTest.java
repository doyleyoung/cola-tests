package com.github.bmsantos.core.cola.filters;

import static com.github.bmsantos.core.cola.filters.FilterProcessor.filtrate;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.github.bmsantos.core.cola.formatter.FeatureDetails;
import com.github.bmsantos.core.cola.formatter.FeaturesLoader;
import com.github.bmsantos.core.cola.formatter.TagStatementDetails;
import com.github.bmsantos.core.cola.story.annotations.Feature;

public class FilterProcessorTest {

    private Filter filter;

    @Before
    public void setUp() {
        filter = Mockito.mock(Filter.class);
    }

    @Test
    public void shouldFilterOutFeature() {
        // Given
        final List<FeatureDetails> features = FeaturesLoader.loadFeaturesFrom(SkipFeatureClass.class);
        when(filter.filtrate(any(TagStatementDetails.class))).thenReturn(true);

        // When
        filtrate(features).using(asList(filter));

        // Then
        assertThat(features.isEmpty(), is(true));
    }

    @Test
    public void shouldNotFilterFeature() {
        // Given
        final List<FeatureDetails> features = FeaturesLoader.loadFeaturesFrom(SkipFeatureClass.class);
        when(filter.filtrate(any(TagStatementDetails.class))).thenReturn(false);

        // When
        filtrate(features).using(asList(filter));

        // Then
        assertThat(features.isEmpty(), is(false));
    }

    private static class SkipFeatureClass {
        @Feature
        private final String skippedFeature =
        "Feature: Load feature\n"
            + "Scenario: Should have scenario steps\n"
            + "Given A\n"
            + "When B\n"
            + "Then C\n";
    }
}

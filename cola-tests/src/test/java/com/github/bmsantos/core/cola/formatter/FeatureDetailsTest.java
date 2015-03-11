package com.github.bmsantos.core.cola.formatter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import gherkin.formatter.model.Feature;
import gherkin.formatter.model.Tag;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class FeatureDetailsTest {

    private FeatureDetails uut;

    @Mock
    private Feature feature;

    @Before
    public void setUp() {
        initMocks(this);

        uut = new FeatureDetails("foo/uri");
        uut.setFeature(feature);
    }

    @Test
    public void shouldIgnore() {
        // Given
        final Tag tag = new Tag("@IgNore", 1);
        when(feature.getTags()).thenReturn(Arrays.asList(tag));

        // When
        final boolean result = uut.ignore();

        // Then
        assertThat(result, is(true));
    }

    @Test
    public void shouldNotIgnore() {
        // Given
        final Tag tag = new Tag("@OtheR", 1);
        when(feature.getTags()).thenReturn(Arrays.asList(tag));

        // When
        final boolean result = uut.ignore();

        // Then
        assertThat(result, is(false));
    }
}

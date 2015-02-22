package com.github.bmsantos.core.cola.formatter;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import com.github.bmsantos.core.cola.exceptions.InvalidFeature;
import com.github.bmsantos.core.cola.exceptions.InvalidFeatureUri;
import com.github.bmsantos.core.cola.formatter.FeatureDetails;
import com.github.bmsantos.core.cola.formatter.FeaturesLoader;
import com.github.bmsantos.core.cola.story.annotations.Feature;
import com.github.bmsantos.core.cola.story.annotations.Features;

public class FeaturesLoaderTest {

    private static final String FEATURE1_NAME = "Load feature";
    private static final String FEATURE2_NAME = "Load gherkin feature";

    @Test
    public void shouldLoadFeatureFromAnnotatedClass() {
        // When
        final List<FeatureDetails> features = FeaturesLoader.loadFeaturesFrom(AnnotatedClass.class);

        // Then
        assertThat(features.size(), equalTo(1));

        final FeatureDetails result = features.get(0);
        assertThat(result.getFeature().getName(), equalTo(FEATURE1_NAME));
    }

    @Test
    public void shouldLoadMultiFeaturesFromAnnotatedClass() {
        // When
        final List<FeatureDetails> features = FeaturesLoader.loadFeaturesFrom(MultiFeatureAnnotatedClass.class);

        // Then
        assertThat(features.size(), equalTo(2));

        final FeatureDetails feature1 = features.get(0);
        assertThat(feature1.getFeature().getName(), equalTo(FEATURE1_NAME));

        final FeatureDetails feature2 = features.get(1);
        assertThat(feature2.getFeature().getName(), equalTo(FEATURE2_NAME));
    }

    @Test(expected = InvalidFeatureUri.class)
    public void shouldFailToLoadInvalidAnnotatedClass() {
        // When
        FeaturesLoader.loadFeaturesFrom(InvalidFeatureAnnotatedClass.class);
    }

    @Test(expected = InvalidFeature.class)
    public void shouldFailToLoadEmptyFeatureAnnotatedClass() {
        // When
        FeaturesLoader.loadFeaturesFrom(EmptyFeatureAnnotatedClass.class);
    }

    @Test
    public void shouldLoadFieldAnnotatedClass() {
        // When
        final List<FeatureDetails> features = FeaturesLoader.loadFeaturesFrom(FieldAnnotatedClass.class);

        // Then
        assertThat(features.size(), equalTo(1));

        final FeatureDetails result = features.get(0);
        assertThat(result.getFeature().getName(), equalTo(FEATURE1_NAME));
    }

    @Test
    public void shouldLoadMultiFieldAnnotatedClass() {
        // When
        final List<FeatureDetails> features = FeaturesLoader.loadFeaturesFrom(MultiFieldAnnotatedClass.class);

        // Then
        assertThat(features.size(), equalTo(2));

        final FeatureDetails feature1 = features.get(0);
        assertThat(feature1.getFeature().getName(), equalTo(FEATURE1_NAME));

        final FeatureDetails feature2 = features.get(1);
        assertThat(feature2.getFeature().getName(), equalTo(FEATURE2_NAME));
    }

    @Test(expected = InvalidFeature.class)
    public void shouldFailToLoadInvalidFieldAnnotatedClass() {
        // When
        FeaturesLoader.loadFeaturesFrom(InvalidFieldAnnotatedClass.class);
    }

    @Test(expected = InvalidFeature.class)
    public void shouldFailToLoadEmptyFieldAnnotatedClass() {
        // When
        FeaturesLoader.loadFeaturesFrom(EmptyFieldAnnotatedClass.class);
    }

    @Features("annotated_class1")
    private class AnnotatedClass {
    }

    @Features({"annotated_class1", "annotated_class2"})
    private class MultiFeatureAnnotatedClass {
    }

    @Features("invalid_feature")
    private class InvalidFeatureAnnotatedClass {
    }

    @Features("empty_feature")
    private class EmptyFeatureAnnotatedClass {
    }

    private static class FieldAnnotatedClass {
        @Feature
        private final String annotatedField =
        "Feature: Load feature\n"
            + "Scenario: Should have scenario steps\n"
            + "Given A\n"
            + "When B\n"
            + "Then C\n";
    }

    private static class MultiFieldAnnotatedClass {

        @Feature
        private final String annotatedField1 =
        "Feature: Load feature\n"
            + "Scenario: Should have scenario steps\n"
            + "Given A\n"
            + "When B\n"
            + "Then C\n";

        @Feature
        private final String annotatedField2 =
        "Feature: Load gherkin feature\n"
            + "Scenario: Should have scenario steps\n"
            + "Given A\n"
            + "When B\n"
            + "Then C\n";
    }

    private static class InvalidFieldAnnotatedClass {
        @Feature
        private final String annotatedField = "This is invalid feature";
    }

    private static class EmptyFieldAnnotatedClass {
        @Feature
        private final String annotatedField = "";
    }
}

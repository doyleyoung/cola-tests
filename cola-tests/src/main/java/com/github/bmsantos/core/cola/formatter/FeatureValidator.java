package com.github.bmsantos.core.cola.formatter;

import com.github.bmsantos.core.cola.exceptions.InvalidFeature;

import static com.github.bmsantos.core.cola.utils.ColaUtils.isSet;

public class FeatureValidator {

    private FeatureValidator() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation");
    }

    public static FeatureDetails validate(final FeatureDetails feature, final String fromUri) {
        validateFeature(feature, fromUri);
        return feature;
    }

    private static void validateFeature(final FeatureDetails feature, final String fromUri) {
        final String name = feature.getFeature().getName();

        if (isSet(feature.getBackground())) {
            if (feature.getBackgroundSteps().isEmpty()) {
                throw new InvalidFeature(fromUri + " - Cause: No background steps for Feature: " + name
                  + " - Background: " + feature.getBackground().getName());
            }
        }

        if (feature.getScenarios().isEmpty()) {
            throw new InvalidFeature(fromUri + " - Cause: No scenarios found for Feature: " + name);
        }

        for (final ScenarioDetails scenario: feature.getScenarios()) {
            validateScenario(name, scenario, fromUri);
        }
    }

    private static void validateScenario(final String feature, final ScenarioDetails scenario, final String fromUri) {
        if (scenario.getSteps().isEmpty()) {
            throw new InvalidFeature(fromUri + " - Cause: No steps found for Feature: " + feature
              + " - Scenario: " + scenario.getScenario().getName());
        }
    }
}
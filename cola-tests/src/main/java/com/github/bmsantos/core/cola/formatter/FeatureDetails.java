package com.github.bmsantos.core.cola.formatter;

import gherkin.formatter.model.Background;
import gherkin.formatter.model.Feature;
import gherkin.formatter.model.Step;

import java.util.ArrayList;
import java.util.List;

public class FeatureDetails {

    private final String uri;
    private Feature feature;
    private Background background;
    private final List<Step> backgroundSteps = new ArrayList<>();
    private final List<ScenarioDetails> scenarios = new ArrayList<>();

    public FeatureDetails(final String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public Feature getFeature() {
        return feature;
    }

    public void setFeature(final Feature feature) {
        this.feature = feature;
    }

    public Background getBackground() {
        return background;
    }

    public void setBackground(final Background background) {
        this.background = background;
    }

    public List<Step> getBackgroundSteps() {
        return backgroundSteps;
    }

    public List<ScenarioDetails> getScenarios() {
        return scenarios;
    }
}

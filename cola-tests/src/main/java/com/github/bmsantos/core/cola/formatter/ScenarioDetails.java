package com.github.bmsantos.core.cola.formatter;

import gherkin.formatter.model.Examples;
import gherkin.formatter.model.Step;
import gherkin.formatter.model.TagStatement;

import java.util.ArrayList;
import java.util.List;

public class ScenarioDetails {

    private final TagStatement scenario;
    private final List<Step> steps = new ArrayList<>();
    private ProjectionValues projectionValues;

    public ScenarioDetails(final TagStatement scenario) {
        this.scenario = scenario;
    }

    public TagStatement getScenario() {
        return scenario;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public boolean hasProjectionValues() {
        return projectionValues != null && projectionValues.canDoProjections();
    }

    public ProjectionValues getProjectionValues() {
        return projectionValues;
    }

    public void setExamples(final Examples examples) {
        projectionValues = new ProjectionValues(examples);
    }
}

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

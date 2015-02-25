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

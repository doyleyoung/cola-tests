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

import gherkin.deps.com.google.gson.Gson;
import gherkin.formatter.model.Examples;
import gherkin.formatter.model.ExamplesTableRow;

import java.util.HashMap;
import java.util.Map;

public class ProjectionValues {

    private final Examples examples;

    public ProjectionValues(final Examples examples) {
        this.examples = examples;
    }

    public int size() {
        return examples.getRows().size() - 1;
    }

    public boolean canDoProjections() {
        return examples.getRows().size() > 1;
    }

    public String doRowProjection(final int index) {
        final Map<String, String> values = new HashMap<>();

        final ExamplesTableRow header = examples.getRows().get(0);
        final ExamplesTableRow row = examples.getRows().get(index + 1);

        for (int i = 0; i < header.getCells().size(); i++) {
            values.put(header.getCells().get(i), row.getCells().get(i));
        }

        return new Gson().toJson(values);
    }
}

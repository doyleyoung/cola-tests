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

package com.github.bmsantos.core.cola.filters;

import static com.github.bmsantos.core.cola.utils.ColaUtils.isSet;
import gherkin.formatter.model.Tag;

import java.util.Iterator;
import java.util.List;

import com.github.bmsantos.core.cola.formatter.FeatureDetails;
import com.github.bmsantos.core.cola.formatter.ScenarioDetails;
import com.github.bmsantos.core.cola.formatter.TagStatementDetails;

public class TagFilter implements Filter {

    private static final String SKIP = "@skip";

    @Override
    public boolean filtrate(final TagStatementDetails statement) {
        if (statement instanceof FeatureDetails) {
            return filterFeature((FeatureDetails) statement);
        }
        return filterScenario((ScenarioDetails)statement);
    }

    private boolean filterFeature(final FeatureDetails feature) {
        if (skipped(feature.getFeature().getTags())) {
            return true;
        }

        final Iterator<ScenarioDetails> it = feature.getScenarios().iterator();
        while (it.hasNext()) {
            if (filterScenario(it.next())) {
                it.remove();
            }
        }

        return feature.getScenarios().isEmpty();
    }

    private boolean filterScenario(final ScenarioDetails scenario) {
        return skipped(scenario.getScenario().getTags());
    }

    private boolean skipped(final List<Tag> tags) {
        if (isSet(tags)) {
            for (final Tag tag : tags) {
                if (tag.getName().toLowerCase().equals(SKIP)) {
                    return true;
                }
            }
        }
        return false;
    }
}

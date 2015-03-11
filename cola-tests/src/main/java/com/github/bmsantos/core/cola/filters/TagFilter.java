package com.github.bmsantos.core.cola.filters;

import static com.github.bmsantos.core.cola.utils.ColaUtils.isSet;
import gherkin.formatter.model.Tag;

import java.util.Iterator;
import java.util.List;

import com.github.bmsantos.core.cola.formatter.FeatureDetails;
import com.github.bmsantos.core.cola.formatter.ScenarioDetails;

public class TagFilter {

    private static final String SKIP = "@skip";

    public static List<FeatureDetails> filterTags(final List<FeatureDetails> features) {
        final Iterator<FeatureDetails> i = features.iterator();
        while (i.hasNext()) {
            final FeatureDetails feature = i.next();
            if (!filterOnSkip(i, feature.getFeature().getTags())) {
                final Iterator<ScenarioDetails> s = feature.getScenarios().iterator();
                while (s.hasNext()) {
                    final ScenarioDetails scenario = s.next();
                    filterOnSkip(s, scenario.getScenario().getTags());
                }
                if (feature.getScenarios().isEmpty()) {
                    i.remove();
                }
            }
        }
        return features;
    }

    private static boolean filterOnSkip(final Iterator<?> it, final List<Tag> tags) {
        if (isSet(tags)) {
            for (final Tag tag : tags) {
                if (tag.getName().toLowerCase().equals(SKIP)) {
                    it.remove();
                    return true;
                }
            }
        }
        return false;
    }
}

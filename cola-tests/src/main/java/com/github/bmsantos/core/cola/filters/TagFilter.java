package com.github.bmsantos.core.cola.filters;

import com.github.bmsantos.core.cola.formatter.FeatureDetails;
import com.github.bmsantos.core.cola.formatter.ScenarioDetails;
import com.github.bmsantos.core.cola.formatter.TagStatementDetails;
import gherkin.formatter.model.Tag;

import java.util.Iterator;
import java.util.List;

import static com.github.bmsantos.core.cola.utils.ColaUtils.isSet;
import static java.lang.System.getProperties;
import static java.lang.System.getProperty;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

public class TagFilter implements Filter {

    private static final String COLA_TAGS = "cola.group";
    private static final String COLA_TAGS_EXCLUDED = "~" + COLA_TAGS;
    private static final String SKIP = "@skip";
    private List<String> colaTags = emptyList();
    private List<String> excludedColaTags = emptyList();
    private boolean isGroupedExecution = false;

    public TagFilter() {
        if (getProperties().containsKey(COLA_TAGS)) {
            colaTags = asList(getProperty(COLA_TAGS).split(","));
            isGroupedExecution = true;
        }
        if (getProperties().containsKey(COLA_TAGS_EXCLUDED)) {
            excludedColaTags = asList(getProperty(COLA_TAGS_EXCLUDED).split(","));
        }
    }

    @Override
    public boolean filtrate(final TagStatementDetails statement) {
        if (statement instanceof FeatureDetails) {
            return filterFeature((FeatureDetails) statement);
        }

        final ScenarioDetails scenario = (ScenarioDetails) statement;
        return isSkippedScenario(scenario) || (isGroupedExecution && !isScenarioInGroup(scenario));
    }

    private boolean filterFeature(final FeatureDetails feature) {
        if (skipped(feature.getFeature().getTags())) {
            return true;
        }

        final Iterator<ScenarioDetails> it = feature.getScenarios().iterator();
        while (it.hasNext()) {
            final ScenarioDetails next = it.next();
            if (isSkippedScenario(next) || (isGroupedExecution && !isScenarioGrouped(feature, next))) {
                it.remove();
            }
        }

        return feature.getScenarios().isEmpty();
    }

    private boolean isScenarioGrouped(final FeatureDetails feature, final ScenarioDetails scenario) {
        return isFeatureInGroup(feature) || isScenarioInGroup(scenario);
    }

    private boolean isFeatureInGroup(final FeatureDetails feature) {
        return isGrouped(feature.getFeature().getTags());
    }

    private boolean isScenarioInGroup(final ScenarioDetails scenario) {
        return isGrouped(scenario.getScenario().getTags());
    }

    private boolean isScenarioInExcludedGroup(final ScenarioDetails scenario) {
        return isGroupExcluded(scenario.getScenario().getTags());
    }

    private boolean isSkippedScenario(final ScenarioDetails scenario) {
        return skipped(scenario.getScenario().getTags()) || isScenarioInExcludedGroup(scenario);
    }

    private boolean skipped(final List<Tag> tags) {
        if (isSet(tags)) {
            for (final Tag tag : tags) {
                if (tag.getName().toLowerCase().equals(SKIP) || excludedColaTags.contains(tag.getName().substring(1))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isGrouped(final List<Tag> tags) {
        if (isSet(tags)) {
            for (final Tag tag : tags) {
                if (colaTags.contains(tag.getName().substring(1))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isGroupExcluded(final List<Tag> tags) {
        if (isSet(tags)) {
            for (final Tag tag : tags) {
                if (excludedColaTags.contains(tag.getName().substring(1))) {
                    return true;
                }
            }
        }
        return false;
    }
}
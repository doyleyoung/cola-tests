package com.github.bmsantos.core.cola.filters;

import static com.github.bmsantos.core.cola.utils.ColaUtils.isSet;
import gherkin.formatter.model.Comment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.bmsantos.core.cola.formatter.FeatureDetails;
import com.github.bmsantos.core.cola.formatter.ReportDetails;
import com.github.bmsantos.core.cola.formatter.ScenarioDetails;
import com.github.bmsantos.core.cola.formatter.TagStatementDetails;

public class ReportFilter implements Filter {
    private final Pattern pattern = Pattern.compile("^#(\\s?[\\w\\-]+)>(.*)$");

    @Override
    public boolean filtrate(final TagStatementDetails statement) {
        if (statement instanceof FeatureDetails) {
            return filterFeature((FeatureDetails) statement);
        }
        return filterScenario((ScenarioDetails) statement);
    }

    private boolean filterFeature(final FeatureDetails feature) {
        feature.setReports(processComments(feature.getFeature().getComments()));

        final Iterator<ScenarioDetails> it = feature.getScenarios().iterator();
        while (it.hasNext()) {
            filterScenario(it.next());
        }

        return false;
    }

    private boolean filterScenario(final ScenarioDetails scenario) {
        scenario.setReports(processComments(scenario.getScenario().getComments()));
        return false;
    }

    private List<ReportDetails> processComments(final List<Comment> comments) {
        final List<ReportDetails> reports = new ArrayList<>();
        if (isSet(comments)) {
            for (final Comment comment : comments) {
                if (isSet(comment.getValue())) {
                    final Matcher matcher = pattern.matcher(comment.getValue().trim());
                    while (matcher.find()) {
                        final ReportDetails reportDetails = new ReportDetails(matcher.group(1).trim(), matcher.group(2).trim());
                        reports.add(reportDetails);
                    }
                }
            }
        }
        return reports;
    }
}

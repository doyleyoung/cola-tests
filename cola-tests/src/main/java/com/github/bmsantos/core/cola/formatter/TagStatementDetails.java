package com.github.bmsantos.core.cola.formatter;

import gherkin.formatter.model.Tag;
import gherkin.formatter.model.TagStatement;

import java.util.List;

public abstract class TagStatementDetails {

    private static final String IGNORE = "@ignore";

    protected TagStatement tagStatement;
    protected List<ReportDetails> reports;

    public TagStatementDetails() {
        // empty
    }

    public TagStatementDetails(final TagStatement tagStatement) {
        this.tagStatement = tagStatement;
    }

    public boolean ignore() {
        for (final Tag tag : tagStatement.getTags()) {
            if (tag.getName().toLowerCase().equals(IGNORE)) {
                return true;
            }
        }
        return false;
    }

    public void setReports(final List<ReportDetails> reports) {
        this.reports = reports;
    }

    public List<ReportDetails> getReports() {
        return reports;
    }
}

package com.github.bmsantos.core.cola.report;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

public enum ReportLoader {
    reportLoader;

    private final Map<String, Report> reports = new HashMap<>();

    public Report get(final String reportName) {
        return getReports().get(reportName);
    }

    public Map<String, Report> getReports() {
        return reports.isEmpty() ? loadReports() : reports;
    }

    private Map<String, Report> loadReports() {
        final Iterator<Report> it = ServiceLoader.load(Report.class).iterator();
        while (it.hasNext()) {
            final Report report = it.next();
            reports.put(report.getName(), report);
        }
        return reports;
    }
}

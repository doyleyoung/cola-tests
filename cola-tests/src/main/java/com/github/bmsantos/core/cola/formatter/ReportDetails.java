package com.github.bmsantos.core.cola.formatter;

public class ReportDetails {
    private final String report;
    private final String arguments;

    public ReportDetails(final String report, final String arguments) {
        this.report = report;
        this.arguments = "kind:" + report + " " + arguments;
    }

    public String getReport() {
        return report;
    }

    public String getArguments() {
        return arguments;
    }
}
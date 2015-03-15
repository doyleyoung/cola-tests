package com.github.bmsantos.core.cola.report;

public interface Report {
    String getName();

    void report(final String parameters, final Throwable error);
}

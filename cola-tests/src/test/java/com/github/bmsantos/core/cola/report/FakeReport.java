package com.github.bmsantos.core.cola.report;

public class FakeReport implements Report {

    @Override
    public String getName() {
        return "fake";
    }

    @Override
    public void report(final String parameters, final Throwable error) {
        // empty
    }

}

package com.github.bmsantos.core.cola.report;

import static com.github.bmsantos.core.cola.report.ReportLoader.reportLoader;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ReportLoaderTest {
    private static final String COLA_REPORT = "report";

    @Test
    public void shouldLoadReports() {
        assertThat(reportLoader.get(COLA_REPORT), notNullValue());
    }
}

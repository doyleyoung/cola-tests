package com.github.bmsantos.core.cola.story.processor;

import static com.github.bmsantos.core.cola.report.ReportLoader.reportLoader;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import gherkin.deps.com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.ComparisonFailure;
import org.junit.Test;

import com.github.bmsantos.core.cola.formatter.ReportDetails;
import com.github.bmsantos.core.cola.report.Report;
import com.github.bmsantos.core.cola.story.annotations.Given;
import com.github.bmsantos.core.cola.story.annotations.Then;
import com.github.bmsantos.core.cola.story.annotations.When;

public class StoryProcessorAnnotationTest {

    private final String story =
        "Given a first method\n"
            + "And a second method\n"
            + "When the first method is called\n"
            + "And the second method is called\n"
            + "Then the first method will execute\n"
            + "But the second method will execute";

    private final String regexStory =
        "Given Beta\n"
            + "When 101 is inserted\n"
            + "Then the true real method will execute";

    private final String exceptionStory =
        "Given a first method\n"
            + "And a second method\n"
            + "When the first method is called\n"
            + "And the second method is called\n"
            + "Then the first method will execute\n"
            + "But the second method assertion will throw an exception";

    private final String noReport = "";

    private TestClass instance;
    private final String projectionValues = "";

    @Before
    public void setUp() {
        instance  = new TestClass();
    }

    @Test
    public void shouldProcessGiven() throws Throwable {
        // When
        StoryProcessor.process("Feature: I'm a feature", "Scenario: Should Process Story", story, projectionValues,
            noReport, instance);

        // Then
        assertThat(instance.wasGivenCalled, equalTo(true));
    }

    @Test
    public void shouldProcessGivenAnd() throws Throwable {
        // When
        StoryProcessor.process("Feature: I'm a feature", "Scenario: Should Process Story", story, projectionValues,
            noReport, instance);

        // Then
        assertThat(instance.wasGivenAndCalled, equalTo(true));
    }

    @Test
    public void shouldProcessWhen() throws Throwable {
        // When
        StoryProcessor.process("Feature: I'm a feature", "Scenario: Should Process Story", story, projectionValues,
            noReport, instance);

        // Then
        assertThat(instance.wasWhenCalled, equalTo(true));
    }

    @Test
    public void shouldProcessWhenAnd() throws Throwable {
        // When
        StoryProcessor.process("Feature: I'm a feature", "Scenario: Should Process Story", story, projectionValues,
            noReport, instance);

        // Then
        assertThat(instance.wasWhenAndCalled, equalTo(true));
    }

    @Test
    public void shouldProcessThen() throws Throwable {
        // When
        StoryProcessor.process("Feature: I'm a feature", "Scenario: Should Process Story", story, projectionValues,
            noReport, instance);

        // Then
        assertThat(instance.wasThenCalled, equalTo(true));
    }

    @Test
    public void shouldProcessThenAnd() throws Throwable {
        // When
        StoryProcessor.process("Feature: I'm a feature", "Scenario: Should Process Story", story, projectionValues,
            noReport, instance);

        // Then
        assertThat(instance.wasThenAndCalled, equalTo(true));
    }

    @Test
    public void shouldProcessInCorrectOrder() throws Throwable {
        // When
        StoryProcessor.process("Feature: I'm a feature", "Scenario: Should Process Story", story, projectionValues,
            noReport, instance);

        // Then
        assertThat(instance.executionOrder,
            contains("givenFirst", "givenSecond", "whenFirst", "whenSecond", "thenFirst", "thenSecond"));
    }

    @Test
    public void shouldProcessRegularExpressionSteps() throws Throwable {
        // When
        StoryProcessor.process("Feature: I'm a feature", "Scenario: Should Process Story", regexStory,
            projectionValues, noReport, instance);

        // Then
        assertThat(instance.executionOrder, contains("givenAlphaOrBeta", "whenANumberIsInserted", "thenTheRealMethodWillExecute"));
    }

    @Test
    public void shouldProcessReports() throws Throwable {
        // Given
        final String reportName = "report";
        final ReportDetails reportDetails = new ReportDetails(reportName, "arg1 arg2");

        final Report report = mock(Report.class);
        reportLoader.getReports().clear();
        reportLoader.getReports().put(reportName, report);

        // When
        StoryProcessor.process("Feature: I'm a feature", "Scenario: Should Process Story", regexStory,
            projectionValues, new Gson().toJson(asList(reportDetails)), instance);

        // Then
        verify(report).report(reportDetails.getArguments(), null);
    }

    @Test(expected = ComparisonFailure.class)
    public void shouldUnwrapTheException() throws Throwable {
        // When
        StoryProcessor.process("Feature: I'm a feature", "Scenario: Should Process Story", exceptionStory,
            projectionValues, noReport, instance);
    }

    private class TestClass {

        public boolean wasGivenCalled = false;
        public boolean wasGivenAndCalled = false;
        public boolean wasWhenCalled = false;
        public boolean wasWhenAndCalled = false;
        public boolean wasThenCalled = false;
        public boolean wasThenAndCalled = false;
        public List<String> executionOrder = new ArrayList<>();

        @Given("a first method")
        public void givenFirst() {
            wasGivenCalled = true;
            executionOrder.add(Thread.currentThread().getStackTrace()[1].getMethodName());
        }

        @Given("a second method")
        public void givenSecond() {
            wasGivenAndCalled = true;
            executionOrder.add(Thread.currentThread().getStackTrace()[1].getMethodName());
        }

        @Given("Alpha|Beta")
        public void givenAlphaOrBeta() {
            executionOrder.add(Thread.currentThread().getStackTrace()[1].getMethodName());
        }

        @When("the first method is called")
        public void whenFirst() {
            wasWhenCalled = true;
            executionOrder.add(Thread.currentThread().getStackTrace()[1].getMethodName());
        }

        @When("the second method is called")
        public void whenSecond() {
            wasWhenAndCalled = true;
            executionOrder.add(Thread.currentThread().getStackTrace()[1].getMethodName());
        }

        @When("\\d+ is inserted")
        public void whenANumberIsInserted() {
            executionOrder.add(Thread.currentThread().getStackTrace()[1].getMethodName());
        }

        @Then("the first method will execute")
        public void thenFirst() {
            wasThenCalled = true;
            executionOrder.add(Thread.currentThread().getStackTrace()[1].getMethodName());
        }

        @Then("the second method will execute")
        public void thenSecond() {
            wasThenAndCalled = true;
            executionOrder.add(Thread.currentThread().getStackTrace()[1].getMethodName());
        }

        @Then("the (unreal|true real) method will execute")
        public void thenTheRealMethodWillExecute() {
            executionOrder.add(Thread.currentThread().getStackTrace()[1].getMethodName());
        }

        @Then("the second method assertion will throw an exception")
        public void thenThrowAnException() throws Exception {
            throw new ComparisonFailure("message", "0", "1");
        }
    }

}

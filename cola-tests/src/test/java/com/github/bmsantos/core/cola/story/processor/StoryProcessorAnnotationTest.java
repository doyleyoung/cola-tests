package com.github.bmsantos.core.cola.story.processor;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.bmsantos.core.cola.story.annotations.Given;
import com.github.bmsantos.core.cola.story.annotations.Then;
import com.github.bmsantos.core.cola.story.annotations.When;
import com.github.bmsantos.core.cola.story.processor.StoryProcessor;

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

    private TestClass instance;
    private final String projectionValues = "";

    @Before
    public void setUp() {
        instance  = new TestClass();
    }

    @Test
    public void shouldProcessGiven() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        // When
        StoryProcessor.process("Feature: I'm a feature", "Scenario: Should Process Story", story, projectionValues,
            instance);

        // Then
        assertThat(instance.wasGivenCalled, equalTo(true));
    }

    @Test
    public void shouldProcessGivenAnd() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        // When
        StoryProcessor.process("Feature: I'm a feature", "Scenario: Should Process Story", story, projectionValues,
            instance);

        // Then
        assertThat(instance.wasGivenAndCalled, equalTo(true));
    }

    @Test
    public void shouldProcessWhen() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        // When
        StoryProcessor.process("Feature: I'm a feature", "Scenario: Should Process Story", story, projectionValues,
            instance);

        // Then
        assertThat(instance.wasWhenCalled, equalTo(true));
    }

    @Test
    public void shouldProcessWhenAnd() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        // When
        StoryProcessor.process("Feature: I'm a feature", "Scenario: Should Process Story", story, projectionValues,
            instance);

        // Then
        assertThat(instance.wasWhenAndCalled, equalTo(true));
    }

    @Test
    public void shouldProcessThen() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        // When
        StoryProcessor.process("Feature: I'm a feature", "Scenario: Should Process Story", story, projectionValues,
            instance);

        // Then
        assertThat(instance.wasThenCalled, equalTo(true));
    }

    @Test
    public void shouldProcessThenAnd() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        // When
        StoryProcessor.process("Feature: I'm a feature", "Scenario: Should Process Story", story, projectionValues,
            instance);

        // Then
        assertThat(instance.wasThenAndCalled, equalTo(true));
    }

    @Test
    public void shouldProcessInCorrectOrder() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        // When
        StoryProcessor.process("Feature: I'm a feature", "Scenario: Should Process Story", story, projectionValues,
            instance);

        // Then
        assertThat(instance.executionOrder,
            contains("givenFirst", "givenSecond", "whenFirst", "whenSecond", "thenFirst", "thenSecond"));
    }

    @Test
    public void shouldProcessRegularExpressionSteps() throws IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {

        // When
        StoryProcessor.process("Feature: I'm a feature", "Scenario: Should Process Story", regexStory,
            projectionValues, instance);

        // Then
        assertThat(instance.executionOrder, contains("givenAlphaOrBeta", "whenANumberIsInserted", "thenTheRealMethodWillExecute"));
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
    }

}

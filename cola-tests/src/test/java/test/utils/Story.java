package test.utils;

import com.github.bmsantos.core.cola.story.annotations.Then;
import com.github.bmsantos.core.cola.story.annotations.When;
import org.junit.Test;

public class Story extends IdeEnablerTest {
    private final String story = "When A\n" + "Then B\n";

    private final String stories =
      "Feature: Story Processor Invocation\n"
        + "Scenario: Should process story\n"
        + story;

    private boolean calledA = false;
    private boolean calledB = false;

    public Story() {
        init();
    }

    private void init() {
        ++StoryDependsOn.timesCalled;
        ++StoryDependencies.timesCalled;
    }

    @When("A")
    public void whenA() {
        calledA = true;
    }

    @Then("B")
    public void thenB() {
        calledB = true;
    }

    @Test
    public void dependenciesTest() {
        ++StoryDependencies.timesCalled;
    }

    public String getStory() {
        return story;
    }

    public boolean wasCalled() {
        return calledA && calledB;
    }
}
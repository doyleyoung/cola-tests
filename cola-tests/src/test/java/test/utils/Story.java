package test.utils;

import com.github.bmsantos.core.cola.story.annotations.Then;
import com.github.bmsantos.core.cola.story.annotations.When;

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

    protected void init() {
        ++StoryDependsOn.timesCalled;
    }

    @When("A")
    public void whenA() {
        calledA = true;
    }

    @Then("B")
    public void thenB() {
        calledB = true;
    }

    public String getStory() {
        return story;
    }

    public boolean wasCalled() {
        return calledA && calledB;
    }
}